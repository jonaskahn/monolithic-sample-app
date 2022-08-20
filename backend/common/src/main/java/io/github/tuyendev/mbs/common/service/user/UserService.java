package io.github.tuyendev.mbs.common.service.user;

import io.github.tuyendev.mbs.common.entity.rdb.User;

public interface UserService extends ViewUserService {

	User findActiveUserById(Long id);
}
