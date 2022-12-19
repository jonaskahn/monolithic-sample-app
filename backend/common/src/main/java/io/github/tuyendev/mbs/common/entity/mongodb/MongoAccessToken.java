package io.github.tuyendev.mbs.common.entity.mongodb;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Document
@Data
@Builder
public class MongoAccessToken implements Serializable {

	private String id;

	private Long userId;

	private Integer status;

	private LocalDateTime expiredAt;

	@DocumentReference(lazy = true)
	private MongoRefreshToken refreshToken;
}
