plugins {
    id("java")
}

group = "se.hgbrg"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.guava:guava:33.0.0-jre")
    implementation("one.util:streamex:0.8.4");
    implementation("org.jgrapht:jgrapht-core:1.5.2")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}
