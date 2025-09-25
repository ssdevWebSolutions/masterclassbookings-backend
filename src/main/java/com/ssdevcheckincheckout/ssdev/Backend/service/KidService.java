package com.ssdevcheckincheckout.ssdev.Backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ssdevcheckincheckout.ssdev.Backend.entity.Kid;
import com.ssdevcheckincheckout.ssdev.Backend.entity.User;
import com.ssdevcheckincheckout.ssdev.Backend.repository.KidRepository;
import com.ssdevcheckincheckout.ssdev.Backend.repository.UserRepository;

@Service
public class KidService {

    @Autowired
    private KidRepository kidRepository;

    @Autowired
    private UserRepository userRepository;

    public Kid addKid(Long parentId, Kid kid) {
        User parent = userRepository.findById(parentId)
                        .orElseThrow(() -> new RuntimeException("Parent not found"));
        kid.setParent(parent);
        return kidRepository.save(kid);
    }

    public List<Kid> getKidsForParent(Long parentId) {
    	User parent = userRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Parent not found"));
    	
    	List<Kid> kid = new ArrayList<>();
    	for(Kid k: parent.getKids())
    	{
    		kid.add(k);
    	}
    	
        return kid;
    }

    public void deleteKid(Long kidId) {
        kidRepository.deleteById(kidId);
    }

    public Kid updateKid(Long kidId, Kid updatedKid) {
        Kid existing = kidRepository.findById(kidId)
                        .orElseThrow(() -> new RuntimeException("Kid not found"));
        existing.setFirstName(updatedKid.getFirstName());
        existing.setLastName(updatedKid.getLastName());
        existing.setAge(updatedKid.getAge());
        existing.setClub(updatedKid.getClub());
        existing.setMedicalInfo(updatedKid.getMedicalInfo());
        existing.setLevel(updatedKid.getLevel());
        return kidRepository.save(existing);
    }
}
