load(
    "//tools/bzl:plugin.bzl",
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
