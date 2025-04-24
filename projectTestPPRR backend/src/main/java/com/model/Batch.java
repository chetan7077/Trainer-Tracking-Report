package com.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
public class Batch 
{
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int bid;
	
	@Column(name = "bname",unique=true)
	private String bname;
	
	@CreationTimestamp
	@Temporal(TemporalType.DATE)
	private Date creation_date;
	
	@Temporal(TemporalType.DATE)
	private Date end_date;

	@JsonBackReference	
	@OneToMany(mappedBy = "batch", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<TrainerSubTopicAssociation> trainerTopicAssociations;

	@PrePersist
	public void calculateEndDate() {
	    // Parse the creation date string
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E MMM dd HH:mm:ss z yyyy");
	    LocalDate parsedCreationDate = LocalDate.parse("Mon Apr 01 05:30:00 IST 2024", formatter);

	    // Add 4 months to the creation date
	    LocalDate tempEndDate = parsedCreationDate.plusMonths(4);

	    // Adjust to the next working day if it falls on a weekend
	    if (tempEndDate.getDayOfWeek() == DayOfWeek.SATURDAY) {
	        tempEndDate = tempEndDate.plusDays(2); // Adjust to Monday
	    } else if (tempEndDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
	        tempEndDate = tempEndDate.plusDays(1); // Adjust to Monday
	    }

	    // Convert LocalDate to Date for creation date
	    setCreation_date(Date.from(parsedCreationDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));

	    // Convert LocalDate to Date for end date
	    setEnd_date(Date.from(tempEndDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
	}

/*
	@ManyToMany(fetch=FetchType.EAGER)
	//@JsonBackReference
	@JoinTable(name = "batch_trainer",
    joinColumns = @JoinColumn(name = "batch_id"),
    inverseJoinColumns = @JoinColumn(name = "trainer_id"))
	private List<Trainer> trainers;
*/
	@ManyToMany(fetch = FetchType.EAGER)
	private List<Profile> profiles;
	
	public Batch() {
		super();
	}

	public Batch(int bid, String bname, List<Profile> profiles, Date creation_date, Date end_date) {
		super();
		this.bid = bid;
		this.bname = bname;
		this.profiles = profiles;
		this.creation_date = creation_date;
		this.end_date = end_date;
	}

	public int getBid() {
		return bid;
	}

	public void setBid(int bid) {
		this.bid = bid;
	}

	public String getBname() {
		return bname;
	}

	public void setBname(String bname) {
		this.bname = bname;
	}
	
	public Date getCreation_date() {
		return creation_date;
	}
	
	public void setCreation_date(Date creation_date) {
		this.creation_date = creation_date;
	}
	
	public Date getEnd_date() {
		return end_date;
	}

	public void setEnd_date(Date end_date) {
		this.end_date = end_date;
	}

	public void setProfiles(List<Profile> profiles) {
		this.profiles = profiles;
	}

	public List<Profile> getProfiles() {
		return profiles;
	}
	public List<TrainerSubTopicAssociation> getTrainerTopicAssociations() {
		return trainerTopicAssociations;
	}
	public void setTrainerTopicAssociations(List<TrainerSubTopicAssociation> trainerTopicAssociations) {
		this.trainerTopicAssociations = trainerTopicAssociations;
	}

	@Override
	public String toString() {
		return "Batch [bid=" + bid + ", bname=" + bname + ", profiles=" + profiles + "]";
	}

}
