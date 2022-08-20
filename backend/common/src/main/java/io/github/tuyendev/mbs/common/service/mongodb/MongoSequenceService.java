package io.github.tuyendev.mbs.common.service.mongodb;

import java.util.Objects;

import io.github.tuyendev.mbs.common.entity.mongodb.MongoSequence;
import lombok.RequiredArgsConstructor;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
@RequiredArgsConstructor
public class MongoSequenceService {

	private final MongoOperations mongoOperations;

	public Long generateSequence(String seqName) {
		MongoSequence counter = mongoOperations.findAndModify(query(where("_id").is(seqName)),
				new Update().inc("seq", 1), options().returnNew(true).upsert(true),
				MongoSequence.class);
		return !Objects.isNull(counter) ? counter.getSeq() : 1L;
	}
}
