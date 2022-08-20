package io.github.tuyendev.mbs.common.entity.rdb;

import java.io.Serializable;

import io.github.tuyendev.mbs.common.CommonConstants.EntityName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(value = EntityName.ROLE_AUTHORITY)
class AuthorityRoleRef implements Serializable {
	private Long roleId;
}
