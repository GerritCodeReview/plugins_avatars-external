Configuration
=============

The configuration of the @PLUGIN@ plugin is done in the `gerrit.config`
file.

```
  [plugin "@PLUGIN@"]
    url = http://example.org/avatars/${user}.jpg
    changeUrl = http://example.org/account.html
    sizeParameter = s=${size}x${size}
```

<a id="url">
`plugin.@PLUGIN@.url`
:	The location of avatar images containing `${user}`, which will then
	be replaced by the `username`. Required.

<a id="changeUrl">
`plugin.@PLUGIN@.changeUrl`
:	The URL shown in Gerrit's user settings to tell the user, where the
	avatar can be changed. Optional.

<a id="sizeParameter">
`plugin.@PLUGIN@.sizeParameter`
:	URL parameter with `${size}` placeholder to forward the preferred
	image size to the avatar provider. Optional.

Please note that `http://` URLs will be automatically rewritten to
`https://`, if `gerrit.canonicalWebUrl` uses HTTPS.
