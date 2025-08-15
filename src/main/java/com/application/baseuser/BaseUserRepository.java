package com.application.baseuser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BaseUserRepository extends JpaRepository<BaseUser, Integer> {

    Optional<BaseUser> findByEmail(String email);

    BaseUser findByUuid(String userUuid);
}
