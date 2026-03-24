package eti.lucasgomes.makalu.features.menu

import eti.lucasgomes.makalu.features.menu.model.MenuItemRequest
import eti.lucasgomes.makalu.features.menu.model.MenuItemResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/stores")
class MenuController(
    private val menuService: MenuService,
    private val menuMapper: MenuMapper
) {

    @PostMapping("/{storeId}/menu")
    @ResponseStatus(HttpStatus.CREATED)
    fun post(
        @Valid @RequestBody request: MenuItemRequest,
        @PathVariable storeId: Long
    ): MenuItemResponse {
        return menuMapper.toResponse(menuService.create(request = request, storeId = storeId))
    }

    @PutMapping("/{storeId}/menu/{menuItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun update(
        @Valid @RequestBody request: MenuItemRequest,
        @PathVariable storeId: Long,
        @PathVariable menuItemId: Long
    ): MenuItemResponse {
        return menuMapper.toResponse(menuService.update(request = request, storeId = storeId, id = menuItemId))
    }

    @GetMapping("/{storeId}/menu")
    fun get(
        @PathVariable storeId: Long
    ): List<MenuItemResponse> {
        return menuService.findByStoreId(storeId).map { menuMapper.toResponse(it) }
    }

    @DeleteMapping("/{storeId}/menu/{menuItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @PathVariable menuItemId: Long
    ) {
        menuService.delete(menuItemId)
    }
}