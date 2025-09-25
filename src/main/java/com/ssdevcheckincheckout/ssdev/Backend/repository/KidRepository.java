package com.ssdevcheckincheckout.ssdev.Backend.repository;

import com.ssdevcheckincheckout.ssdev.Backend.entity.Kid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KidRepository extends JpaRepository<Kid, Long> {
    List<Kid> findByParentId(Long parentId);
}