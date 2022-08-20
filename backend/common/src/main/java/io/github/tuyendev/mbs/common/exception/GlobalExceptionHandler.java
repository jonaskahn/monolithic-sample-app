package io.github.tuyendev.mbs.common.exception;

import javax.servlet.ServletException;

import io.github.tuyendev.mbs.common.response.Response;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler({RuntimeException.class})
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<Object> handleRuntimeException(RuntimeException ex) {
		return ResponseEntity.internalServerError().body(Response.failed(ex));
	}

	@ExceptionHandler({LogicException.class})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<Object> handleBusinessException(LogicException ex) {
		return ResponseEntity.badRequest().body(Response.failed(ex));
	}

	@ExceptionHandler({MethodArgumentNotValidException.class})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<Response> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
		return ResponseEntity.badRequest().body(Response.failed(ex));
	}

	@ExceptionHandler({AuthenticationException.class})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<Response> handleAuthenticationException(AuthenticationException ex) {
		return ResponseEntity.badRequest().body(Response.failed(ex));
	}

	@ExceptionHandler({DataAccessException.class})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<Response> handleDataAccessException(DataAccessException ex) {
		return ResponseEntity.badRequest().body(Response.failed(ex));
	}

	@ExceptionHandler({AccessDeniedException.class})
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ResponseEntity<Response> handleAccessDeniedException(AccessDeniedException ex) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.failed(ex));
	}

	@ExceptionHandler({HttpRequestMethodNotSupportedException.class})
	@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
	public ResponseEntity<Response> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
		return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(Response.failed(ex));
	}

	@ExceptionHandler({ServletException.class})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<Response> handleServletException(ServletException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Response.failed(ex));
	}

	@ExceptionHandler({Exception.class})
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<Response> handleUnexpectedException(Exception ex) {
		return ResponseEntity.internalServerError().body(Response.unexpected(ex));
	}

	@ExceptionHandler({Error.class})
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<Response> handleSystemError(Error ex) {
		return ResponseEntity.internalServerError().body(Response.error(ex));
	}
}
