package io.github.tuyendev.mbs.common.repository.rdb;

import io.github.tuyendev.mbs.common.entity.rdb.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findUserByEmail(final String email);

    Optional<User> findUserByUsername(final String username);

    Optional<User> findUserById(final Long id);

    Optional<User> findUserByPreferredUsername(final String preferredUsername);

    boolean existsByEmail(final String email);

    boolean existsByUsername(final String username);
}
