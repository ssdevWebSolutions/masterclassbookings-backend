package com.ssdevcheckincheckout.ssdev.Backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Venues {
	
	 @Id
	    @GeneratedValue(strategy=GenerationType.IDENTITY)
	    private Long id;

	    @Column(nullable = false)
	    private String venueName;

	    @Column(nullable = false)
	    private String addressLine1;

	  
	    private String addressLine2;

	    @Column(nullable = false)
	    private String town;

	    @Column(nullable = false)
	    private String postcode;

	   
	    private String venueNotes;


		public Long getId() {
			return id;
		}


		public void setId(Long id) {
			this.id = id;
		}


		public String getVenueName() {
			return venueName;
		}


		public void setVenueName(String venueName) {
			this.venueName = venueName;
		}


		public String getAddressLine1() {
			return addressLine1;
		}


		public void setAddressLine1(String addressLine1) {
			this.addressLine1 = addressLine1;
		}


		public String getAddressLine2() {
			return addressLine2;
		}


		public void setAddressLine2(String addressLine2) {
			this.addressLine2 = addressLine2;
		}


		public String getTown() {
			return town;
		}


		public void setTown(String town) {
			this.town = town;
		}


		public String getPostcode() {
			return postcode;
		}


		public void setPostcode(String postcode) {
			this.postcode = postcode;
		}


		public String getVenueNotes() {
			return venueNotes;
		}


		public void setVenueNotes(String venueNotes) {
			this.venueNotes = venueNotes;
		}


		public Venues(Long id, String venueName, String addressLine1, String addressLine2, String town, String postcode,
				String venueNotes) {
			super();
			this.id = id;
			this.venueName = venueName;
			this.addressLine1 = addressLine1;
			this.addressLine2 = addressLine2;
			this.town = town;
			this.postcode = postcode;
			this.venueNotes = venueNotes;
		}


		public Venues() {
			super();
			// TODO Auto-generated constructor stub
		}


		@Override
		public String toString() {
			return "Venues [id=" + id + ", venueName=" + venueName + ", addressLine1=" + addressLine1
					+ ", addressLine2=" + addressLine2 + ", town=" + town + ", postcode=" + postcode + ", venueNotes="
					+ venueNotes + "]";
		}
	    
	    

}
