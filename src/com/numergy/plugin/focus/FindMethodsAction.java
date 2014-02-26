package com.numergy.plugin.focus;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassImpl;
import com.intellij.psi.impl.source.PsiMethodImpl;
import com.intellij.psi.util.PsiTreeUtil;

public class FindMethodsAction extends AnAction {

    public void actionPerformed(AnActionEvent e) {

        // get Class from Context
        PsiClass psiClass = getPsiClassFromContext(e);
        PsiMethod psiCurrentMethod = getCurrentMethodFromContext(e);

        if (psiCurrentMethod == null) {
            FindMethodsErrorWindow findMethodsErrorWindow = new FindMethodsErrorWindow(psiClass, "Must be in a method");
            findMethodsErrorWindow.show();
        } else {
            FindMethodsDialog findMethodsDialog = new FindMethodsDialog(psiClass, psiCurrentMethod);
            findMethodsDialog.show();
        }


    }

    private PsiClass getPsiClassFromContext(AnActionEvent e) {

        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        Editor editor = e.getData(PlatformDataKeys.EDITOR);

        if (psiFile == null || editor == null) {
            e.getPresentation().setEnabled(false);
            return null;
        }

        int offset = editor.getCaretModel().getOffset();
        PsiElement elementAt = psiFile.findElementAt(offset);

        PsiClass psiClass = PsiTreeUtil.getParentOfType(elementAt, PsiClass.class);
        e.getPresentation().setEnabled(false);
        return psiClass;
    }

    private PsiMethod getCurrentMethodFromContext(AnActionEvent e) {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        Editor editor = e.getData(PlatformDataKeys.EDITOR);

        int offset = editor.getCaretModel().getOffset();
        PsiElement elementAt = psiFile.findElementAt(offset);

        PsiMethod currentMethod = findCurrentMethodFromElement(elementAt);

        return currentMethod;
    }

    private PsiMethod findCurrentMethodFromElement(PsiElement elementAt) {

        if (elementAt.getClass() == PsiClassImpl.class) {
            return null;
        }

        if (elementAt.getClass() != PsiMethodImpl.class) {
           return findCurrentMethodFromElement(elementAt.getParent());
        } else {
            return (PsiMethod) elementAt;
        }
    }


    @Override
    public void update(AnActionEvent e) {

        PsiClass psiClass = getPsiClassFromContext(e);
        e.getPresentation().setEnabled(psiClass != null);

    }


}
