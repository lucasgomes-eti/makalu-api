package eti.lucasgomes.makalu.features.stores

import eti.lucasgomes.makalu.features.stores.model.CategoryRequest
import eti.lucasgomes.makalu.features.stores.model.CategoryResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/categories")
class CategoryController(private val categoryService: CategoryService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody request: CategoryRequest): CategoryResponse {
        val category = categoryService.create(request)
        return CategoryResponse(category.id, category.description)
    }

    @GetMapping
    fun findAll(): List<CategoryResponse> {
        val categories = categoryService.findAll()
        return categories.map { CategoryResponse(it.id, it.description) }
    }
}