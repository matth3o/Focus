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

public class FindMethodsErrorWindow extends DialogWrapper {

    private final LabeledComponent<JPanel> component;
    private CollectionListModel<PsiMethod> myCurrentMethods;

    public FindMethodsErrorWindow(PsiClass psiClass, String errorMessage) {
        // init
        super(psiClass.getProject());
        init();
        setTitle("ERROR " + errorMessage);


        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(new JTable());
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
