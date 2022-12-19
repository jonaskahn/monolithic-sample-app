package io.github.tuyendev.mbs.common.service.user;

import io.github.tuyendev.mbs.common.entity.rdb.User;

public interface ViewUserService {

	User findActiveUserById(final Long id);

	User findActiveUserByPreferredUsername(final String preferredUsername);

	User findUserByUsername(final String username);

	User findUserByEmail(final String email);
}
