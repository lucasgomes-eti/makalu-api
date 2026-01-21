package eti.lucasgomes.makalu.features.profile

import eti.lucasgomes.makalu.authenticatedUser
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/profile")
class ProfileController(private val profileMapper: ProfileMapper) {

    @GetMapping
    fun getSelfProfile(): ProfileResponse {
        return profileMapper.toResponse(authenticatedUser)
    }

}