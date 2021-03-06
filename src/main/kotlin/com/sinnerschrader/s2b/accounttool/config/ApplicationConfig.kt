package com.sinnerschrader.s2b.accounttool.config

import com.google.common.base.Predicates
import com.sinnerschrader.s2b.accounttool.config.ldap.LdapConfiguration
import com.sinnerschrader.s2b.accounttool.presentation.interceptor.GlobalMessageInterceptor
import com.sinnerschrader.s2b.accounttool.presentation.interceptor.LdapConnectionInterceptor
import com.sinnerschrader.s2b.accounttool.presentation.interceptor.RequestInterceptor
import com.sinnerschrader.s2b.accounttool.presentation.messaging.GlobalMessageFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter
import springfox.documentation.builders.RequestHandlerSelectors.basePackage
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.BasicAuth
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2
import java.util.*


@SpringBootApplication
@Configuration
@EnableAutoConfiguration
@EnableCaching
@EnableSwagger2
class ApplicationConfig() : WebMvcConfigurer {

    @Autowired
    private lateinit var environment: Environment

    @Autowired
    private lateinit var ldapConfiguration: LdapConfiguration

    @Autowired
    private lateinit var globalMessageFactory: GlobalMessageFactory

    override fun addInterceptors(registry: InterceptorRegistry) {
        with(registry) {
            addInterceptor(RequestInterceptor(environment))
            addInterceptor(LdapConnectionInterceptor(ldapConfiguration))
            addInterceptor(GlobalMessageInterceptor(globalMessageFactory))
        }
    }

    @Bean
    fun localeResolver() =
        with(AcceptHeaderLocaleResolver()) {
            defaultLocale = Locale.ENGLISH
            supportedLocales = listOf(Locale.ENGLISH, Locale.US)
            this
        }

    @Bean
    fun resourceUrlEncodingFilter() = ResourceUrlEncodingFilter()

    @Bean
    fun api() =
            Docket(DocumentationType.SWAGGER_2)
                    .apiInfo(ApiInfo(
                            "account-tool",
                            "",
                            "",
                            "",
                             ApiInfo.DEFAULT_CONTACT,
                            "",
                            "",
                            arrayListOf()))
                    .securitySchemes(listOf(BasicAuth("basicAuth")))
                    .forCodeGeneration(true)
                    .select()
                    .apis(Predicates.or(
                            basePackage("com.sinnerschrader.s2b.accounttool.presentation.controller.v2")))
                    .build()!!
}
