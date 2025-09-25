package com.ssdevcheckincheckout.ssdev.Backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//import com.ssdevcheckincheckout.ssdev.Backend.entity.CricketBooking;
import com.ssdevcheckincheckout.ssdev.Backend.entity.CricketBookingEntity;

@Repository
public interface CricketBookingRepository  extends JpaRepository<CricketBookingEntity, Long>{
	 List<CricketBookingEntity> findByParentId(Long parentId);

}
