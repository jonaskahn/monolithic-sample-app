package io.github.tuyendev.mbs.common.service.auth;

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
public class RefreshTokenRequestDto implements Serializable {

	@NotNull(message = "{TODO-ADD-MESSAGE}")
	private String refreshToken;
}
