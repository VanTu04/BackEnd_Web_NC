package com.vawndev.spring_boot_readnovel.Repositories;

import com.vawndev.spring_boot_readnovel.Entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);

    @Query("SELECT r FROM Role r WHERE :isAdmin = true OR r.name <> 'ADMIN'")
    Set<Role> findAllRoles(boolean isAdmin);

    default Set<Role> getRoles(){
        boolean isAdmin= SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getAuthorities().
                stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return findAllRoles(isAdmin);
    }
}