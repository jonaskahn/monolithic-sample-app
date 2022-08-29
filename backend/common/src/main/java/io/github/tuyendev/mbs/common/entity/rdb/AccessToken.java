package io.github.tuyendev.mbs.common.entity.rdb;

import io.github.tuyendev.mbs.common.CommonConstants.EntityName;
import io.github.tuyendev.mbs.common.entity.ManualPersistable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
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

    private Integer status;

    private LocalDateTime expiredAt;

    @MappedCollection(idColumn = "access_token_id")
    private RefreshToken refreshToken;

    public AccessToken() {
    }

    public AccessToken(String id, Long userId, Integer status, LocalDateTime expiredAt, Boolean newEntity) {
        this.id = id;
        this.userId = userId;
        this.status = status;
        this.expiredAt = expiredAt;
        this.newEntity = newEntity;
    }

    public static AccessTokenBuilder builder() {
        return new AccessTokenBuilder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccessToken that = (AccessToken) o;
        return Objects.equals(id, that.id) && Objects.equals(userId, that.userId) && Objects.equals(status, that.status) && Objects.equals(expiredAt, that.expiredAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, status, expiredAt);
    }

    public static class AccessTokenBuilder {
        private String id;

        private Long userId;

        private Integer status;

        private LocalDateTime expiredAt;

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


        public AccessTokenBuilder status(final Integer status) {
            this.status = status;
            return this;
        }

        public AccessTokenBuilder expiredAt(final LocalDateTime expiredAt) {
            this.expiredAt = expiredAt;
            return this;
        }

        public AccessTokenBuilder newEntity() {
            this.newEntity = Boolean.TRUE;
            return this;
        }

        public AccessToken build() {
            return new AccessToken(this.id, this.userId, this.status, this.expiredAt, this.newEntity);
        }

        @Override
        public String toString() {
            return "AccessTokenBuilder{" +
                    "id='" + id + '\'' +
                    ", userId=" + userId +
                    ", status=" + status +
                    ", expiredAt=" + expiredAt +
                    ", newEntity=" + newEntity +
                    '}';
        }
    }
}
