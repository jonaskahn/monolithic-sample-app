package io.github.tuyendev.mbs.common.service.token;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthorizationRequestDto implements Serializable {

	@NotNull(message = "{app.auth.validation.required-username}")
	private String username;

	@NotNull(message = "{app.auth.validation.required-password}")
	private String password;

	private boolean rememberMe;
}
