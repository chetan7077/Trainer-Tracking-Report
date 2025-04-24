package com.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.dao.BatchRepository;
import com.dao.ProfileRepository;
import com.dao.TrainerRepository;
import com.model.Batch;
import com.model.Profile;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;	

@Service
public class BatchServiceImpt implements BatchService{

	@Autowired
	BatchRepository batchRepo;
	
	@Autowired
    ProfileRepository profileRepo;
	

    @Autowired
    TrainerRepository trainerRepository;
	
	@Autowired
    EntityManager entityManager; 
	
	@Transactional // Adding this annotation to ensure that the method runs within a transaction
	public Batch addBatch(Batch b) 
	{
		// Check if any of the profiles in the new batch are already associated with another batch
	    for (Profile profile : b.getProfiles()) {
	    	if (profileIsMappedToAnotherBatch(profile, b)) {
	    	    throw new IllegalStateException("Trainer " + profile.getFirstName() + " already have two batches running.");
	    	}
	    }

	    // Save the batch
	    Batch savedBatch = batchRepo.save(b);
	    return savedBatch;
	}
	
	public Batch getOneBatch(int id)
	{
		return batchRepo.findById(id).orElse(null);
	}
	
	public List<Batch> getAllBatch()
	{
		return batchRepo.findAll();
	}
	
	public List<Batch> findByName(String bname)
	{
		return batchRepo.findByName(bname);
	}
	
	@Transactional
	public Batch updateBatch(Batch updatedBatch) {
	    Batch existingBatch = batchRepo.findById(updatedBatch.getBid()).orElse(null);
	    if (existingBatch != null) {
	        // Update batch details if changed
	        if (updatedBatch.getBname() != null && !updatedBatch.getBname().equals(existingBatch.getBname())) {
	            existingBatch.setBname(updatedBatch.getBname());
	        }
	        if (updatedBatch.getCreation_date() != null && !updatedBatch.getCreation_date().equals(existingBatch.getCreation_date())) {
	            existingBatch.setCreation_date(updatedBatch.getCreation_date());
	        }
	        if (updatedBatch.getEnd_date() != null && !updatedBatch.getEnd_date().equals(existingBatch.getEnd_date())) {
	            existingBatch.setEnd_date(updatedBatch.getEnd_date());
	        }

	        // Check and add new profiles
	        for (Profile profile : updatedBatch.getProfiles()) {
	            // Check if the profile is already in the existing batch
	            boolean profileExists = existingBatch.getProfiles().stream()
	                    .anyMatch(p -> p.getId() == profile.getId());

	            if (!profileExists) {
	                // Check if the profile is associated with two batches
	                if (profileIsAssociatedWithMaxBatches(profile)) {
	                    throw new IllegalStateException("Trainer " + profile.getFirstName() + " already have two batches running.");
	                }
	                // Add the new profile to the batch
	                existingBatch.getProfiles().add(profile);
	            }
	        }

	        // Save the existing batch entity
	        return batchRepo.save(existingBatch);
	    } else {
	        // Handle the case where the existing batch is not found
	        return null; // or throw an exception, depending on requirements
	    }
	}

	private boolean profileIsAssociatedWithMaxBatches(Profile profile) {
	    // Query the database to check if the profile is associated with any batches
	    List<Batch> batches = batchRepo.findBatchesByProfileId(profile.getId());

	    // Filter batches based on end_date greater than current date
	    List<Batch> activeBatches = batches.stream()
	            .filter(batch -> batch.getEnd_date().after(new Date()))
	            .collect(Collectors.toList());

	    // If the profile is associated with more than two active batches, return true
	    return activeBatches.size() > 1;
	}

	private boolean profileIsMappedToAnotherBatch(Profile profile, Batch currentBatch) {
	    // Query the database to check if the profile is associated with any other active batch excluding the current batch
	    List<Batch> activeBatches = batchRepo.findActiveBatchesByProfileIdAndExcludingBatchId(profile.getId(), currentBatch.getBid());
	    
	    // If the profile is associated with more than one active batch, return true
	    return activeBatches.size() > 1;
	}

	@Transactional //
	public List<Batch> deleteBatch(int id)
	{
		// Deleting related records from the trainer_batch table using native SQL query
        Query query = entityManager.createNativeQuery("DELETE FROM batch_profiles WHERE batch_bid = :id");
        query.setParameter("id", id);
        query.executeUpdate();
	 	
        Batch Batch = batchRepo.findById(id).orElse(null);
        if (Batch != null) {
         
            batchRepo.delete(Batch);
        }

        return batchRepo.findAll();
        
       /* 
		subtopicRepo.deleteById(id); 
		return subtopicRepo.findAll();
	  */
	}
	@Transactional
	public List<Batch> deleteProfileFromBatch(int batchId, int profileId) {
	    // Get the batch
	    Batch batch = batchRepo.findById(batchId).orElse(null);
	    if (batch == null) {
	        throw new RuntimeException("Batch not found with id: " + batchId);
	    }

	    // Remove the profile from the batch
	    batch.getProfiles().removeIf(profile -> profile.getId() == profileId);

	    // Save the batch to reflect changes
	    batchRepo.save(batch);

	    return batchRepo.findAll(); // Return updated batch list
	}

	public List<Batch> getAssociatedBatches(int profileId)
	{
        return batchRepo.findBatchesByProfileId(profileId);
    }
}
/* @Transactional
 * The error message (TransactionRequiredException) suggests that the operation requires an active transaction,
 *  but none is present. This typically happens when trying to execute an update or delete query outside the scope of a transaction. 
*/