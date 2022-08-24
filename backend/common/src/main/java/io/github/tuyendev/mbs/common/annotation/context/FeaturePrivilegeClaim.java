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
	 * A changeable field, can be updated to re-initial.
	 * Privileges presented by a map that contain (name as key, description as value)
	 * The name of each Privilege should be unique in the whole system.
	 * @return
	 */
	Map<String, String> getPrivileges();
}
