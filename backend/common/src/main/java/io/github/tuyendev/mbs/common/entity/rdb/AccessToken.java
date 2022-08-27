package io.github.tuyendev.mbs.common.entity.rdb;

import io.github.tuyendev.mbs.common.CommonConstants.EntityName;
import io.github.tuyendev.mbs.common.entity.ManualPersistable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@Table(value = EntityName.ACCESS_TOKEN)
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
    }

    public AccessToken(String id, Long userId, String token, Integer status, LocalDateTime expiredAt, RefreshToken refreshToken) {
        this.id = id;
        this.userId = userId;
        this.token = token;
        this.status = status;
        this.expiredAt = expiredAt;
        this.refreshToken = refreshToken;
    }

    public AccessToken(String id, Long userId, String token, Integer status, LocalDateTime expiredAt, RefreshToken refreshToken, Boolean newEntity) {
        this.id = id;
        this.userId = userId;
        this.token = token;
        this.status = status;
        this.expiredAt = expiredAt;
        this.refreshToken = refreshToken;
        this.newEntity = newEntity;
    }

    public static AccessTokenBuilder builder() {
        return new AccessTokenBuilder();
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

    public static class AccessTokenBuilder {
        private String id;

        private Long userId;

        private String token;

        private Integer status;

        private LocalDateTime expiredAt;

        private RefreshToken refreshToken;

        private Boolean newEntity;

        AccessTokenBuilder() {
        }

        public AccessTokenBuilder id(final String id) {
            this.id = id;
            return this;
        }

        public AccessTokenBuilder userId(final Long userId) {
            this.userId = userId;
            return this;
        }

        public AccessTokenBuilder token(final String token) {
            this.token = token;
            return this;
        }

        public AccessTokenBuilder status(final Integer status) {
            this.status = status;
            return this;
        }

        public AccessTokenBuilder expiredAt(final LocalDateTime expiredAt) {
            this.expiredAt = expiredAt;
            return this;
        }

        public AccessTokenBuilder refreshToken(final RefreshToken refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }

        public AccessTokenBuilder newEntity() {
            this.newEntity = Boolean.TRUE;
            return this;
        }

        public AccessToken build() {
            return new AccessToken(this.id, this.userId, this.token, this.status, this.expiredAt, this.refreshToken, this.newEntity);
        }

        @Override
        public String toString() {
            return "AccessTokenBuilder{" +
                    "id='" + id + '\'' +
                    ", userId=" + userId +
                    ", token='" + token + '\'' +
                    ", status=" + status +
                    ", expiredAt=" + expiredAt +
                    ", refreshToken=" + refreshToken +
                    ", newEntity=" + newEntity +
                    '}';
        }
    }
}
