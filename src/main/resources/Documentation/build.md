Build
=====

This plugin is built in the Gerrit source tree using Bazel.


Bazel
----

From the plugin source directory:

```
bazel build avatars-external
```

You will find the `avatars-external.jar` file in `../../bazel-bin/plugins/avatars-external/avatars-external.jar`.
