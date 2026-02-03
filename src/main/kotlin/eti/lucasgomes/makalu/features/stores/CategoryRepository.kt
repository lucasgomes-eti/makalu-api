package eti.lucasgomes.makalu.features.stores

import eti.lucasgomes.makalu.features.stores.model.CategoryEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CategoryRepository : JpaRepository<CategoryEntity, Long>