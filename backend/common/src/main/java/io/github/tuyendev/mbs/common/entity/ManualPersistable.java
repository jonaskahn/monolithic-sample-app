package io.github.tuyendev.mbs.common.entity;

import java.io.Serializable;
import java.util.Objects;

import lombok.Data;

import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;

@Data
public abstract class ManualPersistable<ID> implements Persistable<ID>, Serializable {

	@Transient
	protected Boolean newEntity;

	@Override
	public abstract ID getId();

	@Override
	public boolean isNew() {
		return Objects.isNull(newEntity) ? Objects.isNull(getId()) : newEntity;
	}
}
