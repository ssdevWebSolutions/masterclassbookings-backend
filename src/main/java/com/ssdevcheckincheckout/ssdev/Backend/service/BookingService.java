package com.ssdevcheckincheckout.ssdev.Backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

@Service
public class BookingService {

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
    
    
    public List<BookingResponseDto> getBookingsForUser(Long parentId) {
        // Fetch bookings for this parent
        List<CricketBookingEntity> bookings = cricketBookingRepository.findByParentId(parentId);

        List<BookingResponseDto> bookingDtos = new ArrayList<>();
        for (CricketBookingEntity booking : bookings) {
            bookingDtos.add(buildBookingResponse(booking));
        }
        return bookingDtos;
    }

    
    public List<BookingResponseDto> getAllBookings() {
        List<CricketBookingEntity> bookings = cricketBookingRepository.findAll();

        List<BookingResponseDto> bookingDtos = new ArrayList<>();
        for (CricketBookingEntity booking : bookings) {
            bookingDtos.add(buildBookingResponse(booking));
        }
        return bookingDtos;
    }



    public BookingResponseDto updateBookingFromDTO(BookingRequestDto dto) {
        CricketBookingEntity booking = new CricketBookingEntity();

        // validate & update sessions
        List<Session> updateSessions = new ArrayList<>();
        for (Long sessionId : dto.getSessionIds()) {
            Optional<Session> cricketSession = sessionRepository.findById(sessionId);

            if (cricketSession.isPresent()) {
                Session existingSession = cricketSession.get();

                if (existingSession.getBookedCount() > 0) {
                    existingSession.setBookedCount(existingSession.getBookedCount() + 1);
                    updateSessions.add(existingSession);
                } else {
                    throw new RuntimeException("⚠️ No available spots left for session ID: " + sessionId);
                }
            } else {
                throw new RuntimeException("❌ Session not found for ID: " + sessionId);
            }
        }

        sessionRepository.saveAll(updateSessions);

        // map fields from DTO
        booking.setParentId(dto.getParentId());
        booking.setChildId(dto.getChildId());
        booking.setTotalAmount(dto.getTotalAmount());
        booking.setAmount(dto.getTotalAmount());
        booking.setPaymentStatus(true);
        booking.setBookingType(dto.getBookingType());
        booking.setDiscountApplied(dto.getDiscountApplied());
        booking.setSessionIds(dto.getSessionIds());
        booking.setTimestamp(dto.getTimestamp());

        CricketBookingEntity savedBooking = cricketBookingRepository.save(booking);
        
       
        
        BookingResponseDto bookingData = buildBookingResponse(savedBooking);
        System.out.println(bookingData.getParentEmail()+"->"+bookingData.getParentName()+
        				  "->"+bookingData.getKidName()+"->"+bookingData.getBookingId()+
        				  "->"+bookingData.getTotalAmount()+"->"+bookingData.getSessionDetails());
        
        try {
			emailService.sendBookingConfirmation(
				    bookingData.getParentEmail(),   // Parent’s actual email
				    bookingData.getParentName(),    // Parent/Guardian Name
				    bookingData.getKidName(),       // Child Name
				    bookingData.getBookingId(),     // Booking ID / Order Reference
				    bookingData.getTotalAmount(),   // Total Amount Paid
				    bookingData.getSessionDetails()                     // Session Details with dates
				);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        // build response DTO for frontend + mail
//        return buildBookingResponse(savedBooking);
        return bookingData;
    }
    
    

    private BookingResponseDto buildBookingResponse(CricketBookingEntity booking) {
        User parent = userRepository.findById(booking.getParentId())
                .orElseThrow(() -> new RuntimeException("Parent not found"));

        Kid kid = kidRepository.findById(booking.getChildId())
                .orElseThrow(() -> new RuntimeException("Kid not found"));

        List<Session> sessions = sessionRepository.findAllById(booking.getSessionIds());
        List<String> sessionDetails = new ArrayList<>();
        for (Session s : sessions) {
            sessionDetails.add(s.getDay() + " - " + s.getSessionClass() + " at " + s.getTime() +" "+s.getDate());
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
