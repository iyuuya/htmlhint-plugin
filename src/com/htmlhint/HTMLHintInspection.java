package com.htmlhint;

import com.intellij.codeInspection.*;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiUtilCore;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

public class HTMLHintInspection extends PropertySuppressableInspectionBase {
  private static final Logger LOG = Logger.getInstance(HTMLHintInspection.class);

  @NotNull
  public String getDisplayName() {
    return "HTMLHint Warning";
  }

  @NotNull
  public String getShortName() {
    return "HTMLHintInspection";
  }

  public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull final InspectionManager manager, final boolean isOnTheFly) {
    try {
      LOG.debug(file.getFileType().getDefaultExtension());
      if (file.getFileType().getDefaultExtension() != "html") return null;
      HTMLHintProjectComponent component = file.getProject().getComponent(HTMLHintProjectComponent.class);
      if (!component.isSettingsValid() || !component.isEnabled()) return null;

      ExecuteShellCommand.Result result = ExecuteShellCommand.run(file.getProject().getBasePath(), file.getVirtualFile().getPath(), component.htmlhintExecutable, component.htmlhintRcFile);
      if (StringUtils.isNotEmpty(result.errorOutput)) {
        component.showInfoNotification(result.errorOutput, NotificationType.WARNING);
        return null;
      }
      Document document = PsiDocumentManager.getInstance(file.getProject()).getDocument(file);
      final ProblemsHolder problemsHolder = new ProblemsHolder(manager, file, isOnTheFly);
      for (ExecuteShellCommand.Warn warn : result.warns) {
        int offset = StringUtil.lineColToOffset(document.getText(), warn.line - 1, warn.column);
        // System.out.println("+ " + warn.message + " " + warn.line + ":" + warn.column + " " + offset);
        PsiElement lit = PsiUtilCore.getElementAtOffset(file, offset);
        LOG.debug("+ " + lit.getText());

        LocalQuickFix fix = getFixForRule(warn.rule);

        final ProblemDescriptor problem = manager.createProblemDescriptor(
            lit,
            String.format("HTMLHint: %s [%s/%s]", warn.message.trim(), warn.level, warn.rule),
            fix,
            warn.level.equals("error") ? ProblemHighlightType.ERROR : ProblemHighlightType.GENERIC_ERROR_OR_WARNING, isOnTheFly);

        problemsHolder.registerProblem(problem);
      }

      return problemsHolder.getResultsArray();
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println(e.getLocalizedMessage());
      System.out.println(e.getMessage());
      System.out.println(e.getStackTrace());
    }
    return null;
  }

  private static LocalQuickFix getFixForRule(String rule) {
    return null;
  }
}
