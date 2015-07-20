// Copyright (C) 2013 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.googlesource.gerrit.plugins.avatars.external;

import com.google.gerrit.common.Nullable;
import com.google.gerrit.extensions.annotations.Listen;
import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.extensions.restapi.Url;
import com.google.gerrit.server.IdentifiedUser;
import com.google.gerrit.server.avatar.AvatarProvider;
import com.google.gerrit.server.config.CanonicalWebUrl;
import com.google.gerrit.server.config.PluginConfig;
import com.google.gerrit.server.config.PluginConfigFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Listen
@Singleton
public class ExternalUrlAvatarProvider implements AvatarProvider {

  private static final String USER = "${user}";

  private final String pluginName;
  private final boolean ssl;
  private String externalAvatarUrl;
  private String avatarChangeUrl;
  private String sizeParameter;

  @Inject
  ExternalUrlAvatarProvider(PluginConfigFactory cfgFactory,
      @PluginName String pluginName,
      @CanonicalWebUrl @Nullable String canonicalUrl) {
    this.pluginName = pluginName;
    PluginConfig cfg = cfgFactory.getFromGerritConfig(pluginName);
    externalAvatarUrl = cfg.getString("url");
    avatarChangeUrl = cfg.getString("changeUrl");
    sizeParameter = cfg.getString("sizeParameter");
    ssl = canonicalUrl != null && canonicalUrl.startsWith("https://");
  }

  @Override
  public String getUrl(IdentifiedUser forUser, int imageSize) {
    if (externalAvatarUrl == null) {
      Logger log = LoggerFactory.getLogger(ExternalUrlAvatarProvider.class);
      log.warn("Avatar URL is not configured, cannot show avatars. Please configure plugin."
          + pluginName + ".url in etc/gerrit.config");
      return null;
    }

    // it is unrealistic that all users share the same avatar image, thus we're
    // warning if we can't find our marker (%s)
    if (!externalAvatarUrl.contains(USER)) {
      Logger log = LoggerFactory.getLogger(ExternalUrlAvatarProvider.class);
      log.warn("Avatar provider url '" + externalAvatarUrl
          + "' does not contain " + USER
          + ", so cannot replace it with username");
      return null;
    }

    // as the Gerrit only sends a 302 Found, the avatar is loaded by the user
    // agent and thus SSL matters for the avatar image, if Gerrit uses SSL
    if (ssl && externalAvatarUrl.startsWith("http://")) {
      externalAvatarUrl = externalAvatarUrl.replace("http://", "https://");
    }
    StringBuilder avatarUrl = new StringBuilder();
    avatarUrl.append(replaceInUrl(externalAvatarUrl, forUser.getUserName()));
    if (imageSize > 0 && sizeParameter != null) {
      avatarUrl.append("?");
      avatarUrl.append(sizeParameter.replaceAll("\\$\\{size\\}",
          Integer.toString(imageSize)));
    }
    return avatarUrl.toString();
  }

  @Override
  public String getChangeAvatarUrl(IdentifiedUser forUser) {

    return replaceInUrl(avatarChangeUrl, forUser.getUserName());
  }

  /**
   * Takes #{replacement} and substitutes the marker REPLACE_MARKER in #{url}
   * after it has been URL encoded
   *
   * @param url The URL, usually containing #{REPLACE_MARKER}
   * @param replacement String to be put inside
   * @return new URL
   */
  private String replaceInUrl(String url, String replacement) {
    if (replacement == null || url == null
        || url.contains(USER) == false) {
      return url;
    }

    // as we can't assume anything of 'replacement', we're URL encoding it
    return url.replace(USER, Url.encode(replacement));
  }
}
