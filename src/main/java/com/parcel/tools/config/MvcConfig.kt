package com.parcel.tools.config;

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter

@Configuration
open class MvcConfig : WebMvcConfigurerAdapter() {

    override fun addViewControllers(registry: ViewControllerRegistry) {
        registry.addViewController("/base").setViewName("login")
        //registry.addViewController("/hello").setViewName("hello")
        registry.addViewController("/login").setViewName("login")
    }

}

