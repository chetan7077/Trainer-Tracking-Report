package com.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dao.BatchRepository;
import com.dao.TopicRepository;
import com.model.Batch;
import com.model.Topic;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Service
public class TopicServiceImpt implements TopicService {

	@Autowired
	TopicRepository topicRepo;
	
	@Autowired
    EntityManager entityManager; 
	
	@Override
	public Topic addTopic(Topic t) 
	{
		
		// Calculating the sum of subtopic hours
        double subtopicHoursSum = t.getSubtopic().stream()
                                .mapToDouble(subtopic -> subtopic.getShours())
                                .sum();
        
        // Assigning the sum to the topic's hours
        t.setHours((float) (t.getHours() + subtopicHoursSum));
        
        // Save the topic
        return topicRepo.save(t);
	}
	
	public Topic getOneTopic(int id)
	{
		return topicRepo.findById(id).orElse(null);
	}
	
	public List<Topic> getAllTopics()
	{
		return topicRepo.findAll();
	}
	
	public Topic updateTopic(Topic t) {
	    // Retrieve the existing topic from the database
	    Topic existingTopic = topicRepo.findById(t.getTid()).orElse(null);
	    
	    // Check if the existing topic exists
	    if (existingTopic != null) {
	        // Update the properties of the existing topic
	        existingTopic.setTname(t.getTname());
	        existingTopic.setSubtopic(t.getSubtopic());

	        // Calculate the sum of subtopic hours
	        double subtopicHoursSum = t.getSubtopic().stream()
	                                    .mapToDouble(subtopic -> subtopic.getShours())
	                                    .sum();

	        // Deduct the existing topic's hours from the total sum of subtopic hours
	        subtopicHoursSum -= existingTopic.getHours();

	        // Add the updated sum to the existing topic's hours
	        existingTopic.setHours((float) (existingTopic.getHours() + subtopicHoursSum));

	        // Save the updated topic
	        return topicRepo.save(existingTopic);
	    } else {
	        // Handle the case where the existing topic is not found
	        return null; // or throw an exception, depending on requirements
	    }
	}

	
	@Transactional //
	public List<Topic> deleteTopic(int id)
	{
		// Deleting related records from the topic_subtopic table using native SQL query
        Query query = entityManager.createNativeQuery("DELETE FROM course_topic WHERE topic_tid = :id");
        query.setParameter("id", id);
        query.executeUpdate();
		
        Topic Topic = topicRepo.findById(id).orElse(null);
        if (Topic != null) {
         
            topicRepo.delete(Topic);
        }

        return topicRepo.findAll();
        
       /* 
		subtopicRepo.deleteById(id); 
		return subtopicRepo.findAll();
	  */
	}

}
