package io.github.tuyendev.mbs.common.service.role;

import io.github.tuyendev.mbs.common.exception.LogicException;

public class RoleNotFoundException extends LogicException {
	public RoleNotFoundException() {
		super("app.role.exception.not-found");
	}
}
