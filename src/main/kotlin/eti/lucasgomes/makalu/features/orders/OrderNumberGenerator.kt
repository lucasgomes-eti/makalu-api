package eti.lucasgomes.makalu.features.orders

import com.privacylogistics.FF3Cipher
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class OrderNumberGenerator(
    @Value($$"${ff3-cipher.key}") private val ff3CipherKey: String,
    @Value($$"${ff3-cipher.tweak}") private val ff3CipherTweak: String
) {

    fun generate(
        userId: Long,
        storeId: Long,
        cartId: Long,
        menuItemId: Long,
        addressId: Long,
    ): String {
        val userIdSeed = userId.toString().last()
        val storeIdSeed = storeId.toString().last()
        val cartIdSeed = cartId.toString().last()
        val menuItemSeed = menuItemId.toString().last()
        val addressIdSeed = addressId.toString().last()
        val randomSeed = Random.nextInt(0, 9).toString().last()

        val cipher = FF3Cipher(ff3CipherKey, ff3CipherTweak)
        val plainText = buildString {
            append(userIdSeed)
            append(storeIdSeed)
            append(cartIdSeed)
            append(menuItemSeed)
            append(addressIdSeed)
            append(randomSeed)
        }

        return cipher.encrypt(plainText)
    }
}