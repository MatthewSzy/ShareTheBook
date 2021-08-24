package com.example.ShareTheBook.repository;

import com.example.ShareTheBook.entity.RoleEntity;
import com.example.ShareTheBook.entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepo extends JpaRepository<RoleEntity, Long> {

    Optional<RoleEntity> findRoleByName(Roles name);
}
