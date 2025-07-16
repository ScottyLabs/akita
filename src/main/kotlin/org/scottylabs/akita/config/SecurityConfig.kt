package org.scottylabs.akita.config

import org.scottylabs.akita.security.SlabsServerAuthenticationSuccessHandler
import org.scottylabs.akita.security.SlabsServerOauth2AuthorizationRequestResolver
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizationRequestResolver
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository
import org.springframework.security.oauth2.client.web.server.WebSessionServerOAuth2AuthorizedClientRepository
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler
import org.springframework.security.web.server.authentication.logout.SecurityContextServerLogoutHandler
import org.springframework.security.web.server.authentication.logout.ServerLogoutHandler
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsConfigurationSource
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebFluxSecurity
class SecurityConfig(
    @Value("\${app.auth.allowed-origins}") val origins: String,
    private val clientRegistrationRepository: ReactiveClientRegistrationRepository,
) {
    @Bean
    fun authorizedClientRepository(): ServerOAuth2AuthorizedClientRepository {
        return WebSessionServerOAuth2AuthorizedClientRepository()
    }

    @Bean
    fun authenticationSuccessHandler(): ServerAuthenticationSuccessHandler {
        return SlabsServerAuthenticationSuccessHandler()
    }

    @Bean
    fun authorizationRequestResolver(): ServerOAuth2AuthorizationRequestResolver {
        return SlabsServerOauth2AuthorizationRequestResolver(clientRegistrationRepository)
    }

    @Bean
    fun serverLogoutSuccessHandler(): ServerLogoutSuccessHandler {
        return OidcClientInitiatedServerLogoutSuccessHandler(clientRegistrationRepository)
    }

    @Bean
    fun serverLogoutHandler(): ServerLogoutHandler {
        return SecurityContextServerLogoutHandler()
    }

    @Bean
    fun securityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        http.formLogin { it.disable() }

        http.oauth2Login {
            it.authorizationRequestResolver(authorizationRequestResolver())
            it.authenticationSuccessHandler(authenticationSuccessHandler())
        }

        http.logout {
            it.logoutSuccessHandler(serverLogoutSuccessHandler())
            it.logoutHandler(serverLogoutHandler())
        }

        http.authorizeExchange { it.anyExchange().permitAll() }

        http.csrf{ it.disable() }

        http.cors { it.configurationSource(corsConfigurationSource()) }

        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()

        configuration.allowedOriginPatterns = origins.split(",")
        configuration.allowedMethods = listOf("*")
        configuration.allowedHeaders = listOf("*")
        configuration.exposedHeaders = listOf("*")
        configuration.allowCredentials = true

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

}
