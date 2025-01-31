package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.auth.AuthUserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuthUserDao {

    AuthUserEntity createUser(AuthUserEntity authUserEntity);

    AuthUserEntity update (AuthUserEntity authUserEntity);

    Optional<AuthUserEntity> findById(UUID id);

    Optional<AuthUserEntity> findByUsername(String username);

    List<AuthUserEntity> findAll();

    void remove(AuthUserEntity user);
}
