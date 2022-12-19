package io.github.tuyendev.mbs.common.entity.rdb;

import java.io.Serializable;

import io.github.tuyendev.mbs.common.CommonConstants.EntityName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.relational.core.mapping.Table;

@Data
@Table(value = EntityName.GROUP_MEMBER)
@NoArgsConstructor
@AllArgsConstructor
class UserGroupRef implements Serializable {

	private Long groupId;
}
