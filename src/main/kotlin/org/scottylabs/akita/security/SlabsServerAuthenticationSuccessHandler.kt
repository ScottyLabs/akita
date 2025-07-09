package org.scottylabs.akita.security

import org.scottylabs.akita.security.SlabsOidcClientProperties.POST_AUTHENTICATION_SUCCESS_URI_SESSION_ATTRIBUTE
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.DefaultServerRedirectStrategy
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler
import org.springframework.web.server.WebSession
import reactor.core.publisher.Mono
import java.net.URI

class SlabsServerAuthenticationSuccessHandler : ServerAuthenticationSuccessHandler {
    private var defaultRedirectUri = URI.create("https://scottylabs.org")
    private val redirectStrategy = DefaultServerRedirectStrategy()

    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun onAuthenticationSuccess(
        webFilterExchange: WebFilterExchange,
        authentication: Authentication?
    ): Mono<Void> {
        return webFilterExchange.exchange.session.flatMap<Void> { session: WebSession ->
            val uri = session.getAttributeOrDefault(
                POST_AUTHENTICATION_SUCCESS_URI_SESSION_ATTRIBUTE,
                defaultRedirectUri
            )

            logger.debug("Login success. location: {}", uri.toString())
            redirectStrategy.sendRedirect(webFilterExchange.exchange, uri)
        }
    }
}