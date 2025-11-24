package com.ssdevcheckincheckout.ssdev.Backend.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ssdevcheckincheckout.ssdev.Backend.entity.GetInTouch;
import com.ssdevcheckincheckout.ssdev.Backend.exceptions.GetInTouchException;
import com.ssdevcheckincheckout.ssdev.Backend.repository.GetInTouchRepository;

@Service
public class GetInTouchService {

    private static final Logger logger = LoggerFactory.getLogger(GetInTouchService.class);

    @Autowired
    private GetInTouchRepository getInTouchRepository;

    /**
     * Add new GetInTouch details
     */
    public GetInTouch addDetails(GetInTouch getInTouch) {
        logger.info("Start: addDetails() — Adding new GetInTouch details: {}", getInTouch);

        try {
            GetInTouch saved = getInTouchRepository.save(getInTouch);
            logger.info("Success: addDetails() — Saved successfully with ID: {}", saved.getId());
            return saved;
        } catch (Exception e) {
            logger.error("Error in addDetails(): {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Get all GetInTouch records
     */
    public List<GetInTouch> getDetails() {
        logger.info("Start: getDetails() — Fetching all GetInTouch records");

        try {
            List<GetInTouch> list = getInTouchRepository.findAll();
            logger.info("Success: getDetails() — Retrieved {} records", list.size());
            return list;
        } catch (Exception e) {
            logger.error("Error in getDetails(): {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Get a record by ID
     */
    public GetInTouch getById(Long id) {
        logger.info("Start: getById() — Fetching GetInTouch record with ID: {}", id);

        try {
            GetInTouch record = getInTouchRepository.findById(id)
                    .orElseThrow(() -> new GetInTouchException(id));

            logger.info("Success: getById() — Found record: {}", record);
            return record;
        } catch (GetInTouchException e) {
            logger.error("Not Found: getById() — {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error in getById(): {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Update a record by ID
     */
    public GetInTouch updateById(Long id, GetInTouch getInTouch) {
        logger.info("Start: updateById() — Updating record with ID: {}", id);

        try {
            if (!getInTouchRepository.existsById(id)) {
                throw new GetInTouchException(id);
            }

            GetInTouch updated = getInTouchRepository.save(getInTouch);
            logger.info("Success: updateById() — Updated successfully: {}", updated);
            return updated;
        } catch (GetInTouchException e) {
            logger.error("Not Found: updateById() — {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error in updateById(): {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Delete a record by ID
     */
    public void deleteById(Long id) {
        logger.info("Start: deleteById() — Deleting record with ID: {}", id);

        try {
            if (!getInTouchRepository.existsById(id)) {
                throw new GetInTouchException(id);
            }

            getInTouchRepository.deleteById(id);
            logger.info("Success: deleteById() — Deleted record with ID: {}", id);
        } catch (GetInTouchException e) {
            logger.error("Not Found: deleteById() — {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error in deleteById(): {}", e.getMessage(), e);
            throw e;
        }
    }
}
