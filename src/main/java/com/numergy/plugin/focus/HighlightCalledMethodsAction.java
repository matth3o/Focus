package com.numergy.plugin.focus;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.java.PsiMethodCallExpressionImpl;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.generate.tostring.psi.PsiAdapter;

import java.util.HashSet;
import java.util.Set;

public class HighlightCalledMethodsAction extends AnAction {

    public void actionPerformed(AnActionEvent e) {

        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        Editor editor = e.getData(PlatformDataKeys.EDITOR);

        PsiClass psiClass = getPsiClassFromContext(e, psiFile, editor);
        PsiMethod psiCurrentMethod = getCurrentMethodFromContext(e, psiFile, editor);

        if (psiCurrentMethod == null) {
            HighlightUtil.highlightMethods(editor, null);
        } else {
            Set<PsiMethod> subMethods = new HashSet<PsiMethod>();
            Set<PsiMethodCallExpression> subMethodCalls = new HashSet<PsiMethodCallExpression>();

            // recursive search
            findMethodCallExpressionInElement(psiCurrentMethod, subMethodCalls);

            // parse set of <PsiMethodCallExpression>
            for (PsiMethodCallExpression methodCall : subMethodCalls) {

                PsiElement referenceExpression = PsiTreeUtil.getChildOfType(methodCall, PsiReferenceExpression.class);
                if (referenceExpression == null) {
                    continue;
                }

                PsiElement identifier = PsiTreeUtil.getChildOfType(referenceExpression, PsiIdentifier.class);
                if (identifier == null) {
                    continue;
                }

                String methodName = identifier.getText();
                PsiMethod subMethod = PsiAdapter.findMethodByName(psiClass, methodName);

                if (subMethod != null) {
                    subMethods.add(subMethod);
                }
            }

            // highlight methods
            HighlightUtil.highlightMethods(editor, subMethods);
        }
    }

    private PsiClass getPsiClassFromContext(AnActionEvent e,PsiFile psiFile, Editor editor) {

        // get datas from context (psiFile & editor)
        if (psiFile == null || editor == null) {
            e.getPresentation().setEnabled(false);
            return null;
        }

        PsiElement elementAt = getCurrentElementFrom(psiFile, editor);
        return PsiTreeUtil.getParentOfType(elementAt, PsiClass.class);
    }

    private PsiMethod getCurrentMethodFromContext(AnActionEvent e,PsiFile psiFile, Editor editor) {

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
