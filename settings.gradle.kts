plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}
rootProject.name = "TrafficProfiler"

include(
    "api",
    "core",
    "nms:v1_21_R1",
    "nms:v1_21_R2",
    "nms:v1_21_R3"
)