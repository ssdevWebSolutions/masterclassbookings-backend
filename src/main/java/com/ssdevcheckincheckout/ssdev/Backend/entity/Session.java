package com.ssdevcheckincheckout.ssdev.Backend.entity;



import jakarta.persistence.*;

@Entity
@Table(name = "sessions")
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String date;
    private String day;
    private String time;
    private String type;
    private String sessionClass; // "Class 1" or "Class 2" for Sunday sessions
    private Integer bookedCount;

    public Session() {}

    public Session(String date, String day, String time, String type, String sessionClass, Integer bookedCount) {
        this.date = date;
        this.day = day;
        this.time = time;
        this.type = type;
        this.sessionClass = sessionClass;
        this.bookedCount = bookedCount;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getDay() { return day; }
    public void setDay(String day) { this.day = day; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getSessionClass() { return sessionClass; }
    public void setSessionClass(String sessionClass) { this.sessionClass = sessionClass; }

    public Integer getBookedCount() { return bookedCount; }
    public void setBookedCount(Integer bookedCount) { this.bookedCount = bookedCount; }
}

