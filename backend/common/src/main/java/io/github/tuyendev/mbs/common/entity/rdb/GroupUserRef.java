package io.github.tuyendev.mbs.common.entity.rdb;

import java.io.Serializable;

import io.github.tuyendev.mbs.common.CommonConstants.EntityName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@ToString
@Table(value = EntityName.GROUP_MEMBER)
public class GroupUserRef implements Serializable {
	private Long userId;
}
