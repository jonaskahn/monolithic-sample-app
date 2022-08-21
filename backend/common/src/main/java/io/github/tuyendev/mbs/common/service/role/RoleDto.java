package io.github.tuyendev.mbs.common.service.role;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

import io.github.tuyendev.mbs.common.CommonConstants;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoleDto implements Serializable {

	private Long id;

	private String name;

	private Long parentId;

	private RoleDto parent;

	private Map<String, String> authorities;

	String getReadAuthority() {
		return Objects.nonNull(authorities) ? authorities.get(CommonConstants.Privilege.READ_PREFIX) : null;
	}

	String getWriteAuthority() {
		return Objects.nonNull(authorities) ? authorities.get(CommonConstants.Privilege.WRITE_PREFIX) : null;
	}

	String getUpdateAuthority() {
		return Objects.nonNull(authorities) ? authorities.get(CommonConstants.Privilege.UPDATE_PREFIX) : null;
	}

	String getDeleteAuthority() {
		return Objects.nonNull(authorities) ? authorities.get(CommonConstants.Privilege.DELETE_PREFIX) : null;
	}
}
