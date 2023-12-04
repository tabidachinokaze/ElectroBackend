plugins {
    application
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.serialization)
}

group = "cn.tabidachi"
version = "0.0.2"
application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {

    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.jodatime)
    implementation(libs.firebase.admin)
    implementation(libs.jakarta.mail)
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger.slf4j)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.network.tls.certificates)
    implementation(libs.ktor.serialization.kotlinx.json.jvm)
    implementation(libs.ktor.server.auth.jvm)
    implementation(libs.ktor.server.auth.jwt.jvm)
    implementation(libs.ktor.server.caching.headers.jvm)
    implementation(libs.ktor.server.call.logging.jvm)
    implementation(libs.ktor.server.content.negotiation.jvm)
    implementation(libs.ktor.server.core.jvm)
    implementation(libs.ktor.server.host.common.jvm)
    implementation(libs.ktor.server.netty.jvm)
    implementation(libs.ktor.server.rate.limit)
    implementation(libs.ktor.server.status.pages.jvm)
    implementation(libs.ktor.server.websockets.jvm)
    implementation(libs.logback.classic)
    implementation(libs.minio)
    implementation(libs.mysql.connector.j)
    implementation(libs.protobuf.kotlin)
    implementation(libs.simple.xml.safe)

    // test
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.ktor.server.tests.jvm)

}