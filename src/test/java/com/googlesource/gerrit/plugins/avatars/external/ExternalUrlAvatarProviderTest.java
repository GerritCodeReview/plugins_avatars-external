// Copyright (C) 2022 The Android Open Source Project
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

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.when;

import com.google.gerrit.entities.Account;
import com.google.gerrit.extensions.restapi.Url;
import com.google.gerrit.server.IdentifiedUser;
import com.google.gerrit.server.config.PluginConfig;
import com.google.gerrit.server.config.PluginConfigFactory;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ExternalUrlAvatarProviderTest {
  private final String PLUGIN_NAME = "avatars-external";

  @Mock private PluginConfigFactory cfgFactory;
  @Mock private PluginConfig cfg;
  @Mock private IdentifiedUser user;
  @Mock private Account account;

  @Before
  public void setup() {
    when(cfgFactory.getFromGerritConfig(PLUGIN_NAME)).thenReturn(cfg);

    when(cfg.getString("url")).thenReturn("http://avatars.example.com/${user}.jpg");
    when(cfg.getString("changeUrl")).thenReturn("http://avatars.example.com/change");
    when(cfg.getString("sizeParameter")).thenReturn("s=${size}x${size}");

    when(user.getAccountId()).thenReturn(Account.id(1));
    when(user.getAccount()).thenReturn(account);
    when(user.getUserName()).thenReturn(Optional.of("JDoe"));

    when(account.preferredEmail()).thenReturn("john.doe@example.com");
  }

  @Test(expected = Test.None.class)
  public void doNotFailIfNoUrlConfigured() {
    when(cfg.getString("url")).thenReturn(null);
    ExternalUrlAvatarProvider avatarProvider =
        new ExternalUrlAvatarProvider(cfgFactory, PLUGIN_NAME, "https://gerrit.example.com");
    String url = avatarProvider.getUrl(user, 30);
    assertThat(url).isNull();
  }

  @Test(expected = Test.None.class)
  public void doNotFailIfUrlHasNoPlaceholders() {
    when(cfg.getString("url")).thenReturn("http://avatars.example.com");
    ExternalUrlAvatarProvider avatarProvider =
        new ExternalUrlAvatarProvider(cfgFactory, PLUGIN_NAME, "https://gerrit.example.com");
    String url = avatarProvider.getUrl(user, 30);
    assertThat(url).isNull();
  }

  @Test
  public void sslIsEnforcedIfGerritUsesSsl() {
    ExternalUrlAvatarProvider avatarProvider =
        new ExternalUrlAvatarProvider(cfgFactory, PLUGIN_NAME, "https://gerrit.example.com");
    String url = avatarProvider.getUrl(user, 30);
    assertThat(url).startsWith("https://");
  }

  @Test
  public void allAcceptedTemplatesAreReplaced() {
    when(cfg.getString("url")).thenReturn("http://avatars.example.com/${user}-${id}-${email}.jpg");
    ExternalUrlAvatarProvider avatarProvider =
        new ExternalUrlAvatarProvider(cfgFactory, PLUGIN_NAME, "http://gerrit.example.com");
    String url = avatarProvider.getUrl(user, 30);
    assertThat(url)
        .startsWith(
            String.format(
                "http://avatars.example.com/%s", Url.encode("JDoe-1-john.doe@example.com.jpg")));
  }

  @Test
  public void userNameInUrlIsConvertedToLowerCaseIfConfigured() {
    ExternalUrlAvatarProvider avatarProvider =
        new ExternalUrlAvatarProvider(cfgFactory, PLUGIN_NAME, "https://gerrit.example.com");
    String url = avatarProvider.getUrl(user, 30);
    assertThat(url.toLowerCase()).isNotEqualTo(url);

    when(cfg.getBoolean("lowerCase", false)).thenReturn(true);
    avatarProvider =
        new ExternalUrlAvatarProvider(cfgFactory, PLUGIN_NAME, "https://gerrit.example.com");
    url = avatarProvider.getUrl(user, 30);
    assertThat(url.toLowerCase()).isEqualTo(url);
  }

  @Test
  public void sizeParametersAreAddedToUrl() {
    ExternalUrlAvatarProvider avatarProvider =
        new ExternalUrlAvatarProvider(cfgFactory, PLUGIN_NAME, "https://gerrit.example.com");
    String url = avatarProvider.getUrl(user, 30);
    assertThat(url).endsWith("?s=30x30");

    when(cfg.getString("url")).thenReturn("http://avatars.example.com/${user}?q=test");
    avatarProvider =
        new ExternalUrlAvatarProvider(cfgFactory, PLUGIN_NAME, "http://gerrit.example.com");
    url = avatarProvider.getUrl(user, 30);
    assertThat(url).endsWith("?q=test&s=30x30");
  }

  @Test
  public void changeAvatarUrlIsTemplated() {
    ExternalUrlAvatarProvider avatarProvider =
        new ExternalUrlAvatarProvider(cfgFactory, PLUGIN_NAME, "https://gerrit.example.com");
    String url = avatarProvider.getChangeAvatarUrl(user);
    assertThat(url).isEqualTo("http://avatars.example.com/change");

    when(cfg.getString("changeUrl")).thenReturn("http://avatars.example.com/change/${user}");
    avatarProvider =
        new ExternalUrlAvatarProvider(cfgFactory, PLUGIN_NAME, "http://gerrit.example.com");
    url = avatarProvider.getChangeAvatarUrl(user);
    assertThat(url).isEqualTo("http://avatars.example.com/change/JDoe");
  }
}
