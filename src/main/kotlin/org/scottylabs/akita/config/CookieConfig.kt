/*
 * Copyright 2014-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.scottylabs.akita.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.server.session.CookieWebSessionIdResolver
import org.springframework.web.server.session.WebSessionIdResolver
import java.time.Duration

/**
 * @author Eleftheria Stein
 */
@Configuration
class CookieConfig {

    @Bean
    fun webSessionIdResolver(): WebSessionIdResolver {
        val resolver = CookieWebSessionIdResolver()
        resolver.setCookieName("SESSION") // Use SESSION instead of JSESSIONID
        resolver.addCookieInitializer { builder -> builder.path("/") }
        resolver.addCookieInitializer { builder -> builder.sameSite("None") } // Allow cross-site
        resolver.addCookieInitializer { builder -> builder.secure(true) } // HTTPS only
        resolver.addCookieInitializer { builder -> builder.httpOnly(true) } // Prevent JS access
        resolver.addCookieInitializer { builder -> builder.maxAge(Duration.ofDays(365)) } // 1 year persistence
        return resolver
    }
}
