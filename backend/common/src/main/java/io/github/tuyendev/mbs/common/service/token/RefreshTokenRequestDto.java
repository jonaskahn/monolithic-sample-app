package io.github.tuyendev.mbs.common.service.token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshTokenRequestDto implements Serializable {

    @NotNull(message = "{app.auth.validation.required-refresh-token}")
    private String refreshToken;
}
