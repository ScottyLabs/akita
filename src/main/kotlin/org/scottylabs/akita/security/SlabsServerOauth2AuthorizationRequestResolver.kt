package org.scottylabs.akita.security

import org.scottylabs.akita.security.SlabsOidcClientProperties.POST_AUTHENTICATION_SUCCESS_URI_PARAMETER
import org.scottylabs.akita.security.SlabsOidcClientProperties.POST_AUTHENTICATION_SUCCESS_URI_SESSION_ATTRIBUTE
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.net.URI

class SlabsServerOauth2AuthorizationRequestResolver(
    clientRegistrationRepository: ReactiveClientRegistrationRepository,
    authorizationRequestMatcher: ServerWebExchangeMatcher =
        PathPatternParserServerWebExchangeMatcher(DEFAULT_AUTHORIZATION_REQUEST_PATTERN)
) : DefaultServerOAuth2AuthorizationRequestResolver(clientRegistrationRepository, authorizationRequestMatcher) {
    override fun resolve(exchange: ServerWebExchange, clientRegistrationId: String): Mono<OAuth2AuthorizationRequest> {
        return super.resolve(exchange, clientRegistrationId).map { authorizationRequest ->
            val redirectUri = exchange.request.queryParams.getFirst(POST_AUTHENTICATION_SUCCESS_URI_PARAMETER)
            if (redirectUri != null) {
                exchange.session.flatMap { session ->
                    session.attributes[POST_AUTHENTICATION_SUCCESS_URI_SESSION_ATTRIBUTE] = URI.create(redirectUri)
                    Mono.just(authorizationRequest)
                }
            } else {
                Mono.just(authorizationRequest)
            }
        }.flatMap { it }
    }
}