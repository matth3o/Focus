package com.numergy.plugin.focus;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.psi.PsiMethod;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class HighlightUtil {

    private static Set<RangeHighlighter> highlighters = new HashSet<RangeHighlighter>();
    private static final TextAttributes CURRENT_LINE_ATTRIBUTES = new TextAttributes(null, new Color(220, 255, 220), null, null, Font.PLAIN);

    public static void highlightMethods(Editor editor, Set<PsiMethod> methods) {

        clearHighlightedLine(editor);

        if (methods != null && !methods.isEmpty()) {
            Set<Integer> lines = getLinesOf(editor, methods);
            MarkupModel mm = editor.getMarkupModel();
            for (Integer line : lines) {
                if (line >= 0 && line < editor.getDocument().getLineCount()) {
                    highlighters.add(mm.addLineHighlighter(line, 100, CURRENT_LINE_ATTRIBUTES));
                }
            }
        }
    }

    private static Set<Integer> getLinesOf(Editor editor, Set<PsiMethod> methods) {
        Set<Integer> range = new HashSet<Integer>();

        for (PsiMethod psiMethod : methods) {
            range.addAll(getRangeLines(editor, psiMethod));
        }

        return range;
    }


    private static Set<Integer> getRangeLines(Editor editor, PsiMethod method) {
        Set<Integer> range = new HashSet<Integer>();

        int startLine = editor.getDocument().getLineNumber(method.getTextRange().getStartOffset());
        int endLine = editor.getDocument().getLineNumber(method.getTextRange().getEndOffset());

        for (int i = startLine; i <= endLine; i++) {
            range.add(i);
        }

        return range;
    }

    private static void clearHighlightedLine(Editor editor) {
        if (!highlighters.isEmpty()) {
            for (RangeHighlighter rangeHighlighter : highlighters) {
                editor.getMarkupModel().removeHighlighter(rangeHighlighter);
            }
            highlighters.clear();
        }
    }


}
