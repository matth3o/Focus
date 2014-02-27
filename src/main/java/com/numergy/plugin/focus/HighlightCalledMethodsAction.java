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

/**
 * Created by ken2ke on 2/27/14.
 */
public class HighlightCalledMethodsAction extends AnAction {
    /**
     * Methode appelée lorsque l'Action FindMethods est appelée
     *
     * @param e
     */
    public void actionPerformed(AnActionEvent e) {

        PsiClass psiClass = getPsiClassFromContext(e);
        PsiMethod psiCurrentMethod = getCurrentMethodFromContext(e);

        if (psiCurrentMethod == null) {
            HighlightUtil.highlightMethods(e.getData(PlatformDataKeys.EDITOR), null);
        } else {
            Set<PsiMethod> subMethods = new HashSet<PsiMethod>();
            Set<PsiMethodCallExpression> subMethodCalls = new HashSet<PsiMethodCallExpression>();

            // recursive search
            searchInElement(psiCurrentMethod, subMethodCalls);

            // analyse the set of <PsiMethodCallExpression>
            for (PsiMethodCallExpression methodCall : subMethodCalls) {

                PsiElement referenceExpression = PsiTreeUtil.getChildOfType(methodCall, PsiReferenceExpression.class);
                PsiElement identifier = PsiTreeUtil.getChildOfType(referenceExpression, PsiIdentifier.class);
                String methodName = identifier.getText();

                // try to find method in current class
                PsiMethod subMethod = PsiAdapter.findMethodByName(psiClass, methodName);

                if (subMethod != null) {
                    subMethods.add(subMethod);
                }
            }

            // highlight methods
            HighlightUtil.highlightMethods(e.getData(PlatformDataKeys.EDITOR), subMethods);
        }
    }

    private PsiClass getPsiClassFromContext(AnActionEvent e) {

        // get datas from context (psiFile & editor)
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        if (psiFile == null || editor == null) {
            e.getPresentation().setEnabled(false);
            return null;
        }

        int offset = editor.getCaretModel().getOffset();                // get offset from editor
        PsiElement elementAt = psiFile.findElementAt(offset);           // get current element from offset
        e.getPresentation().setEnabled(false);
        return PsiTreeUtil.getParentOfType(elementAt, PsiClass.class);  // return PsiClass
    }

    private PsiMethod getCurrentMethodFromContext(AnActionEvent e) {

        // get datas from context (psiFile & editor)
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        if (psiFile == null || editor == null) {
            e.getPresentation().setEnabled(false);
            return null;
        }

        int offset = editor.getCaretModel().getOffset();
        PsiElement elementAt = psiFile.findElementAt(offset);

        return PsiTreeUtil.getParentOfType(elementAt, PsiMethod.class);
    }

    protected void searchInElement(PsiElement element, Set<PsiMethodCallExpression> methodCalls) {

        if (element.getClass() == PsiMethodCallExpressionImpl.class) {
            methodCalls.add((PsiMethodCallExpression) element);
        }

        PsiElement[] children = element.getChildren();
        for (PsiElement child : children) {
            searchInElement(child, methodCalls);
        }
    }


    @Override
    public void update(AnActionEvent e) {

        PsiClass psiClass = getPsiClassFromContext(e);
        e.getPresentation().setEnabled(psiClass != null);

    }

}
