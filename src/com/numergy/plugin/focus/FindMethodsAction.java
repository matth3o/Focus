package com.numergy.plugin.focus;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.java.PsiExpressionStatementImpl;
import com.intellij.psi.impl.source.tree.java.PsiIdentifierImpl;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.generate.tostring.psi.PsiAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FindMethodsAction extends AnAction {

    /**
     * Methode appelée lorsque l'Action FindMethods est appelée
     *
     * @param e
     */
    public void actionPerformed(AnActionEvent e) {

        // get Class from Context
        PsiClass psiClass = getPsiClassFromContext(e);
        // get current method from context
        PsiMethod psiCurrentMethod = getCurrentMethodFromContext(e);

        if (psiCurrentMethod == null) {
            // Error : the caret is not in a method
            // FindMethodsErrorWindow findMethodsErrorWindow = new FindMethodsErrorWindow(psiClass, "Must be in a method");
            // findMethodsErrorWindow.show();
            HighLightUtil.highlightMethods(e.getData(PlatformDataKeys.EDITOR), null);
        } else {
            // get sub methods list from current method
            Set<PsiMethod> subMethods = getSubMethods(psiClass, psiCurrentMethod);
            // highlight methods
            HighLightUtil.highlightMethods(e.getData(PlatformDataKeys.EDITOR), subMethods);
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

        int offset = editor.getCaretModel().getOffset();        // get offset from editor
        PsiElement elementAt = psiFile.findElementAt(offset);   // get current element from offset
        e.getPresentation().setEnabled(false);
        return PsiTreeUtil.getParentOfType(elementAt, PsiClass.class); // return PsiClass
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


    private Set<PsiMethod> getSubMethods(PsiClass psiClass, PsiMethod psiMethod) {


        List<PsiIdentifierImpl> listOfInstructions = new ArrayList<PsiIdentifierImpl>();

        PsiCodeBlock[] codeBlocks = null;
        PsiElement[] codeBlocksChildrends = null;
        Set<PsiMethod> subMethods = new HashSet<PsiMethod>();

        // get the method code block children (contains implementation code of the method)
        codeBlocks = PsiTreeUtil.getChildrenOfType(psiMethod, PsiCodeBlock.class);

        if (codeBlocks == null) {
            return null;
        }

        for (PsiCodeBlock block : codeBlocks) {

            // get all children of the code block
            // returns different types of PsiElement such as : new line, affectation, expression statement, comment...
            codeBlocksChildrends = block.getChildren();

            for (PsiElement codeBlockChild : codeBlocksChildrends) {

                // re-init variable
                String methodName = "";

                if (codeBlockChild.getClass() == PsiExpressionStatementImpl.class) {

                    // line in an expression statement ==> could be a method call in it
                    String expression = codeBlockChild.getText();

                    System.out.println("Start parsing expression <" + expression + ">");

                    if (!(expression.contains("(") && expression.contains(")"))) {
                        System.out.println("No Brackets in line ==> continue");
                        continue;
                    }

                    methodName = extractMethodNameFromExpression(expression);

                    // try to find method in current class
                    PsiMethod subMethod = PsiAdapter.findMethodByName(psiClass, methodName);

                    if (subMethod == null) {
                        System.out.println("Method <" + methodName + "> not found in current class");
                    } else {
                        subMethods.add(subMethod);
                    }
                }
            }
        }

        return subMethods;

    }


    private String extractMethodNameFromExpression(String expression) {

        System.out.println("Try to extract method name from expression <" + expression + ">");

        // delete ";" at end of line
        expression = expression.substring(0, expression.length() - 1);

        // ==> split from "=" to end of line + trim line
        if (expression.contains("=")) {
            expression = StringUtil.split(expression, "=").get(1).trim();
        }

        // delete parentheses
        int openParentheseIndex = expression.indexOf("(");
        String methodName = expression.substring(0, openParentheseIndex);

        System.out.println("Extracted method name is <" + methodName + ">");

        return methodName;
    }


    @Override
    public void update(AnActionEvent e) {

        PsiClass psiClass = getPsiClassFromContext(e);
        e.getPresentation().setEnabled(psiClass != null);

    }


}
