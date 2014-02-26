package com.numergy.plugin.focus;

import com.intellij.ide.util.DefaultPsiElementCellRenderer;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.ToolbarDecorator;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class FindMethodsDialog extends DialogWrapper {

    private final LabeledComponent<JPanel> component;
//    private CollectionListModel<PsiField> myFields;
    private CollectionListModel<PsiMethod> myCurrentMethods;

    public FindMethodsDialog(PsiClass psiClass, PsiMethod psiMethod) {
        // init
        super(psiClass.getProject());
        init();
        setTitle("Current Method");


        // get all methods of the class (super class methods are excluded)
        myCurrentMethods = new CollectionListModel<PsiMethod>();
        myCurrentMethods.add(psiMethod);
        JList currentMethodList = new JList(myCurrentMethods);
        currentMethodList.setCellRenderer(new DefaultPsiElementCellRenderer());

        // get current method

        // add list to panel
        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(currentMethodList);
        decorator.disableAddAction();
        JPanel panel = decorator.createPanel();

        // create LabeledComponent and init
        component = LabeledComponent.create(panel, "Current Method");
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {

        return component;
    }

}
