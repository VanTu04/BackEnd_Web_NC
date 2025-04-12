package com.vawndev.spring_boot_readnovel.Repositories;

import com.vawndev.spring_boot_readnovel.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> existsUserByEmail(String email);


    // đếm số lượng user đăng kí mới mới theo role trong tháng
    @Query("select count(u) from User u where exists (select 1 from u.roles r where r.name = :role) and month(u.createdAt) = :month and year(u.createdAt) = :year")
    Long CountNewUsersByRole(@Param("role") String role, @Param("month") int month, @Param("year") int year);
}