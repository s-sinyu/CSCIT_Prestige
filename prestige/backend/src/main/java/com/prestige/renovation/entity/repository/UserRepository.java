package com.prestige.renovation.entity.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.prestige.renovation.entity.Role;
import com.prestige.renovation.entity.User;

public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {

    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Integer id);

    boolean existsByRole(Role role);

    @Query("select u.role, count(u) from User u where u.createdAt >= :from and u.createdAt < :to group by u.role")
    List<Object[]> countRegistrationsByRole(@Param("from") Instant from, @Param("to") Instant to);
}
