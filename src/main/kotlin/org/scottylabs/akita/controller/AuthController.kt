package org.scottylabs.akita.controller

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

data class AuthStatusResponse(
    val authenticated: Boolean,
    val user: AuthUser?
)

data class AuthUser(
    val id: String,
    val email: String?,
    val name: String?,
    val picture: String?,
    val preferredUsername: String?
)

@RestController
@RequestMapping("/api/auth")
class AuthController {
    @GetMapping("/status")
    fun getAuthStatus(@AuthenticationPrincipal oidcUser: OidcUser?): Mono<AuthStatusResponse> {
        return if (oidcUser != null) {
            Mono.just(
                AuthStatusResponse(
                    authenticated = true,
                    user = AuthUser(
                        id = oidcUser.subject,
                        email = oidcUser.email,
                        name = oidcUser.fullName,
                        picture = oidcUser.picture,
                        preferredUsername = oidcUser.preferredUsername
                    )
                )
            )
        } else {
            Mono.just(AuthStatusResponse(authenticated = false, user = null))
        }
    }
}