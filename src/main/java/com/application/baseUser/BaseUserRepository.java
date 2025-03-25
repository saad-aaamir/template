package com.application.baseUser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BaseUserRepository extends JpaRepository<BaseUser, Integer> {
    Boolean existsByEmail(String email);

    Optional<BaseUser> findByEmail(String email);

}
