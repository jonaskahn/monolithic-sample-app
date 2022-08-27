package io.github.tuyendev.mbs.common.service.user;

import io.github.tuyendev.mbs.common.entity.rdb.Role;
import io.github.tuyendev.mbs.common.entity.rdb.User;
import io.github.tuyendev.mbs.common.repository.rdb.UserRepository;
import io.github.tuyendev.mbs.common.service.role.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static io.github.tuyendev.mbs.common.message.Translator.eval;

@RequiredArgsConstructor
@Service
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;

    private final RoleService roleService;

    @Override
    public User findUserByUsername(String username) {
        User user = userRepo.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(eval("app.user.exception.not-found")));
        fulfillUserInfo(user);
        return user;
    }

    private void fulfillUserInfo(User user) {
        Set<Long> roleIds = user.roleIds();
        Set<Role> roles = roleService.findAllActiveByIds(roleIds);
        user.setRoles(roles);
    }

    @Override
    public User findUserByEmail(String email) {
        User user = userRepo.findUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(eval("app.user.exception.not-found")));
        fulfillUserInfo(user);
        return user;
    }

    public User findActiveUserById(Long userId) {
        User user = userRepo.findUserById(userId)
                .orElseThrow(() -> new UsernameNotFoundException(eval("app.user.exception.not-found")));
        fulfillUserInfo(user);
        return user;
    }
}
