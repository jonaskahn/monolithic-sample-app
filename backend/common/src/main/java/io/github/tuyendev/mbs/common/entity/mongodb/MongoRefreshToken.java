package io.github.tuyendev.mbs.common.entity.mongodb;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@Builder
public class MongoRefreshToken implements Serializable {

	private String id;

	private String accessTokenId;

	private Long userId;

	private Integer status;

	private LocalDateTime expiredAt;
}
