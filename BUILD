load("//tools/bzl:junit.bzl", "junit_tests")
load(
    "//tools/bzl:plugin.bzl",
    "PLUGIN_DEPS",
    "PLUGIN_TEST_DEPS",
    "gerrit_plugin",
)

gerrit_plugin(
    name = "avatars-external",
    srcs = glob(["src/main/java/**/*.java"]),
    resources = glob(["src/main/resources/**/*"]),
    manifest_entries = [
        "Gerrit-PluginName: avatars-external",
        "Implementation-Title: External Avatar plugin",
        "Implementation-URL: https://gerrit-review.googlesource.com/#/admin/projects/plugins/avatars-external",
        "Gerrit-AvatarProvider: com.googlesource.gerrit.plugins.avatars.external.ExternalUrlAvatarProvider",
    ],
)

junit_tests(
    name = "avatars-external_tests",
    srcs = glob(["src/test/java/**/*.java"]),
    tags = ["avatars-external"],
    deps = [
        ":avatars-external__plugin_test_deps",
    ],
)

java_library(
    name = "avatars-external__plugin_test_deps",
    testonly = 1,
    visibility = ["//visibility:public"],
    exports = PLUGIN_DEPS + PLUGIN_TEST_DEPS + [
        ":avatars-external__plugin",
        "@commons-io//jar",
        "@mockito//jar",
    ],
)
