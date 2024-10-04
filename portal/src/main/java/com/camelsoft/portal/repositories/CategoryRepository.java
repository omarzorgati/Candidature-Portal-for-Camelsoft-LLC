package com.camelsoft.portal.repositories;

import com.camelsoft.portal.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
