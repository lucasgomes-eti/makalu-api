package eti.lucasgomes.makalu.features.stores

import eti.lucasgomes.makalu.features.stores.model.CategoryEntity
import eti.lucasgomes.makalu.features.stores.model.CategoryRequest
import org.springframework.stereotype.Service

@Service
class CategoryService(private val categoryRepository: CategoryRepository) {

    fun create(categoryRequest: CategoryRequest): CategoryEntity {
        return categoryRepository.save(CategoryEntity(description = categoryRequest.description))
    }

    fun findAll(): List<CategoryEntity> {
        return categoryRepository.findAll()
    }

    fun findAllByIds(ids: List<Long>): List<CategoryEntity> {
        return categoryRepository.findAllById(ids)
    }
}