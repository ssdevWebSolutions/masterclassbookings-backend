package com.ssdevcheckincheckout.ssdev.Backend.controller;

import com.ssdevcheckincheckout.ssdev.Backend.dto.BookingRequestDto;
import com.ssdevcheckincheckout.ssdev.Backend.dto.BookingResponseDto;
import com.ssdevcheckincheckout.ssdev.Backend.dto.ReservationResult;
import com.ssdevcheckincheckout.ssdev.Backend.service.BookingService;
import com.ssdevcheckincheckout.ssdev.Backend.service.ReservationService;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@CrossOrigin(origins = "https://masterclassbookings-rt5n.vercel.app/,http://localhost:3000")
@RestController
@RequestMapping("/api/auth/payments")
public class PaymentController {

    @Value("${stripe.api.key}")
    private String stripeApiKey;
    
    @Value("${stripe.endpoint.secret}")
    private String stripeEndpointSecret;
    
    @Autowired
    private BookingService bookingService;
    
    @Autowired
    private ReservationService reservationService;
    
    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    // ------------------- RESERVE SLOTS -------------------
    @PostMapping("/booking/reserve")
    public ResponseEntity<Map<String, Object>> reserveSlots(@RequestBody Map<String, Object> requestData) {
        try {
            Object parentIdObj = requestData.get("parentId");
            Object childIdObj = requestData.get("childId");
            Object sessionIdsObj = requestData.get("sessionIds");
            Object totalAmountObj = requestData.get("totalAmount");

            Long parentId = convertToLong(parentIdObj);
            Long childId = convertToLong(childIdObj);
            Double totalAmount = convertToDouble(totalAmountObj);

            List<Long> sessionIds = ((List<?>) sessionIdsObj)
                .stream()
                .map(this::convertToLong)
                .collect(Collectors.toList());

            ReservationResult result = reservationService.reserveSlots(parentId, childId, sessionIds, totalAmount);

            Map<String, Object> response = new HashMap<>();
            if (result.isSuccess()) {
                response.put("success", true);
                response.put("reservationId", result.getReservationId());
                response.put("expiresAt", result.getExpiresAt());
                response.put("ttlSeconds", result.getTtlSeconds());
                response.put("reservedSessionIds", sessionIds);
                response.put("message", "Slots reserved successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", result.getErrorMessage());
                response.put("unavailableSessionIds", result.getUnavailableSessionIds());
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

        } catch (Exception e) {
            log.error("Failed to reserve slots", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Internal server error during reservation");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ------------------- RELEASE RESERVATION -------------------
    @DeleteMapping("/booking/release-reservation")
    public ResponseEntity<Map<String, Object>> releaseReservation(@RequestBody Map<String, Object> requestData) {
        try {
            String reservationId = (String) requestData.get("reservationId");
            boolean released = reservationService.releaseReservation(reservationId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", released);
            response.put("message", released ? "Reservation released successfully" : "Reservation not found or already expired");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Failed to release reservation", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Internal server error during release");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ------------------- CREATE STRIPE CHECKOUT SESSION -------------------
    @PostMapping("/create-checkout-session")
    public ResponseEntity<Map<String, String>> createCheckoutSession(@RequestBody Map<String, Object> requestData) {
        Stripe.apiKey = stripeApiKey;
        try {
            String reservationId = (String) requestData.get("reservationId");
            Double amount = convertToDouble(requestData.get("amount"));
            String currency = (String) requestData.get("currency");

            if (reservationId == null || reservationId.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Reservation ID is required"));
            }

            Map<String, Object> reservationDetails = reservationService.getReservationDetails(reservationId);
            if (reservationDetails.isEmpty()) {
                return ResponseEntity.status(HttpStatus.GONE).body(Map.of("error", "Reservation not found or expired. Please reserve slots again."));
            }

            Map<String, Object> params = new HashMap<>();
            params.put("payment_method_types", Arrays.asList("card"));
            params.put("mode", "payment");

            // Stripe expects amount in cents
            long stripeAmount = (long) (amount * 1);

            List<Object> lineItems = new ArrayList<>();
            Map<String, Object> item = new HashMap<>();
            item.put("price_data", Map.of(
                    "currency", currency,
                    "product_data", Map.of("name", "Cricket Academy Booking"),
                    "unit_amount", stripeAmount
            ));
            item.put("quantity", 1);
            lineItems.add(item);
            params.put("line_items", lineItems);

            params.put("automatic_tax", Map.of("enabled", false));

            // Metadata
            Map<String, String> metadata = new HashMap<>();
            metadata.put("reservationId", reservationId);
            metadata.put("parentId", reservationDetails.get("parentId").toString());
            metadata.put("childId", reservationDetails.get("childId").toString());
            metadata.put("sessionIds", reservationDetails.get("sessionIds").toString());
            metadata.put("totalAmount", reservationDetails.get("totalAmount").toString());
            params.put("metadata", metadata);
            
//            params.put("success_url", "http://localhost:3000/payment-success?session_id={CHECKOUT_SESSION_ID}");
//            params.put("cancel_url", "http://localhost:3000/booking?cancelled=true");

//            params.put("success_url", "http://localhost:3000/bookings");
//            params.put("cancel_url", "http://localhost:3000/bookings?cancelled=true");
            
            params.put("success_url", "https://masterclassbookings-rt5n.vercel.app/payment-success?session_id={CHECKOUT_SESSION_ID}");
            params.put("cancel_url", "https://masterclassbookings-rt5n.vercel.app/bookings?cancelled=true");
            
         

            Session session = Session.create(params);

            return ResponseEntity.ok(Map.of("url", session.getUrl()));

        } catch (Exception e) {
            log.error("Error creating checkout session", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to create payment session"));
        }
    }

    // ------------------- STRIPE WEBHOOK -------------------
    @PostMapping("/stripe-webhook")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload,
                                                      @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, stripeEndpointSecret);

            if ("checkout.session.completed".equals(event.getType())) {
                Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
                if (session != null) handlePaymentSuccess(session);
            } else if ("checkout.session.expired".equals(event.getType())) {
                Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
                if (session != null) handlePaymentExpired(session);
            }

            return ResponseEntity.ok("Success");

        } catch (SignatureVerificationException e) {
            log.error("Invalid Stripe signature", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        } catch (Exception e) {
            log.error("Error processing Stripe webhook", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Webhook error");
        }
    }

    private void handlePaymentSuccess(Session session) {
        try {
            Map<String, String> metadata = session.getMetadata();
            String reservationId = metadata.get("reservationId");
            if (reservationId == null) return;

            Map<String, Object> reservationDetails = reservationService.getReservationDetails(reservationId);
            if (reservationDetails.isEmpty()) return;

            Long parentId = Long.valueOf(reservationDetails.get("parentId").toString());
            Long childId = Long.valueOf(reservationDetails.get("childId").toString());
            Double totalAmount = Double.valueOf(reservationDetails.get("totalAmount").toString());
            List<Long> sessionIds = Arrays.stream(reservationDetails.get("sessionIds").toString().split(","))
                    .map(Long::valueOf)
                    .collect(Collectors.toList());

            BookingRequestDto bookingRequest = new BookingRequestDto();
            bookingRequest.setParentId(parentId);
            bookingRequest.setChildId(childId);
            bookingRequest.setSessionIds(sessionIds);
            bookingRequest.setTotalAmount(totalAmount);
            bookingRequest.setPaymentStatus(true);
            bookingRequest.setBookingType("ONLINE");
            bookingRequest.setTimestamp(LocalDateTime.now());

            BookingResponseDto booking = bookingService.updateBookingFromDTO(bookingRequest);

            reservationService.releaseReservation(reservationId);

            log.info("Payment successful and booking confirmed: {} for reservation: {}", booking.getBookingId(), reservationId);

        } catch (Exception e) {
            log.error("Error processing successful payment", e);
        }
    }

    private void handlePaymentExpired(Session session) {
        try {
            Map<String, String> metadata = session.getMetadata();
            String reservationId = metadata.get("reservationId");
            if (reservationId != null) {
                boolean released = reservationService.releaseReservation(reservationId);
                log.info("Payment expired, reservation {} released: {}", reservationId, released);
            }
        } catch (Exception e) {
            log.error("Error processing expired payment", e);
        }
    }

    // ------------------- BOOKING ENDPOINTS -------------------
    @PostMapping("/bookings")
    public ResponseEntity<BookingResponseDto> createBooking(@RequestBody BookingRequestDto bookingRequest) {
        return ResponseEntity.ok(bookingService.updateBookingFromDTO(bookingRequest));
    }

    @GetMapping("/bookings/parent/{parentId}")
    public ResponseEntity<List<BookingResponseDto>> getBookingsForParent(@PathVariable Long parentId) {
        return ResponseEntity.ok(bookingService.getBookingsForUser(parentId));
    }

    @GetMapping("/bookings/all")
    public ResponseEntity<List<BookingResponseDto>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    // ------------------- SESSION AVAILABILITY -------------------
    @GetMapping("/sessions/{sessionId}/availability")
    public ResponseEntity<Map<String, Object>> getSessionAvailability(@PathVariable Long sessionId) {
        return ResponseEntity.ok(reservationService.getSessionAvailability(sessionId));
    }

    @PostMapping("/sessions/availability")
    public ResponseEntity<Map<Long, Map<String, Object>>> getMultipleSessionsAvailability(@RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<Long> sessionIds = ((List<Number>) request.get("sessionIds"))
                .stream().map(Number::longValue).collect(Collectors.toList());
        return ResponseEntity.ok(reservationService.getMultipleSessionsAvailability(sessionIds));
    }

    // ------------------- EXTEND RESERVATION -------------------
    @PostMapping("/reservation/{reservationId}/extend")
    public ResponseEntity<Map<String, Object>> extendReservation(@PathVariable String reservationId,
                                                                 @RequestBody Map<String, Object> request) {
        try {
            Long extendSeconds = ((Number) request.get("extendSeconds")).longValue();
            Map<String, Object> reservationDetails = reservationService.getReservationDetails(reservationId);
            if (reservationDetails.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "message", "Reservation not found or expired"));
            }

            boolean extended = reservationService.extendReservationTTL(reservationId, extendSeconds);
            Map<String, Object> response = new HashMap<>();
            response.put("success", extended);
            response.put("message", extended ? "Reservation extended successfully" : "Failed to extend reservation");
            if (extended) {
                response.put("expiresAt", LocalDateTime.now().plusSeconds(extendSeconds).toString());
                response.put("ttlSeconds", extendSeconds);
            }
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error extending reservation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("success", false, "message", "Internal server error"));
        }
    }

    // ------------------- HELPER METHODS -------------------
    private Long convertToLong(Object obj) {
        if (obj instanceof Number) return ((Number) obj).longValue();
        else if (obj instanceof String) return Long.parseLong((String) obj);
        else throw new IllegalArgumentException("Cannot convert to Long: " + obj);
    }

    private Double convertToDouble(Object obj) {
        if (obj instanceof Number) return ((Number) obj).doubleValue();
        else if (obj instanceof String) return Double.parseDouble((String) obj);
        else throw new IllegalArgumentException("Cannot convert to Double: " + obj);
    }
}
