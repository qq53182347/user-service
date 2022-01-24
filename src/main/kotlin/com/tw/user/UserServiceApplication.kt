@file:Suppress("SpreadOperator")
package com.tw.user

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import sun.misc.Unsafe
import java.lang.reflect.Field
import java.util.*

@SpringBootApplication
class UserServiceApplication

fun main(args: Array<String>) {
	disableWarning()
	TimeZone.setDefault(TimeZone.getTimeZone("Etc/UTC"))
	runApplication<UserServiceApplication>(*args)
}

@Bean
fun createRestApi(): Docket {
	return Docket(DocumentationType.SWAGGER_2)
		.select()
		.apis(RequestHandlerSelectors.basePackage("com.tw.user.controller"))
		.build()
}

fun disableWarning() {
	val theUnsafe: Field = Unsafe::class.java.getDeclaredField("theUnsafe")
	theUnsafe.isAccessible = true
	val unsafe: Unsafe = theUnsafe.get(null) as Unsafe
	val loader = Class.forName("jdk.internal.module.IllegalAccessLogger")
	val logger: Field = loader.getDeclaredField("logger")
	unsafe.putObjectVolatile(loader, unsafe.staticFieldOffset(logger), null)
}
