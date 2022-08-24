package io.github.tuyendev.mbs.common;


import java.util.Map;

import io.github.tuyendev.mbs.common.annotation.Modular;
import io.github.tuyendev.mbs.common.annotation.context.FeaturePrivilegeClaim;
import io.github.tuyendev.mbs.common.annotation.context.MessageResourceClaim;

import org.springframework.context.annotation.PropertySource;

@Modular
@PropertySource({"classpath:common00.properties"})
public class CommonModular implements MessageResourceClaim, FeaturePrivilegeClaim {
	@Override
	public String[] messageSource() {
		return new String[] {"classpath:common-messages"};
	}

	@Override
	public String getName() {
		return "COMMON";
	}

	@Override
	public String getDescription() {
		return "Access to common resource";
	}

	@Override
	public Map<String, String> getPrivileges() {
		return Map.of("COMMON_READ", "app.common.message.privilege.read");
	}
}
