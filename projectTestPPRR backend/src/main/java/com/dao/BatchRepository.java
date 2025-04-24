package com.dao;

import java.util.List; 

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.model.Batch;

public interface BatchRepository extends JpaRepository<Batch, Integer>
{
	@Query("select b from Batch b where b.bname =:bn")
	public List<Batch> findByName(@Param("bn")String bn);
	
	@Query("SELECT b FROM Batch b JOIN b.profiles p WHERE p.id = :profileId")
	List<Batch> findBatchesByProfileId(@Param("profileId")int profileId);
	
	// Define a custom query to find batches associated with a profile while excluding a specific batch ID
    @Query("SELECT b FROM Batch b JOIN b.profiles p WHERE p.id = :profileId AND b.bid != :excludeBatchId")
    List<Batch> findBatchesByProfileIdAndExcludingBatchId(@Param("profileId") int profileId, @Param("excludeBatchId")int excludeBatchId);
    
    @Query("SELECT b FROM Batch b JOIN b.profiles p WHERE p.id = :profileId AND b.bid != :excludeBatchId AND b.end_date > CURRENT_DATE")
    List<Batch> findActiveBatchesByProfileIdAndExcludingBatchId(@Param("profileId")int profileId,@Param("excludeBatchId") int excludeBatchId);

    @Query("SELECT b FROM Batch b JOIN b.profiles p WHERE p.id = :profileId AND b.end_date > CURRENT_DATE")
    List<Batch> findActiveBatchesByProfileId(@Param("profileId")int profileId);

	
	//List<Batch> findByTrainersId(int trainerId);
	
/*	
	@Query("select b.bname from Batch b join b.trainers t where t.id = :trainerId")
	// @Query("select b.bname from Batch b ( select  where t.id = :trainerId") )
	 List<String> findBatchNamesByTrainerId(@Param("trainerId") int trainerId);
*/	 
}
