package com.htmlhint;

import com.htmlhint.settings.Settings;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class HTMLHintProjectComponent implements ProjectComponent {
  private static final Logger LOG = Logger.getInstance("HTMLHint-Plugin");

  protected Project project;
  protected Settings settings;
  protected boolean settingValidStatus;
  protected String settingValidVersion;
  protected String settingVersionLastShowNotification;
  public String htmlhintRcFile;
  public String htmlhintExecutable;
  public boolean pluginEnabled;

  public HTMLHintProjectComponent(Project project) {
    this.project = project;
    settings = Settings.getInstance(project);
  }

  @Override
  public void projectOpened() {
    if (isEnabled()) {
      validateSettings();
    }
  }

  @Override
  public void projectClosed() {
  }

  @Override
  public void initComponent() {
    if (isEnabled()) {
      validateSettings();
    }
  }

  @Override
  public void disposeComponent() {
  }

  @NotNull
  @Override
  public String getComponentName() {
    return "HTMLHintProjectComponent";
  }

  public boolean isEnabled() {
    return Settings.getInstance(project).pluginEnabled;
  }

  public boolean isSettingsValid() {
    if (!settings.getVersion().equals(settingValidVersion)) {
      validateSettings();
      settingValidVersion = settings.getVersion();
    }
    return settingValidStatus;
  }

  public boolean validateSettings() {
    htmlhintExecutable = settings.htmlhintExecutable;
    htmlhintRcFile = settings.htmlhintRcFile;

    settingValidStatus = true;
    return true;
  }

  protected void showErrorConfigNotification(String content) {
    if (!settings.getVersion().equals(settingVersionLastShowNotification)) {
      settingVersionLastShowNotification = settings.getVersion();
      showInfoNotification(content, NotificationType.ERROR);
    }
  }

  public void showInfoNotification(String content, NotificationType type) {
    Notification errorNotification = new Notification("HTMLHint plugin", "HTMLHint plugin", content, type);
    Notifications.Bus.notify(errorNotification, this.project);
  }
}
