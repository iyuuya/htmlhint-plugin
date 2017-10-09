package com.htmlhint;

import com.intellij.codeInsight.FileModificationService;
import com.intellij.codeInspection.CustomSuppressableInspectionTool;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.SuppressIntentionAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
// import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public abstract class PropertySuppressableInspectionBase extends LocalInspectionTool implements CustomSuppressableInspectionTool {
  private static final Logger LOG = Logger.getInstance("#com.intellij.lang.properties.PropertySuppressableInspectionBase");

  @NotNull
  public String getGroupDisplayName() {
    return "Code quality tools";
  }

  public SuppressIntentionAction[] getSuppressActions(final PsiElement element) {
    return new SuppressIntentionAction[]{new SuppressForStatement(getShortName()), new SuppressForFile(getShortName())};
  }

  public boolean isSuppressedFor(@NotNull PsiElement element) {
    return false;
  }

  private static class SuppressForStatement extends SuppressIntentionAction {
    private final String rule;

    public SuppressForStatement(String rule) {
      this.rule = rule;
    }

    @NotNull
    public String getText() {
      return "Suppress for statement";
    }

    @NotNull
    public String getFamilyName() {
      return "Suppress for statement";
    }

    public boolean isAvailable(@NotNull final Project project, final Editor editor, @NotNull final PsiElement element) {
      LOG.debug(element.toString());
      return true;
      // final HTMLElement property = PsiTreeUtil.getParentOfType(element, HTMLElement.class);
      // return property != null && property.isValid();
    }

    public void invoke(@NotNull final Project project, final Editor editor, @NotNull final PsiElement element) throws IncorrectOperationException {
      final PsiFile file = element.getContainingFile();
      if (!FileModificationService.getInstance().prepareFileForWrite(file)) return;

      // final JSElement property = PsiTreeUtil.getParentOfType(element, JSElement.class);
      // LOG.assertTrue(property != null);
      // final int start = property.getTextRange().getStartOffset();

      @NonNls final Document doc = PsiDocumentManager.getInstance(project).getDocument(file);
      LOG.assertTrue(doc != null);
      // final int line = doc.getLineNumber(start);
      final int line = doc.getLineNumber(0);
      final int lineStart = doc.getLineStartOffset(line);

      doc.insertString(lineStart, "// htmlhint suppress inspection \"" + rule + "\"\n");
    }
  }

  private static class SuppressForFile extends SuppressIntentionAction {
    private final String rule;

    public SuppressForFile(String rule) {
      this.rule = rule;
    }

    @NotNull
    public String getText() {
      return "Suppress for file";
    }

    @NotNull
    public String getFamilyName() {
      return "Suppress for file";
    }

    public boolean isAvailable(@NotNull final Project project, final Editor editor, @NotNull final PsiElement element) {
      return element.isValid(); // && element.getContainingFile() instanceof JSFileImpl;
    }

    public void invoke(@NotNull final Project project, final Editor editor, @NotNull final PsiElement element) throws IncorrectOperationException {
      final PsiFile file = element.getContainingFile();
      if (!FileModificationService.getInstance().prepareFileForWrite(file)) return;

      @NonNls final Document doc = PsiDocumentManager.getInstance(project).getDocument(file);
      LOG.assertTrue(doc != null, file);

      doc.insertString(0, "// htmlhint suppress inspection \"" + rule + "\" for whole file\n");
    }
  }
}
