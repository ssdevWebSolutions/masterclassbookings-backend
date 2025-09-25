package com.ssdevcheckincheckout.ssdev.Backend.service;





import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ssdevcheckincheckout.ssdev.Backend.entity.Session;
import com.ssdevcheckincheckout.ssdev.Backend.repository.SessionRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class SessionService {

    @Autowired
    private SessionRepository sessionRepository;

    public void insertPredefinedSessions() {
        if (sessionRepository.count() > 0) {
            // Avoid inserting duplicate data
            return;
        }

        List<Session> sessions = new ArrayList<>();

        // --------------------- 2025 Friday Sessions ---------------------
        String[] fridayDates2025 = {
                "2025-10-10","2025-10-17","2025-10-24","2025-10-31",
                "2025-11-07","2025-11-14","2025-11-21","2025-11-28",
                "2025-12-05","2025-12-12"
        };
        for (String date : fridayDates2025) {
            sessions.add(new Session(date, "Friday", "5:45pm-7:15pm", "friday", null, 0));
        }

        // --------------------- 2025 Sunday Class 1 ---------------------
        String[] sundayClass1Dates2025 = {
                "2025-10-12","2025-10-19","2025-10-26","2025-11-02",
                "2025-11-09","2025-11-16","2025-11-23","2025-11-30",
                "2025-12-07","2025-12-14"
        };
        for (String date : sundayClass1Dates2025) {
            sessions.add(new Session(date, "Sunday", "4:30pm-6:00pm", "sunday-class1", "Class 1", 0));
        }

        // --------------------- 2025 Sunday Class 2 ---------------------
        String[] sundayClass2Dates2025 = {
                "2025-10-12","2025-10-19","2025-10-26","2025-11-02",
                "2025-11-09","2025-11-16","2025-11-23","2025-11-30",
                "2025-12-07","2025-12-14"
        };
        for (String date : sundayClass2Dates2025) {
            sessions.add(new Session(date, "Sunday", "6:00pm-7:30pm", "sunday-class2", "Class 2", 0));
        }

        // --------------------- 2026 Friday Sessions ---------------------
        String[] fridayDates2026 = {
                "2026-01-16","2026-01-23","2026-01-30","2026-02-06","2026-02-13",
                "2026-02-20","2026-02-27","2026-03-06","2026-03-13","2026-03-20"
        };
        for (String date : fridayDates2026) {
            sessions.add(new Session(date, "Friday", "5:45pm-7:15pm", "friday", null, 36));
        }

        // --------------------- 2026 Sunday Class 1 ---------------------
        String[] sundayClass1Dates2026 = {
                "2026-01-18","2026-01-25","2026-02-01","2026-02-08","2026-02-15",
                "2026-02-22","2026-03-01","2026-03-08","2026-03-15","2026-03-22"
        };
        for (String date : sundayClass1Dates2026) {
            sessions.add(new Session(date, "Sunday", "4:30pm-6:00pm", "sunday-class1", "Class 1", 0));
        }

        // --------------------- 2026 Sunday Class 2 ---------------------
        String[] sundayClass2Dates2026 = {
                "2026-01-18","2026-01-25","2026-02-01","2026-02-08","2026-02-15",
                "2026-02-22","2026-03-01","2026-03-08","2026-03-15","2026-03-22"
        };
        for (String date : sundayClass2Dates2026) {
            sessions.add(new Session(date, "Sunday", "6:00pm-7:30pm", "sunday-class2", "Class 2", 0));
        }

        // Save all sessions at once
        sessionRepository.saveAll(sessions);
    }
}
