package com.ssdevcheckincheckout.ssdev.Backend.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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


    public BookingResponseDto updateBookingFromDTO(BookingRequestDto dto) {
        log.info("Updating booking from DTO: {}", dto);

        CricketBookingEntity booking = new CricketBookingEntity();

        // validate & update sessions
        List<Session> updateSessions = new ArrayList<>();
        for (Long sessionId : dto.getSessionIds()) {
            Optional<Session> cricketSession = sessionRepository.findById(sessionId);

            if (cricketSession.isPresent()) {
                Session existingSession = cricketSession.get();

                if (existingSession.getBookedCount() <= 36) {
                    existingSession.setBookedCount(existingSession.getBookedCount() + 1);
                    updateSessions.add(existingSession);
                } else {
                    log.error("No available spots left for session ID={}", sessionId);
                    throw new RuntimeException("⚠️ No available spots left for session ID: " + sessionId);
                }
            } else {
                log.error("Session not found for ID={}", sessionId);
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

        log.info("Booking created successfully: {}", bookingData);
        
        
//        sendgrid
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//        gmail
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
            log.error("Failed to send booking confirmation email for bookingId={}", bookingData.getBookingId(), e);
        }

        return bookingData;
    }


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
