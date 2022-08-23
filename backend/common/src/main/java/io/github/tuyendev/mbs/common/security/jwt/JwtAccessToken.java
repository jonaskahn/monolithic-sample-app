package io.github.tuyendev.mbs.common.security.jwt;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JwtAccessToken implements Serializable {
	private String type;

	private Long accessTokenExpiration;

	private String accessToken;

	private Long refreshTokenExpiration;

	private String refreshToken;
}
