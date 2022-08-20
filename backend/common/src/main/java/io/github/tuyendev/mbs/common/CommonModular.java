package io.github.tuyendev.mbs.common;


import io.github.tuyendev.mbs.common.annotation.Modular;
import io.github.tuyendev.mbs.common.annotation.context.MessageResourceClaim;

import org.springframework.context.annotation.PropertySource;

@Modular
@PropertySource({"classpath:common00.properties"})
public class CommonModular implements MessageResourceClaim {
	@Override
	public String[] messageSource() {
		return new String[] {"classpath:common-messages"};
	}
}
