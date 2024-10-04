package com.camelsoft.portal.repositories;


import com.camelsoft.portal.models.Role;
import com.camelsoft.portal.models.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findByName(RoleName role);

}

