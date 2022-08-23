package io.github.tuyendev.mbs.common.entity.rdb;

import java.time.LocalDateTime;

import io.github.tuyendev.mbs.common.CommonConstants.EntityName;
import io.github.tuyendev.mbs.common.entity.ManualPersistable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@ToString
@Builder
@Table(value = EntityName.REFRESH_TOKEN)
public class RefreshToken extends ManualPersistable<String> {

	@Id
	protected String id;

	protected String accessTokenId;

	@Transient
	private String token;

	private LocalDateTime expiredAt;

	private Integer status;

	public RefreshToken() {
		this.newEntity = Boolean.TRUE;
	}

	public RefreshToken(String id, String accessTokenId, String token, LocalDateTime expiredAt, Integer status) {
		this.id = id;
		this.accessTokenId = accessTokenId;
		this.token = token;
		this.expiredAt = expiredAt;
		this.status = status;
		this.newEntity = Boolean.TRUE;
	}
}
