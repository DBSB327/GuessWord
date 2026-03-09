package com.pm.guessword.repository;

import com.pm.guessword.enums.Role;
import com.pm.guessword.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);
    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameIgnoreCase(String username);

    Page<User> findByUsernameContainingIgnoreCase(String username, Pageable pageable);

    Page<User> findByRole(Role roleFilter, Pageable pageable);

    Page<User> findByUsernameContainingIgnoreCaseAndRole(String username, Role roleFilter, Pageable pageable);
}
