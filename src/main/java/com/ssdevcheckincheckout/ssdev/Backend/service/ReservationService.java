package com.ssdevcheckincheckout.ssdev.Backend.service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.core.StringRedisTemplate;

import org.springframework.stereotype.Service;

import com.ssdevcheckincheckout.ssdev.Backend.dto.ReservationResult;
import com.ssdevcheckincheckout.ssdev.Backend.entity.Session;
import com.ssdevcheckincheckout.ssdev.Backend.repository.SessionRepository;

@Service
public class ReservationService {

    private static final Logger log = LoggerFactory.getLogger(ReservationService.class);
    private static final long DEFAULT_TTL_SECONDS = 300; // 5 minutes

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private SessionRepository sessionRepository;

    // ------------------- RESERVE SLOTS -------------------
    public ReservationResult reserveSlots(Long parentId, Long childId, List<Long> sessionIds, Double totalAmount) {
        String reservationId = generateReservationId();
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(DEFAULT_TTL_SECONDS);

        try {
            // Check session availability
            List<Long> unavailable = new ArrayList<>();
            for (Long sessionId : sessionIds) {
                if (!isSessionAvailable(sessionId)) {
                    unavailable.add(sessionId);
                }
            }

            if (!unavailable.isEmpty()) {
                return ReservationResult.failure("Some sessions are not available", unavailable);
            }

            // Reserve slots atomically
            boolean success = reserveSlotsAtomically(reservationId, parentId, childId, sessionIds, totalAmount, expiresAt);

            if (success) {
                return ReservationResult.success(reservationId, expiresAt, DEFAULT_TTL_SECONDS);
            } else {
                return ReservationResult.failure("Failed to acquire reservation locks", Collections.emptyList());
            }

        } catch (Exception e) {
            log.error("Error during slot reservation", e);
            return ReservationResult.failure("Internal error during reservation", Collections.emptyList());
        }
    }

    private boolean isSessionAvailable(Long sessionId) {
        Optional<Session> sessionOpt = sessionRepository.findById(sessionId);
        if (!sessionOpt.isPresent()) return false;

        Session session = sessionOpt.get();
        int maxCapacity = 36;

        // Get number of currently reserved slots from Redis
        String reservedKey = "session:" + sessionId + ":reserved_count";
        String reservedStr = redisTemplate.opsForValue().get(reservedKey);
        int reservedCount = reservedStr != null ? Integer.parseInt(reservedStr) : 0;

        // Available if booked + reserved < max capacity
        return (session.getBookedCount() + reservedCount) < maxCapacity;
    }

    private boolean reserveSlotsAtomically(String reservationId, Long parentId, Long childId,
                                           List<Long> sessionIds, Double totalAmount, LocalDateTime expiresAt) {
        try {
            for (Long sessionId : sessionIds) {
                String reservedKey = "session:" + sessionId + ":reserved_count";

                // Atomically increment reserved slots if below max capacity
                Boolean success = redisTemplate.opsForValue().setIfAbsent(reservedKey, "1", DEFAULT_TTL_SECONDS, TimeUnit.SECONDS);
                if (success == null || !success) {
                    // Increment existing value safely
                    Long current = redisTemplate.opsForValue().increment(reservedKey);
                    if (current != null && current > 36) { // max capacity
                        redisTemplate.opsForValue().decrement(reservedKey);
                        return false;
                    }
                    redisTemplate.expire(reservedKey, DEFAULT_TTL_SECONDS, TimeUnit.SECONDS);
                }
            }

            // Save reservation details
            String reservationKey = "reservation:" + reservationId;
            String sessionIdsStr = sessionIds.stream().map(String::valueOf).collect(Collectors.joining(","));
            redisTemplate.opsForHash().putAll(reservationKey, Map.of(
                    "parentId", parentId.toString(),
                    "childId", childId.toString(),
                    "sessionIds", sessionIdsStr,
                    "totalAmount", totalAmount.toString(),
                    "expiresAt", expiresAt.toString()
            ));
            redisTemplate.expire(reservationKey, DEFAULT_TTL_SECONDS, TimeUnit.SECONDS);

            return true;

        } catch (Exception e) {
            log.error("Error reserving slots atomically", e);
            return false;
        }
    }

    // ------------------- RELEASE RESERVATION -------------------
    public boolean releaseReservation(String reservationId) {
        try {
            String reservationKey = "reservation:" + reservationId;
            Map<Object, Object> reservationData = redisTemplate.opsForHash().entries(reservationKey);
            if (reservationData.isEmpty()) return false;

            String sessionIdsStr = (String) reservationData.get("sessionIds");
            List<Long> sessionIds = Arrays.stream(sessionIdsStr.split(",")).map(Long::valueOf).collect(Collectors.toList());

            for (Long sessionId : sessionIds) {
                String reservedKey = "session:" + sessionId + ":reserved_count";
                redisTemplate.opsForValue().decrement(reservedKey);
            }

            redisTemplate.delete(reservationKey);
            return true;

        } catch (Exception e) {
            log.error("Error releasing reservation: " + reservationId, e);
            return false;
        }
    }

    // ------------------- OTHER EXISTING METHODS -------------------
    private String generateReservationId() {
        return "res_" + UUID.randomUUID().toString().substring(0, 16);
    }

    public boolean isValidReservation(String reservationId, Long parentId) {
        try {
            String reservationKey = "reservation:" + reservationId;
            String storedParentId = (String) redisTemplate.opsForHash().get(reservationKey, "parentId");
            return storedParentId != null && storedParentId.equals(parentId.toString());
        } catch (Exception e) {
            return false;
        }
    }

    public Map<String, Object> getReservationDetails(String reservationId) {
        String reservationKey = "reservation:" + reservationId;
        return redisTemplate.opsForHash().entries(reservationKey)
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().toString(),
                        Map.Entry::getValue
                ));
    }

    public Map<String, Object> getSessionAvailability(Long sessionId) {
        Map<String, Object> availability = new HashMap<>();
        try {
            Optional<Session> sessionOpt = sessionRepository.findById(sessionId);
            if (!sessionOpt.isPresent()) {
                availability.put("available", false);
                availability.put("reason", "Session not found");
                return availability;
            }

            Session session = sessionOpt.get();
            int booked = session.getBookedCount();
            int reserved = Optional.ofNullable(redisTemplate.opsForValue().get("session:" + sessionId + ":reserved_count"))
                                   .map(Integer::parseInt).orElse(0);
            int totalAvailable = 36 - booked - reserved;

            availability.put("sessionId", sessionId);
            availability.put("bookedSpots", booked);
            availability.put("reservedSpots", reserved);
            availability.put("availableSpots", totalAvailable);
            availability.put("available", totalAvailable > 0);
            availability.put("reason", totalAvailable > 0 ? "Available" : "Fully booked");

        } catch (Exception e) {
            log.error("Error checking session availability: {}", sessionId, e);
            availability.put("available", false);
            availability.put("reason", "Error checking availability");
        }
        return availability;
    }

    public Map<Long, Map<String, Object>> getMultipleSessionsAvailability(List<Long> sessionIds) {
        return sessionIds.stream()
                .collect(Collectors.toMap(
                        id -> id,
                        this::getSessionAvailability
                ));
    }

    public boolean extendReservationTTL(String reservationId, Long extendSeconds) {
        try {
            String reservationKey = "reservation:" + reservationId;
            Map<Object, Object> reservationData = redisTemplate.opsForHash().entries(reservationKey);
            if (reservationData.isEmpty()) return false;

            String sessionIdsStr = (String) reservationData.get("sessionIds");
            List<Long> sessionIds = Arrays.stream(sessionIdsStr.split(",")).map(Long::valueOf).collect(Collectors.toList());

            redisTemplate.expire(reservationKey, extendSeconds, TimeUnit.SECONDS);
            for (Long sessionId : sessionIds) {
                String reservedKey = "session:" + sessionId + ":reserved_count";
                redisTemplate.expire(reservedKey, extendSeconds, TimeUnit.SECONDS);
            }

            return true;

        } catch (Exception e) {
            log.error("Error extending reservation TTL", e);
            return false;
        }
    }
}
