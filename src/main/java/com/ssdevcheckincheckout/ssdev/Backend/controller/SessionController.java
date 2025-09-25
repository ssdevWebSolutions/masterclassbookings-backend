package com.ssdevcheckincheckout.ssdev.Backend.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ssdevcheckincheckout.ssdev.Backend.entity.Session;
import com.ssdevcheckincheckout.ssdev.Backend.repository.SessionRepository;
import com.ssdevcheckincheckout.ssdev.Backend.service.SessionService;

import java.util.List;

@RestController
@RequestMapping("/api/auth/sessions")
public class SessionController {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private SessionRepository sessionRepository;

    // Insert all predefined sessions (optional)
    @PostMapping("/init")
    public ResponseEntity<String> insertSessions() {
        sessionService.insertPredefinedSessions();
        return ResponseEntity.ok("All sessions inserted successfully!");
    }

    // GET all 2025 sessions
    @GetMapping("/2025")
    public ResponseEntity<List<Session>> getSessions2025() {
        List<Session> sessions2025 = sessionRepository.findAll()
                .stream()
                .filter(s -> s.getDate().startsWith("2025"))
                .toList();
        return ResponseEntity.ok(sessions2025);
    }

    // GET all 2026 sessions
    @GetMapping("/2026")
    public ResponseEntity<List<Session>> getSessions2026() {
        List<Session> sessions2026 = sessionRepository.findAll()
                .stream()
                .filter(s -> s.getDate().startsWith("2026"))
                .toList();
        return ResponseEntity.ok(sessions2026);
    }

    // UPDATE bookedCount by session ID
    @PutMapping("/{id}")
    public ResponseEntity<Session> updateBookedCount(@PathVariable Long id, @RequestBody SlotUpdateRequest request) {
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found with id " + id));

        session.setBookedCount(request.getBookedCount());
        sessionRepository.save(session);
        return ResponseEntity.ok(session);
    }

    // DTO for updating bookedCount
    public static class SlotUpdateRequest {
        private Integer bookedCount;

        public Integer getBookedCount() { return bookedCount; }
        public void setBookedCount(Integer bookedCount) { this.bookedCount = bookedCount; }
    }
}

