package io.github.tuyendev.mbs.common.entity.rdb;

import java.time.LocalDateTime;
import java.util.Objects;

import io.github.tuyendev.mbs.common.CommonConstants.EntityName;
import io.github.tuyendev.mbs.common.entity.ManualPersistable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@ToString
@Table(value = EntityName.ACCESS_TOKEN)
@Builder
public class AccessToken extends ManualPersistable<String> {

	@Id
	protected String id;

	private Long userId;

	@Transient
	private String token;

	private Integer status;

	private LocalDateTime expiredAt;

	@MappedCollection(idColumn = "access_token_id")
	private RefreshToken refreshToken;

	public AccessToken() {
		this.newEntity = Boolean.TRUE;
	}

	public AccessToken(String id, Long userId, String token, Integer status, LocalDateTime expiredAt, RefreshToken refreshToken) {
		this.id = id;
		this.userId = userId;
		this.token = token;
		this.status = status;
		this.expiredAt = expiredAt;
		this.refreshToken = refreshToken;
		this.newEntity = Boolean.TRUE;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		AccessToken that = (AccessToken) o;
		return Objects.equals(id, that.id) && Objects.equals(userId, that.userId) && Objects.equals(token, that.token) && Objects.equals(status, that.status) && Objects.equals(expiredAt, that.expiredAt) && Objects.equals(refreshToken, that.refreshToken);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, userId, token, status, expiredAt);
	}
}
