package eti.lucasgomes.makalu.support

import eti.lucasgomes.makalu.features.address.AddressEntity
import eti.lucasgomes.makalu.features.address.AddressRepository
import eti.lucasgomes.makalu.features.auth.JwtService
import eti.lucasgomes.makalu.features.auth.UserRepository
import eti.lucasgomes.makalu.features.auth.model.UserEntity
import eti.lucasgomes.makalu.features.cart.CartItemRepository
import eti.lucasgomes.makalu.features.cart.CartRepository
import eti.lucasgomes.makalu.features.cart.model.CartEntity
import eti.lucasgomes.makalu.features.cart.model.CartItemEntity
import eti.lucasgomes.makalu.features.menu.MenuItemsRepository
import eti.lucasgomes.makalu.features.menu.model.MenuItemEntity
import eti.lucasgomes.makalu.features.stores.StoreRepository
import eti.lucasgomes.makalu.features.stores.model.StoreEntity
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.PrecisionModel
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.concurrent.atomic.AtomicLong

/** Builds the rows an order needs. Every call creates fresh, unique data. */
@Component
class TestData(
    private val userRepository: UserRepository,
    private val storeRepository: StoreRepository,
    private val addressRepository: AddressRepository,
    private val menuItemsRepository: MenuItemsRepository,
    private val cartRepository: CartRepository,
    private val cartItemRepository: CartItemRepository,
    private val jwtService: JwtService
) {
    private val sequence = AtomicLong(System.nanoTime() % 1_000_000_000)
    private val geometry = GeometryFactory(PrecisionModel(), 4326)

    private fun point(): Point = geometry.createPoint(Coordinate(-46.63, -23.55))

    fun user(): UserEntity {
        val n = sequence.incrementAndGet()
        return userRepository.save(
            UserEntity(
                name = "User $n",
                email = "user$n@example.com",
                phoneNumber = n.toString().padStart(11, '0').takeLast(15),
                passwordHash = "not-a-real-hash",
                profileImageId = null
            )
        )
    }

    fun bearer(user: UserEntity): String = "Bearer " + jwtService.generateAccessToken(user.id.toString())

    fun store(owner: UserEntity): StoreEntity = storeRepository.save(
        StoreEntity(
            name = "Store ${sequence.incrementAndGet()}",
            categories = mutableListOf(),
            logoImageId = null,
            coverImageId = null,
            ownerUserId = owner.id,
            location = point(),
            deliveryFee = BigDecimal("5.00")
        )
    )

    /** A customer with an address and a one-item cart at [store], ready to order. */
    fun cartFor(customer: UserEntity, store: StoreEntity): CartEntity {
        addressRepository.save(
            AddressEntity(
                zipCode = "01000-000",
                street = "Test Street",
                number = "1",
                complement = null,
                location = point(),
                ownerUserId = customer.id
            )
        )
        val menuItem = menuItemsRepository.save(
            MenuItemEntity(
                storeId = store.id,
                category = "Main",
                name = "Dish",
                price = BigDecimal("20.00"),
                ingredients = null,
                imageId = null
            )
        )
        val cart = cartRepository.save(CartEntity(id = 0, ownerId = customer.id, store = store, items = emptyList()))
        cartItemRepository.save(CartItemEntity(cart = cart, menuItem = menuItem))
        return cart
    }
}
