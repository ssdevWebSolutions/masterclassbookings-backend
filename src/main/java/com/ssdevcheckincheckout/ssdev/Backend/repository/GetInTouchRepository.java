package com.ssdevcheckincheckout.ssdev.Backend.repository;

//import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ssdevcheckincheckout.ssdev.Backend.entity.GetInTouch;

@Repository

public interface GetInTouchRepository  extends JpaRepository<GetInTouch,Long>
{
//	updating message via email
//	Optional<GetInTouch> findByEmail(String email);
	
	
	

}
