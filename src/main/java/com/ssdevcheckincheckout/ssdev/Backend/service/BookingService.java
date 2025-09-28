package com.ssdevcheckincheckout.ssdev.Backend.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ssdevcheckincheckout.ssdev.Backend.dto.BookingRequestDto;
import com.ssdevcheckincheckout.ssdev.Backend.dto.BookingResponseDto;
import com.ssdevcheckincheckout.ssdev.Backend.entity.CricketBookingEntity;
import com.ssdevcheckincheckout.ssdev.Backend.entity.Kid;
import com.ssdevcheckincheckout.ssdev.Backend.entity.Session;
import com.ssdevcheckincheckout.ssdev.Backend.entity.User;
import com.ssdevcheckincheckout.ssdev.Backend.repository.CricketBookingRepository;
import com.ssdevcheckincheckout.ssdev.Backend.repository.KidRepository;
import com.ssdevcheckincheckout.ssdev.Backend.repository.SessionRepository;
import com.ssdevcheckincheckout.ssdev.Backend.repository.UserRepository;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;

// Updated BookingService.java - Remove Redis dependency

@Service
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    @Autowired
    private CricketBookingRepository cricketBookingRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private KidRepository kidRepository;

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private SendGridEmailService sendGridEmailService;
    
    @Autowired
    private ReservationService reservationService; // Add this

    // Keep existing methods unchanged
    public List<BookingResponseDto> getBookingsForUser(Long parentId) {
        log.info("Fetching bookings for parentId={}", parentId);
        List<CricketBookingEntity> bookings = cricketBookingRepository.findByParentId(parentId);

        List<BookingResponseDto> bookingDtos = new ArrayList<>();
        for (CricketBookingEntity booking : bookings) {
            bookingDtos.add(buildBookingResponse(booking));
        }
        log.debug("Found {} bookings for parentId={}", bookingDtos.size(), parentId);
        return bookingDtos;
    }

    public List<BookingResponseDto> getAllBookings() {
        log.info("Fetching all bookings...");
        List<CricketBookingEntity> bookings = cricketBookingRepository.findAll();

        List<BookingResponseDto> bookingDtos = new ArrayList<>();
        for (CricketBookingEntity booking : bookings) {
            bookingDtos.add(buildBookingResponse(booking));
        }
        log.debug("Total bookings found: {}", bookingDtos.size());
        return bookingDtos;
    }

    @Transactional
    public BookingResponseDto updateBookingFromDTO(BookingRequestDto dto) {
        log.info("Updating booking from DTO: {}", dto);

        // Validate sessions and update booked count atomically
        List<Session> updatedSessions = new ArrayList<>();
        
        for (Long sessionId : dto.getSessionIds()) {
            Optional<Session> sessionOpt = sessionRepository.findById(sessionId);

            if (sessionOpt.isPresent()) {
                Session existingSession = sessionOpt.get();

                // Double-check availability (even though reservation should guarantee this)
                if (existingSession.getBookedCount() < 36) {
                    existingSession.setBookedCount(existingSession.getBookedCount() + 1);
                    updatedSessions.add(existingSession);
                    log.info("Updated session {} booked count to {}", 
                            sessionId, existingSession.getBookedCount());
                } else {
                    log.error("Session {} is full during booking confirmation", sessionId);
                    throw new RuntimeException("Session " + sessionId + " is now full");
                }
            } else {
                log.error("Session not found for ID={}", sessionId);
                throw new RuntimeException("Session not found for ID: " + sessionId);
            }
        }

        // Save all session updates atomically
        sessionRepository.saveAll(updatedSessions);

        // Create the booking record
        CricketBookingEntity booking = new CricketBookingEntity();
        booking.setParentId(dto.getParentId());
        booking.setChildId(dto.getChildId());
        booking.setTotalAmount(dto.getTotalAmount());
        booking.setAmount(dto.getTotalAmount());
        booking.setPaymentStatus(true); // Only called after successful payment
        booking.setBookingType(dto.getBookingType());
        booking.setDiscountApplied(dto.getDiscountApplied());
        booking.setSessionIds(dto.getSessionIds());
        booking.setTimestamp(dto.getTimestamp());

        CricketBookingEntity savedBooking = cricketBookingRepository.save(booking);

        BookingResponseDto bookingData = buildBookingResponse(savedBooking);
        log.info("Booking created successfully: {}", bookingData);
        
        // Send confirmation emails (existing code)
        try {
            sendGridEmailService.sendBookingConfirmation(
                    bookingData.getParentEmail(),
                    bookingData.getParentName(),
                    bookingData.getKidName(),
                    bookingData.getBookingId(),
                    bookingData.getTotalAmount(),
                    bookingData.getSessionDetails()
            );
        } catch (IOException e) {
            log.error("Failed to send SendGrid email", e);
        }

        try {
            emailService.sendBookingConfirmation(
                    bookingData.getParentEmail(),
                    bookingData.getParentName(),
                    bookingData.getKidName(),
                    bookingData.getBookingId(),
                    bookingData.getTotalAmount(),
                    bookingData.getSessionDetails()
            );
            log.info("Booking confirmation email sent to {}", bookingData.getParentEmail());
        } catch (MessagingException e) {
            log.error("Failed to send booking confirmation email for bookingId={}", 
                    bookingData.getBookingId(), e);
        }

        return bookingData;
    }

    // Add this new method for reservation-based booking
    @Transactional
    public BookingResponseDto createBookingFromReservation(String reservationId) {
        log.info("Creating booking from reservation: {}", reservationId);
        
        // Get reservation details from ReservationService
        Map<String, Object> reservationDetails = reservationService.getReservationDetails(reservationId);
        if (reservationDetails.isEmpty()) {
            throw new RuntimeException("Reservation not found or expired: " + reservationId);
        }
        
        // Extract booking details from reservation
        Long parentId = Long.valueOf((String) reservationDetails.get("parentId"));
        Long childId = Long.valueOf((String) reservationDetails.get("childId"));
        String sessionIdsStr = (String) reservationDetails.get("sessionIds");
        Double totalAmount = Double.valueOf((String) reservationDetails.get("totalAmount"));
        
        List<Long> sessionIds = Arrays.stream(sessionIdsStr.split(","))
            .map(Long::valueOf)
            .collect(Collectors.toList());

        // Create booking DTO
        BookingRequestDto bookingRequest = new BookingRequestDto();
        bookingRequest.setParentId(parentId);
        bookingRequest.setChildId(childId);
        bookingRequest.setSessionIds(sessionIds);
        bookingRequest.setTotalAmount(totalAmount);
        bookingRequest.setPaymentStatus(true);
        bookingRequest.setBookingType("ONLINE");
        bookingRequest.setTimestamp(LocalDateTime.now());
        
        // Process the booking
        return updateBookingFromDTO(bookingRequest);
    }

    // Delegate availability checking to ReservationService
    public Map<String, Object> getSessionAvailability(Long sessionId) {
        return reservationService.getSessionAvailability(sessionId);
    }

    public Map<Long, Map<String, Object>> getMultipleSessionsAvailability(List<Long> sessionIds) {
        return reservationService.getMultipleSessionsAvailability(sessionIds);
    }

    // Keep your existing buildBookingResponse method unchanged
    private BookingResponseDto buildBookingResponse(CricketBookingEntity booking) {
        log.debug("Building booking response for bookingId={}", booking.getId());

        User parent = userRepository.findById(booking.getParentId())
                .orElseThrow(() -> {
                    log.error("Parent not found for parentId={}", booking.getParentId());
                    return new RuntimeException("Parent not found");
                });

        Kid kid = kidRepository.findById(booking.getChildId())
                .orElseThrow(() -> {
                    log.error("Kid not found for childId={}", booking.getChildId());
                    return new RuntimeException("Kid not found");
                });

        List<Session> sessions = sessionRepository.findAllById(booking.getSessionIds());
        List<String> sessionDetails = new ArrayList<>();
        for (Session s : sessions) {
            sessionDetails.add(s.getDay() + " - " + s.getSessionClass() + " at " + s.getTime() + " " + s.getDate());
        }

        BookingResponseDto dto = new BookingResponseDto();
        dto.setBookingId(booking.getId());
        dto.setParentName(parent.getFirstName() + " " + parent.getLastName());
        dto.setParentEmail(parent.getEmail());
        dto.setKidName(kid.getFirstName() + " " + kid.getLastName());
        dto.setKidLevel(kid.getLevel());
        dto.setTotalAmount(booking.getTotalAmount());
        dto.setPaymentStatus(booking.getPaymentStatus());
        dto.setSessionDetails(sessionDetails);

        return dto;
    }
}