package io.github.tuyendev.mbs.common.annotation.context;

import java.util.Map;

public interface FeaturePrivilegeClaim {

	/**
	 * Not changeable field, change name will cause problems to running app
	 * @return
	 */
	String getName();

	/**
	 * A changeable field, can be updated to re-initial
	 * @return
	 */
	String getDescription();

	/**
	 * A changeable field, can be updated to re-initial
	 * @return
	 */
	Map<String, String> getPrivileges();
}
