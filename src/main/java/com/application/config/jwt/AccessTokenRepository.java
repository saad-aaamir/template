package com.application.config.jwt;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccessTokenRepository extends CrudRepository<AccessToken, String> {
    Optional<AccessToken> findByUuid(String email);
    List<AccessToken> findAllByUuid(String token);
}
