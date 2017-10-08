package com.htmlhint.settings;

import com.htmlhint.HTMLHintProjectComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class HTMLHintSettingsPage implements Configurable {
  protected Project project;
  private JCheckBox pluginEnabledCheckbox;
  private JTextField htmlhintBinField;
  private JTextField configFilePathField;
  private JPanel panel;

  public HTMLHintSettingsPage(@NotNull final Project project) {
    this.project = project;
  }

  @Nls
  @Override
  public String getDisplayName() {
    return "HTMLHint Plugin";
  }

  @Nullable
  @Override
  public String getHelpTopic() {
    return null;
  }

  @Nullable
  @Override
  public JComponent createComponent() {
    loadSettings();
    return panel;
  }

  @Override
  public boolean isModified() {
    return !pluginEnabledCheckbox.isSelected() == getSettings().pluginEnabled
      || !htmlhintBinField.getText().equals(getSettings().htmlhintExecutable)
      || !configFilePathField.getText().equals(getSettings().htmlhintRcFile);
  }

  @Override
  public void apply() throws ConfigurationException {
    saveSettings();
    PsiManager.getInstance(project).dropResolveCaches();
  }

  protected void saveSettings() {
    getSettings().pluginEnabled = pluginEnabledCheckbox.isSelected();
    getSettings().htmlhintExecutable = htmlhintBinField.getText();
    getSettings().htmlhintRcFile = configFilePathField.getText();
    project.getComponent(HTMLHintProjectComponent.class).validateSettings();
  }

  protected void loadSettings() {
    pluginEnabledCheckbox.setSelected(getSettings().pluginEnabled);
    htmlhintBinField.setText(getSettings().htmlhintExecutable);
    configFilePathField.setText(getSettings().htmlhintRcFile);
  }

  @Override
  public void reset() {
    loadSettings();
  }

  @Override
  public void disposeUIResources() {
  }

  protected Settings getSettings() {
    return Settings.getInstance(project);
  }
}
