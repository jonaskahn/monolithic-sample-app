package io.github.tuyendev.mbs.common.entity.rdb;

import java.time.LocalDateTime;
import java.util.Objects;

import io.github.tuyendev.mbs.common.CommonConstants.EntityName;
import io.github.tuyendev.mbs.common.entity.ManualPersistable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@ToString
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
	}

	public RefreshToken(String id, String token, String accessTokenId, LocalDateTime expiredAt, Integer status, Boolean newEntity) {
		this.id = id;
		this.token = token;
		this.accessTokenId = accessTokenId;
		this.expiredAt = expiredAt;
		this.status = status;
		this.newEntity = newEntity;
	}

	public static RefreshTokenBuilder builder() {
		return new RefreshTokenBuilder();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		RefreshToken that = (RefreshToken) o;
		return Objects.equals(id, that.id) && Objects.equals(token, that.token) && Objects.equals(expiredAt, that.expiredAt) && Objects.equals(status, that.status) && Objects.equals(newEntity, that.newEntity);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, token, expiredAt, status, newEntity);
	}

	@Override
	public boolean isNew() {
		return Objects.isNull(newEntity) ? Objects.isNull(getId()) : newEntity;
	}

	public static class RefreshTokenBuilder {
		protected String accessTokenId;

		private String id;

		private String token;

		private LocalDateTime expiredAt;

		private Integer status;

		RefreshTokenBuilder() {
		}

		public RefreshTokenBuilder id(final String id) {
			this.id = id;
			return this;
		}

		public RefreshTokenBuilder accessTokenId(final String accessTokenId) {
			this.accessTokenId = accessTokenId;
			return this;
		}

		public RefreshTokenBuilder token(final String token) {
			this.token = token;
			return this;
		}

		public RefreshTokenBuilder expiredAt(final LocalDateTime expiredAt) {
			this.expiredAt = expiredAt;
			return this;
		}

		public RefreshTokenBuilder status(final Integer status) {
			this.status = status;
			return this;
		}

		public RefreshToken build() {
			return new RefreshToken(this.id, this.token, this.accessTokenId, this.expiredAt, this.status, Boolean.TRUE);
		}

		public String toString() {
			return "RefreshToken.RefreshTokenBuilder(id=" + this.id + ", token=" + this.token + ", expiredAt=" + this.expiredAt + ", status=" + this.status + ")";
		}
	}
}
