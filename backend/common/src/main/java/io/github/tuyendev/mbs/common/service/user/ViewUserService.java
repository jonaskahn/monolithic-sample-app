package io.github.tuyendev.mbs.common.service.user;

import io.github.tuyendev.mbs.common.entity.rdb.User;

public interface ViewUserService {

    User findActiveUserById(Long id);

    User findUserByUsername(String username);

    User findUserByEmail(String email);
}
