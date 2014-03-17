package com.numergy.plugin.focus;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jetbrains.generate.tostring.psi.PsiAdapter;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.java.PsiMethodCallExpressionImpl;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;

public class HighlightCalledMethodsAction extends AnAction {

    public void actionPerformed(AnActionEvent e) {

        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        Editor editor = e.getData(PlatformDataKeys.EDITOR);

        PsiClass psiClass = getPsiClassFromContext(e, psiFile, editor);
        PsiMethod psiCurrentMethod = getCurrentMethodFromContext(e, psiFile, editor);

        if (psiCurrentMethod == null) {
            HighlightUtil.highlightMethods(editor, null);
        } else {
            Map<Integer, PsiMethod> subMethodsToHighlight = new HashMap<Integer, PsiMethod>();
            Set<PsiMethodCallExpression> subMethodCalls = new HashSet<PsiMethodCallExpression>();

            // look for calls in current method recursively
            findMethodCallExpressionInElement(psiCurrentMethod, subMethodCalls);

            // parse set of <PsiMethodCallExpression>
            for (PsiMethodCallExpression methodCall : subMethodCalls) {

                PsiElement referenceExpression = PsiTreeUtil.getChildOfType(methodCall, PsiReferenceExpression.class);
                if (referenceExpression == null)
                    continue;

                PsiElement identifier = PsiTreeUtil.getChildOfType(referenceExpression, PsiIdentifier.class);
                if (identifier == null)
                    continue;

                String subMethodName = identifier.getText();
                int subMethodCallLine = editor.getDocument().getLineNumber(identifier.getTextRange().getStartOffset());

                PsiMethod subMethod = PsiAdapter.findMethodByName(psiClass, subMethodName);

                if (subMethod != null) {
                    subMethodsToHighlight.put(subMethodCallLine, subMethod);
                }
            }

            ToolWindowManager twm = ToolWindowManager.getInstance(getEventProject(e));
            ToolWindow structureToolWindow = twm.getToolWindow("Structure");
            final ContentManager contentManager = structureToolWindow.getContentManager();
            final Content[] contents = contentManager.getContents();
            contents[0].getActions();



            final Component[] components = structureToolWindow.getComponent().getComponents();

            for (Component comp : components) {
                if (comp.getClass().getSimpleName().equals("MyContentComponent")) {

                    System.out.print("Break");

                }
            }

            HighlightUtil.highlightMethods(editor, subMethodsToHighlight);
        }
    }

    private PsiClass getPsiClassFromContext(AnActionEvent e, PsiFile psiFile, Editor editor) {

        // get datas from context (psiFile & editor)
        if (psiFile == null || editor == null) {
            e.getPresentation().setEnabled(false);
            return null;
        }

        PsiElement elementAt = getCurrentElementFrom(psiFile, editor);
        return PsiTreeUtil.getParentOfType(elementAt, PsiClass.class);
    }

    private PsiMethod getCurrentMethodFromContext(AnActionEvent e, PsiFile psiFile, Editor editor) {

        // get datas from context (psiFile & editor)
        if (psiFile == null || editor == null) {
            e.getPresentation().setEnabled(false);
            return null;
        }

        PsiElement elementAt = getCurrentElementFrom(psiFile, editor);
        return PsiTreeUtil.getParentOfType(elementAt, PsiMethod.class);
    }

    private PsiElement getCurrentElementFrom(PsiFile psiFile, Editor editor) {

        int offset = editor.getCaretModel().getOffset();
        return psiFile.findElementAt(offset);
    }

    protected void findMethodCallExpressionInElement(PsiElement element, Set<PsiMethodCallExpression> methodCalls) {

        if (element.getClass().equals(PsiMethodCallExpressionImpl.class)) {
            methodCalls.add((PsiMethodCallExpression) element);
        }

        for (PsiElement child : element.getChildren()) {
            findMethodCallExpressionInElement(child, methodCalls);
        }
    }

    @Override
    public void update(AnActionEvent e) {

        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        Editor editor = e.getData(PlatformDataKeys.EDITOR);

        PsiClass psiClass = getPsiClassFromContext(e, psiFile, editor);
        e.getPresentation().setEnabled(psiClass != null);
    }

}
