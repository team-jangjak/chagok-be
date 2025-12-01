package com.jangjak.chagok.admin.repository;

import com.jangjak.chagok.admin.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    boolean existsByIdAndRoleIsAdmin(Long id);
}
