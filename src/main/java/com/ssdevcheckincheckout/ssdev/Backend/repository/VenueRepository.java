package com.ssdevcheckincheckout.ssdev.Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ssdevcheckincheckout.ssdev.Backend.entity.Venues;


@Repository
public interface VenueRepository extends JpaRepository<Venues,Long> {
	
	

}
