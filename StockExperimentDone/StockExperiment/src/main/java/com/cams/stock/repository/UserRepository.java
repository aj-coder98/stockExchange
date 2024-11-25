package com.cams.stock.repository;

import com.cams.stock.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);
    Optional<User> findByPan(String pan);
    Optional<User> findByUsername(String username);
    Optional<User> findByPhone(String phone);
}
