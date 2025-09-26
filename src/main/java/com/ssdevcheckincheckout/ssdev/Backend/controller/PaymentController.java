package com.ssdevcheckincheckout.ssdev.Backend.controller;

import com.ssdevcheckincheckout.ssdev.Backend.dto.BookingRequestDto;
import com.ssdevcheckincheckout.ssdev.Backend.dto.BookingResponseDto;
import com.ssdevcheckincheckout.ssdev.Backend.service.BookingService;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "https://masterclassbookings-rt5n.vercel.app/") // allow frontend origin
@RestController
@RequestMapping("/api/auth/payments")
public class PaymentController {

    @Value("${stripe.api.key}")
    private String stripeApiKey;
    
    @Autowired
    private BookingService bookingService;

    @PostMapping("/create-checkout-session")
    public ResponseEntity<Map<String, String>> createCheckoutSession(@RequestBody Map<String, Object> requestData) {
        Stripe.apiKey = stripeApiKey;

        try {
            Long amount = ((Number) requestData.get("amount")).longValue();
            String currency = (String) requestData.get("currency");

            Map<String, Object> params = new HashMap<>();
            params.put("payment_method_types", Arrays.asList("card"));
            params.put("mode", "payment");

            List<Object> lineItems = new ArrayList<>();
            Map<String, Object> item = new HashMap<>();
            item.put("price_data", Map.of(
                    "currency", currency,
                    "product_data", Map.of("name", "Cricket Academy Booking"),
                    "unit_amount", amount
            ));
            item.put("quantity", 1);
            lineItems.add(item);

            params.put("line_items", lineItems);
            params.put("success_url", "https://masterclassbookings-rt5n.vercel.app/payment-success");
            params.put("cancel_url", "https://masterclassbookings-rt5n.vercel.app/booking");

            Session session = Session.create(params);

            Map<String, String> responseData = new HashMap<>();
            responseData.put("url", session.getUrl());

            return ResponseEntity.ok(responseData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    
    @PostMapping("/bookings")
    public ResponseEntity<BookingResponseDto> createBooking(@RequestBody BookingRequestDto bookingRequest) {
        // Save bookingRequest into DB or process further
    	
    	
    	
    	
        return ResponseEntity.status(HttpStatus.OK).body(bookingService.updateBookingFromDTO(bookingRequest));
    }

 // --- GET bookings for a particular parent ---
    @GetMapping("/bookings/parent/{parentId}")
    public ResponseEntity<List<BookingResponseDto>> getBookingsForParent(@PathVariable Long parentId) {
        List<BookingResponseDto> bookings = bookingService.getBookingsForUser(parentId);
        return ResponseEntity.ok(bookings);
    }

    // --- GET bookings for all parents (admin) ---
    @GetMapping("/bookings/all")
    public ResponseEntity<List<BookingResponseDto>> getAllBookings() {
        List<BookingResponseDto> allBookings = bookingService.getAllBookings();
        return ResponseEntity.ok(allBookings);
    }
    
    
    
}
