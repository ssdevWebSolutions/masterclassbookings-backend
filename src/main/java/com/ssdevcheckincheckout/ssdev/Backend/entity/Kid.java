package com.ssdevcheckincheckout.ssdev.Backend.entity;


import jakarta.persistence.*;

@Entity
@Table(name = "kids")
public class Kid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private int age;
    private String club;
    private String medicalInfo;
    private String level;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id") // foreign key to User
    private User parent;

    public Kid() {}

    public Kid(String firstName, String lastName, int age, String club, String medicalInfo, String level, User parent) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.club = club;
        this.medicalInfo = medicalInfo;
        this.level = level;
        this.parent = parent;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public String getClub() { return club; }
    public void setClub(String club) { this.club = club; }
    public String getMedicalInfo() { return medicalInfo; }
    public void setMedicalInfo(String medicalInfo) { this.medicalInfo = medicalInfo; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
//    public User getParent() { return parent; }
    public void setParent(User parent) { this.parent = parent; }
}
