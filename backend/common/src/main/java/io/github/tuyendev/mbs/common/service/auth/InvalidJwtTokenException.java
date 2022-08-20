package io.github.tuyendev.mbs.common.service.auth;

import org.springframework.security.core.AuthenticationException;

import static io.github.tuyendev.mbs.common.message.Translator.eval;

public class InvalidJwtTokenException extends AuthenticationException {

	public InvalidJwtTokenException() {
		super(eval("TODO-ADD-KEY"));
	}
}
