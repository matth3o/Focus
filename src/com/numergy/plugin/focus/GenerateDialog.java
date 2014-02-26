package com.numergy.plugin.focus;

import com.intellij.ide.util.DefaultPsiElementCellRenderer;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.ToolbarDecorator;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created by matth3o on 2/26/14.
 */
public class GenerateDialog extends DialogWrapper {

    private final LabeledComponent<JPanel> component;
    private CollectionListModel<PsiField> myFields;

    public GenerateDialog(PsiClass psiClass) {
        super(psiClass.getProject());
        init();
        setTitle("Select Fields coco !!!");

        myFields = new CollectionListModel<PsiField>(psiClass.getAllFields());
        JList fieldList = new JList(myFields);
        fieldList.setCellRenderer(new DefaultPsiElementCellRenderer());
        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(fieldList);
        decorator.disableAddAction();
        JPanel panel = decorator.createPanel();
        component = LabeledComponent.create(panel, "Fields to include !!!!");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {

        return component;
    }

}
