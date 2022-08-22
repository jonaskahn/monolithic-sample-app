package io.github.tuyendev.mbs.common.repository.rdb;

import java.util.Optional;

import io.github.tuyendev.mbs.common.entity.rdb.Feature;

import org.springframework.data.repository.CrudRepository;

public interface FeatureRepository extends CrudRepository<Feature, Long> {

	boolean existsByName(String name);

	Optional<Feature> findFeatureByName(String name);
}
