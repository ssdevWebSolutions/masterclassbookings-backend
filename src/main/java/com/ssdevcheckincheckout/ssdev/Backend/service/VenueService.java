package com.ssdevcheckincheckout.ssdev.Backend.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ssdevcheckincheckout.ssdev.Backend.entity.Venues;
import com.ssdevcheckincheckout.ssdev.Backend.repository.VenueRepository;

@Service
public class VenueService {

    private static final Logger logger = LoggerFactory.getLogger(VenueService.class);

    @Autowired
    private VenueRepository venueRepository;

    /**
     * Add a new venue
     */
    public Venues addVenueDetails(Venues venueDetails) {
        logger.info("Start: addVenueDetails() — Adding new venue: {}", venueDetails);

        try {
            Venues savedVenue = venueRepository.save(venueDetails);
            logger.info("Success: addVenueDetails() — Saved venue with ID: {}", savedVenue.getId());
            return savedVenue;
        } catch (Exception e) {
            logger.error("Error in addVenueDetails(): {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Update venue details by ID
     */
    public Venues updateVenue(Long id, Venues venue) {
        logger.info("Start: updateVenue() — Updating venue with ID: {}", id);

        try {
            Optional<Venues> existingVenue = venueRepository.findById(id);

            if (existingVenue.isPresent()) {
                Venues updated = existingVenue.get();
                updated.setVenueName(venue.getVenueName());
                updated.setAddressLine1(venue.getAddressLine1());
                updated.setAddressLine2(venue.getAddressLine2());
                updated.setTown(venue.getTown());
                updated.setPostcode(venue.getPostcode());
                updated.setVenueNotes(venue.getVenueNotes());

                Venues saved = venueRepository.save(updated);
                logger.info("Success: updateVenue() — Updated venue successfully: {}", saved);
                return saved;
            } else {
                logger.warn("Warning: updateVenue() — Venue not found with ID: {}", id);
                return null;
            }
        } catch (Exception e) {
            logger.error("Error in updateVenue(): {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Get all venues
     */
    public List<Venues> getAllVenues() {
        logger.info("Start: getAllVenues() — Fetching all venues");

        try {
            List<Venues> venues = venueRepository.findAll();
            logger.info("Success: getAllVenues() — Retrieved {} venues", venues.size());
            return venues;
        } catch (Exception e) {
            logger.error("Error in getAllVenues(): {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Get venue by ID
     */
    public Venues getVenueById(Long id) {
        logger.info("Start: getVenueById() — Fetching venue with ID: {}", id);

        try {
            Optional<Venues> venue = venueRepository.findById(id);
            if (venue.isPresent()) {
                logger.info("Success: getVenueById() — Found venue: {}", venue.get());
                return venue.get();
            } else {
                logger.warn("Warning: getVenueById() — Venue not found with ID: {}", id);
                return null;
            }
        } catch (Exception e) {
            logger.error("Error in getVenueById(): {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Delete venue by ID
     */
    public void deleteVenue(Long id) {
        logger.info("Start: deleteVenue() — Deleting venue with ID: {}", id);

        try {
            if (!venueRepository.existsById(id)) {
                logger.warn("Warning: deleteVenue() — Venue not found with ID: {}", id);
                return;
            }

            venueRepository.deleteById(id);
            logger.info("Success: deleteVenue() — Deleted venue with ID: {}", id);
        } catch (Exception e) {
            logger.error("Error in deleteVenue(): {}", e.getMessage(), e);
            throw e;
        }
    }
}
