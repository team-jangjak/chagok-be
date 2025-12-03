package com.jangjak.chagok.admin.repository;

import com.jangjak.chagok.admin.entity.Admin;
import com.jangjak.chagok.common.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    boolean existsByIdAndRole(Long id, Role role);
}
