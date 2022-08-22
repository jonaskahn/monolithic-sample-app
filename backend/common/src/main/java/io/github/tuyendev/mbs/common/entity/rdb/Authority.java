package io.github.tuyendev.mbs.common.entity.rdb;

import java.util.Objects;

import io.github.tuyendev.mbs.common.CommonConstants.EntityName;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@ToString
@Table(value = EntityName.AUTHORITY)
@Builder
public class Authority extends AbstractJdbcEntity<Long> {

	private Long featureId;

	private String name;

	private String description;

	private Integer status;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Authority authority = (Authority) o;
		return Objects.equals(featureId, authority.featureId) && Objects.equals(name, authority.name) && Objects.equals(description, authority.description) && Objects.equals(status, authority.status);
	}

	@Override
	public int hashCode() {
		return Objects.hash(featureId, name, description, status);
	}
}
