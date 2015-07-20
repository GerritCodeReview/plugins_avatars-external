Build
=====

This plugin is built using Buck.


Buck
----

From gerrit source directory:

```
buck build plugins/avatars/external:avatars-external
```

You will find the `avatars-external.jar` file in `buck-out/gen/plugins/avatars/external`.
