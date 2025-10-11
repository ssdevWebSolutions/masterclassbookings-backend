package com.ssdevcheckincheckout.ssdev.Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ssdevcheckincheckout.ssdev.Backend.entity.RegistrationServiceRequest;


@Repository
public interface RegistrationService  extends JpaRepository<RegistrationServiceRequest, Long>{

}
