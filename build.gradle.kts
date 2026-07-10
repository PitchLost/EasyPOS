plugins {
    java
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
}

javafx {
    version = "21"
    modules("javafx.controls", "javafx.fxml")
}

application {
    mainClass.set("app.App")
}

group = "org.example"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("com.google.code.gson:gson:2.10.1")
}

tasks.register<Exec>("jpackageTask") {
    dependsOn("installDist")
    commandLine(
        "jpackage",
        "--type", "app-image",
        "--input", "build/install/EasyPOS/lib",
        "--main-jar", "EasyPOS-1.0.0.jar",
        "--main-class", "app.App",
        "--name", "EasyPOS",
        "--app-version", "1.0.0",
        "--vendor", "Johnny Anderson",
        "--dest", "build/jpackage",
        "--java-options", "--module-path \$APPDIR",
        "--java-options", "--add-modules=javafx.controls,javafx.fxml,javafx.graphics",
        "--runtime-image", System.getenv("JAVA_HOME")
    )
}

tasks.test {
    useJUnitPlatform()
}