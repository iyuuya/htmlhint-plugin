package com.htmlhint.settings;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Nullable;

@State(
  name = "HTMLHintProjectComponent",
  storages = {
    @Storage(id = "default", file = StoragePathMacros.PROJECT_FILE),
    @Storage(id = "dir", file = StoragePathMacros.PROJECT_CONFIG_DIR + "/htmlhintPlugin.xml", scheme = StorageScheme.DIRECTORY_BASED)
  }
)

public class Settings implements PersistentStateComponent<Settings> {
  public static final String DEFAULT_HTMLHINT_RC = ".htmlhintrc";
  public static final String DEFAULT_HTMLHINT_EXE = "node_modules/htmlhint/bin/htmlhint";
  public static final Boolean DEFAULT_PLUGIN_ENABLED = false;
  public String htmlhintRcFile = DEFAULT_HTMLHINT_RC;
  public String htmlhintExecutable = DEFAULT_HTMLHINT_EXE;
  public boolean pluginEnabled = DEFAULT_PLUGIN_ENABLED;
  protected Project project;

  public static Settings getInstance(Project project) {
    Settings settings = ServiceManager.getService(project, Settings.class);
    settings.project = project;
    return settings;
  }

  @Nullable
  @Override
  public Settings getState() {
    return this;
  }

  @Override
  public void loadState(Settings state) {
    XmlSerializerUtil.copyBean(state, this);
  }

  public String getVersion() {
    return htmlhintExecutable + htmlhintRcFile;
  }
}
