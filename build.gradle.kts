import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.3.12.RELEASE"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	id("io.gitlab.arturbosch.detekt").version("1.9.0")
	jacoco
	kotlin("jvm") version "1.6.10"
	kotlin("plugin.spring") version "1.6.10"
}

group = "com.tw"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	jcenter()
	maven {url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
	maven { url =uri("https://maven.aliyun.com/repository/google") }
	maven { url= uri("https://maven.aliyun.com/nexus/content/groups/public/") }
	maven { url= uri("https://maven.aliyun.com/repository/jcenter")}
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("javax.validation:validation-api:2.0.1.Final")
	implementation("org.hibernate.validator:hibernate-validator:7.0.1.Final")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-hateoas")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.11.4")
	implementation("org.springdoc:springdoc-openapi-ui:1.5.9")
	implementation("io.springfox:springfox-boot-starter:3.0.0")
	implementation("com.google.code.gson:gson:2.8.9")
	implementation("mysql:mysql-connector-java:8.0.26")
	implementation("org.flywaydb:flyway-core")
	developmentOnly("org.springframework.boot:spring-boot-devtools")

	compileOnly("org.jetbrains.kotlinx:kotlinx-html-jvm:0.6.4")
	compileOnly("org.jetbrains.kotlinx:kotlinx-html-js:0.6.4")
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
	}
	testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
	testImplementation("org.mockito:mockito-core:4.0.0")
	testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
}

jacoco {
	toolVersion = "0.8.7"
}


tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
	dependsOn("chmodGitFileToExecute")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

detekt {
	toolVersion = "1.9.0"
	config = files("config/detekt/detekt.yml")
	buildUponDefaultConfig = true
	reports {
		xml {
			enabled = false
		}
		html {
			enabled = true
			destination = file("$buildDir/reports/detekt/check/detektReport.html")
		}
		txt {
			enabled = false
		}
	}
}

tasks.getByName<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
	args("--spring.profiles.active=${project.properties["profile"] ?: "local"}")
}

tasks.register("chmodGitFileToExecute", Exec::class) {
	dependsOn("copyGitHookToGit")
	commandLine = "chmod u+x $projectDir/.git/hooks/pre-commit".split(" ")
	commandLine = "chmod u+x $projectDir/.git/hooks/pre-push".split(" ")
}


tasks.register<Copy>("copyGitHookToGit") {
	from("$projectDir/scripts/git-hooks/pre-commit", "$projectDir/scripts/git-hooks/pre-push")
	into("$projectDir/.git/hooks/")
}


tasks.test {
	finalizedBy(tasks.jacocoTestCoverageVerification)
	finalizedBy(tasks.jacocoTestReport)
}
tasks.jacocoTestReport {
	dependsOn(tasks.test)
	doLast {
		println("file://${project.rootDir}/build/reports/jacoco/test/html/index.html")
	}
	reports {
		xml.isEnabled = true
		xml.destination = file("$buildDir/reports/jacoco/test/jacocoTestReport.xml")
	}
	classDirectories.setFrom(
		files(classDirectories.files.map {
			fileTree(it) {
				exclude("**/exception/**",
					"**/model/**",
					"**/entity/**",
					"**/po/**",
					"**/dto/**",
					"**/config/**",
					"**/UserServiceApplication*",
					"**/Constants*")
			}
		})
	)
}

tasks.check {
	dependsOn(tasks.test)
	dependsOn(tasks.jacocoTestReport)
}

tasks.jacocoTestCoverageVerification {
	dependsOn(tasks.test)
	violationRules {
		rule {
			element = "CLASS"
			limit {
				counter = "LINE"
				value = "COVEREDRATIO"
				minimum = BigDecimal(1)
			}
			limit {
				counter = "BRANCH"
				value = "COVEREDRATIO"
				minimum = BigDecimal(1)
			}
			excludes = listOf(
				"com.tw.user.exception.*",
				"com.tw.user.service.model.*",
				"com.tw.user.dao.po.*",
				"com.tw.user.service.controller.dto.*",
				"com.tw.user.UserServiceApplication*",
				"com.tw.user.**.Constants*"
			)
		}
	}
}
