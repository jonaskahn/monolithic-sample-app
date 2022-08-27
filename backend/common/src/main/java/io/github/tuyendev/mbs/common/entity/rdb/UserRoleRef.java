package io.github.tuyendev.mbs.common.entity.rdb;

import io.github.tuyendev.mbs.common.CommonConstants.EntityName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(value = EntityName.USER_ROLE)
class UserRoleRef implements Serializable {
    private Long roleId;
}
