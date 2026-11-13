package eti.lucasgomes.makalu.features.realtime

import eti.lucasgomes.makalu.authenticatedUser
import eti.lucasgomes.makalu.features.realtime.model.RealtimeCredentialsResponse
import org.springframework.http.CacheControl
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/orders")
class RealtimeController(private val realtimeService: RealtimeService) {

    /** For the customer who placed the order. */
    @GetMapping("/{orderId}/realtime")
    fun forOrder(@PathVariable orderId: Long): ResponseEntity<RealtimeCredentialsResponse> =
        notStored(realtimeService.credentialsForOrder(userId = authenticatedUser.id, orderId = orderId))

    /** For the owner of the store. */
    @GetMapping("/stores/{storeId}/realtime")
    fun forStore(@PathVariable storeId: Long): ResponseEntity<RealtimeCredentialsResponse> =
        notStored(realtimeService.credentialsForStore(userId = authenticatedUser.id, storeId = storeId))

    private fun notStored(credentials: RealtimeCredentialsResponse) =
        ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(credentials)
}
