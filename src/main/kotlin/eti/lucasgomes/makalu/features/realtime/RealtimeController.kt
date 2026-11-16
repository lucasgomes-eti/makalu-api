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

    @GetMapping("/{orderId}/realtime")
    fun credentialsForCustomerOrder(@PathVariable orderId: Long): ResponseEntity<RealtimeCredentialsResponse> =
        neverCached(
            realtimeService.credentialsForOrderPlacedByUser(userId = authenticatedUser.id, orderId = orderId)
        )

    @GetMapping("/stores/{storeId}/realtime")
    fun credentialsForStoreOwner(@PathVariable storeId: Long): ResponseEntity<RealtimeCredentialsResponse> =
        neverCached(
            realtimeService.credentialsForStoreOwnedByUser(userId = authenticatedUser.id, storeId = storeId)
        )

    private fun neverCached(credentials: RealtimeCredentialsResponse) =
        ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(credentials)
}
