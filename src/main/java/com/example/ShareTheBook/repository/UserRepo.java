package com.example.ShareTheBook.repository;

import com.example.ShareTheBook.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findUserEntityById(Long id);

    Optional<UserEntity> findUserByUsername(String username);

    Optional<UserEntity> findUserById(Long id);

    Boolean existsByUsername(String username);
}
