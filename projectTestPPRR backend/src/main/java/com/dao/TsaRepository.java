package com.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.model.Batch;
import com.model.SubTopic;
import com.model.TrainerSubTopicAssociation;

public interface TsaRepository extends JpaRepository<TrainerSubTopicAssociation,Integer>
{
	Optional<TrainerSubTopicAssociation> findByBatchAndSubtopic(Batch batch, SubTopic subtopic);
}
