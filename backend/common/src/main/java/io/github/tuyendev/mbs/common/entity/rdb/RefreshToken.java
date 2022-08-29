package io.github.tuyendev.mbs.common.entity.rdb;

import io.github.tuyendev.mbs.common.CommonConstants.EntityName;
import io.github.tuyendev.mbs.common.entity.ManualPersistable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Table(value = EntityName.REFRESH_TOKEN)
public class RefreshToken extends ManualPersistable<String> {

    @Id
    protected String id;

    private String accessTokenId;

    private Long userId;

    private LocalDateTime expiredAt;

    private Integer status;

    public RefreshToken() {
    }

    public RefreshToken(String id, String accessTokenId, Long userId, LocalDateTime expiredAt, Integer status, Boolean newEntity) {
        this.id = id;
        this.accessTokenId = accessTokenId;
        this.userId = userId;
        this.expiredAt = expiredAt;
        this.status = status;
        this.newEntity = newEntity;
    }

    public static RefreshTokenBuilder builder() {
        return new RefreshTokenBuilder();
    }

    public static class RefreshTokenBuilder {
        private String id;

        private String accessTokenId;

        private Long userId;

        private LocalDateTime expiredAt;

        private Integer status;

        private Boolean newEntity;

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

        public RefreshTokenBuilder userId(final Long userId) {
            this.userId = userId;
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

        public RefreshTokenBuilder newEntity() {
            this.newEntity = Boolean.TRUE;
            return this;
        }

        public RefreshToken build() {
            return new RefreshToken(this.id, this.accessTokenId, this.userId, this.expiredAt, this.status, this.newEntity);
        }

        @Override
        public String toString() {
            return "RefreshTokenBuilder{" +
                    "id='" + id + '\'' +
                    ", accessTokenId='" + accessTokenId + '\'' +
                    ", userId=" + userId +
                    ", expiredAt=" + expiredAt +
                    ", status=" + status +
                    ", newEntity=" + newEntity +
                    '}';
        }
    }
}
