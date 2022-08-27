package io.github.tuyendev.mbs.common.entity.rdb;

import io.github.tuyendev.mbs.common.CommonConstants;
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
@Table(value = CommonConstants.EntityName.ROLE_AUTHORITY)
public class AuthorityRoleRef implements Serializable {
    private Long roleId;
}
