package io.github.tuyendev.mbs.common.entity.rdb;

import java.io.Serializable;

import io.github.tuyendev.mbs.common.entity.AbstractAuditable;

import org.springframework.data.annotation.Id;

public abstract class AbstractJdbcEntity<ID extends Serializable> extends AbstractAuditable<Long> {
	@Id
	protected ID id;

	public ID getId() {
		return id;
	}

	public void setId(ID id) {
		this.id = id;
	}
}
