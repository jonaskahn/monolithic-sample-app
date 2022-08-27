package io.github.tuyendev.mbs.common.entity.rdb;

import io.github.tuyendev.mbs.common.CommonConstants.EntityName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;

@Data
@Table(value = EntityName.GROUP_MEMBER)
@NoArgsConstructor
@AllArgsConstructor
class UserGroupRef implements Serializable {

    private Long groupId;
}
