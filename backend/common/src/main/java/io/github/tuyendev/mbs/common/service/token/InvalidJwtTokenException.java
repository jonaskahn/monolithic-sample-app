package io.github.tuyendev.mbs.common.service.token;

import org.springframework.security.core.AuthenticationException;

import static io.github.tuyendev.mbs.common.message.Translator.eval;

public class InvalidJwtTokenException extends AuthenticationException {

	public InvalidJwtTokenException() {
		super(eval("app.auth.exception.token-not-valid"));
	}
}
