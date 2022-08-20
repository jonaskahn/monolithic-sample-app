package io.github.tuyendev.mbs.common.entity.rdb;

import java.io.Serializable;

import io.github.tuyendev.mbs.common.CommonConstants.EntityName;
import lombok.Data;

import org.springframework.data.relational.core.mapping.Table;

@Data
@Table(value = EntityName.GROUP_MEMBER)
class UserGroupRef implements Serializable {
	private Long groupId;
}
