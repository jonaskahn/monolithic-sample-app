package io.github.tuyendev.mbs.common;

public abstract class CommonConstants {
	public static abstract class EntityName {
		public static final String ROLE = "roles";

		public static final String USER = "users";

		public static final String REFRESH_TOKEN = "refresh_tokens";

		public static final String ACCESS_TOKEN = "access_tokens";

		public static final String USER_ROLE = "user_roles";

		public static final String ROLE_AUTHORITY = "role_authorities";

		public static final String AUTHORITY = "authorities";

		public static final String GROUP = "groups";

		public static final String GROUP_MEMBER = "group_members";
	}

	public static abstract class EntityStatus {
		public static final Integer ACTIVE = 1;

		public static final Integer INACTIVE = 0;

		public static final Integer LOCK = 2;

		public static final Integer DELETED = 9;

		public static final Integer ENABLED = 1;

		public static final Integer DISABLED = 0;

		public static final Integer VERIFIED = 1;

		public static final Integer UNVERIFIED = 0;
	}


	public static abstract class TokenAudience {
		public static final String ACCESS_TOKEN = "ACCTN";

		public static final String REFRESH_TOKEN = "REFTN";
	}
}
