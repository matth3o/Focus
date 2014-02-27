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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FindMethodsDialog extends DialogWrapper {

    private final LabeledComponent<JPanel> component;
//    private CollectionListModel<PsiField> myFields;
    private CollectionListModel<PsiMethod> myCurrentMethods;

    public FindMethodsDialog(PsiClass psiClass, Set<PsiMethod> subMethodsList) {
        // init
        super(psiClass.getProject());
        init();
        setTitle("Sub Methods List");


        // get all methods of the class (super class methods are excluded)
        myCurrentMethods = new CollectionListModel<PsiMethod>();
        myCurrentMethods.add(new ArrayList<PsiMethod>(subMethodsList));
        JList currentMethodList = new JList(myCurrentMethods);
        currentMethodList.setCellRenderer(new DefaultPsiElementCellRenderer());

        // get current method

        // add list to panel
        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(currentMethodList);
        decorator.disableAddAction();
        JPanel panel = decorator.createPanel();

        // create LabeledComponent and init
        component = LabeledComponent.create(panel, "Sub Methods List");
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {

        return component;
    }

}
