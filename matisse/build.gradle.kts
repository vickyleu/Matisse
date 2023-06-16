@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.library")
    kotlin("android")
    id("org.jetbrains.kotlin.plugin.parcelize")
    `maven-publish`
}
description = "Matisse for Jetpack Compose"
group = "github.leavesczy"
version = "1.0.0"

// 配置发布到本地 Maven 仓库
afterEvaluate {
    publishing {
        repositories {
            mavenLocal()
        }
        // 将 'matisse' 组件发布到本地 Maven 仓库
        publications {
            // 定义Maven发布以便将模块发布到本地或远程 Maven 存储库。
            create<MavenPublication>("release") {
                groupId = project.group.toString()
                artifactId = project.name
                version = project.version.toString()
                from(components["release"])
                pom {
                    packaging = "jar"
                    description.set(project.description)
                    name.set(project.name)
                }
            }
        }
    }
}


val composeCompilerVersion: String by properties
android {
    namespace = "github.leavesczy.matisse"
    compileSdk = 33
    defaultConfig {
        minSdk = 21
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.majorVersion
        freeCompilerArgs = arrayListOf(
            *freeCompilerArgs.toTypedArray(),
            *arrayListOf(
                "-Xopt-in=androidx.compose.material3.ExperimentalMaterial3Api",
                "-Xopt-in=androidx.compose.foundation.ExperimentalFoundationApi",
            ).toTypedArray()
        )
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = composeCompilerVersion
    }
}

//configurations.all {
//    resolutionStrategy {
//        exclude(group = "androidx.compose.material", module = "material-icons-extended")
//    }
//}

dependencies {
    val composeUIVersion: String by properties
    val appcompatVersion: String by properties
    val activityComposeVersion: String by properties
    val composeBomVersion: String by properties
    val composeMaterial3Version: String by properties

    implementation("androidx.appcompat:appcompat:$appcompatVersion")
    implementation("androidx.compose.foundation:foundation:$composeUIVersion")
    implementation("androidx.activity:activity-compose:$activityComposeVersion")
    implementation(platform("androidx.compose:compose-bom:$composeBomVersion"))
    implementation("androidx.compose.material3:material3:$composeMaterial3Version"){
        exclude(group = "androidx.compose.material", module = "material-icons-extended")
        exclude(group = "androidx.compose.material", module = "material-icons-core")
    }
//    implementation("androidx.compose.material:material-icons-extended:$composeUIVersion")
}