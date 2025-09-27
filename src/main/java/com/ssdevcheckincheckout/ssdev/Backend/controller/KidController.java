package com.ssdevcheckincheckout.ssdev.Backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssdevcheckincheckout.ssdev.Backend.entity.Kid;
import com.ssdevcheckincheckout.ssdev.Backend.service.KidService;

@CrossOrigin(origins = "https://masterclassbookings-rt5n.vercel.app/,http://localhost:3000")
@RestController
@RequestMapping("/api/auth")
public class KidController {

    @Autowired
    private KidService kidService;

   
    
    
    @PostMapping("/kids/add/{parentId}")
    public ResponseEntity<Kid> addKid(@PathVariable Long parentId, @RequestBody Kid kid) {
        Kid savedKid = kidService.addKid(parentId, kid);
        return ResponseEntity.ok(savedKid);
    }

    @GetMapping("/kids/parent/{parentId}")
    public ResponseEntity<List<Kid>> getKids(@PathVariable Long parentId) {
        return ResponseEntity.ok(kidService.getKidsForParent(parentId));
    }

    @PutMapping("/kids/update/{kidId}")
    public ResponseEntity<Kid> updateKid(@PathVariable Long kidId, @RequestBody Kid kid) {
        return ResponseEntity.ok(kidService.updateKid(kidId, kid));
    }

    @DeleteMapping("/kids/delete/{kidId}")
    public ResponseEntity<Void> deleteKid(@PathVariable Long kidId) {
        kidService.deleteKid(kidId);
        return ResponseEntity.noContent().build();
    }
}
