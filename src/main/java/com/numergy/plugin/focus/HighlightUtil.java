package com.numergy.plugin.focus;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.psi.PsiMethod;

public class HighlightUtil {

    private static Set<RangeHighlighter> highlighters = new HashSet<RangeHighlighter>();
    private static final TextAttributes TEXT_ATTRIBUTES = new TextAttributes(null, new Color(220, 255, 220), null, null, Font.PLAIN);

    public static void highlightMethods(Editor editor, Map<Integer, PsiMethod> methods) {

        clearHighlightedLine(editor);

        MarkupModel markupModel = editor.getMarkupModel();

        Map<PsiMethod, TextAttributes> alreadyHighlightedMethods = new HashMap<PsiMethod, TextAttributes>();

        if (methods != null && !methods.isEmpty()) {
            for (int callingLine : methods.keySet()) {

                PsiMethod method = methods.get(callingLine);

                // check if method is already highlighted
                if (alreadyHighlightedMethods.containsKey(method)) {
                    // highlight calling line only
                    highlighters.add(markupModel.addLineHighlighter(callingLine, 100, alreadyHighlightedMethods.get(method)));
                    continue;
                }

                // create highlighting color (minimum = 200, for having light colors)
                int randomR = (int) (200 + StrictMath.random() * 55);
                int randomG = (int) (200 + StrictMath.random() * 55);
                int randomB = (int) (200 + StrictMath.random() * 55);
                Color color = new Color(randomR, randomG, randomB);

                // create text attributes using highlighting color
                TextAttributes textAttributes = new TextAttributes(null, color, null, null, Font.PLAIN);

                // get PsiMethod lines to highlight
                Set<Integer> lines = getLinesOf(editor, method);

                // highlight calling line
                highlighters.add(markupModel.addLineHighlighter(callingLine, 100, textAttributes));

                // highlight method block
                for (Integer line : lines) {
                    highlighters.add(markupModel.addLineHighlighter(line, 100, textAttributes));
                }

                // memorize method + text attributes
                alreadyHighlightedMethods.put(method, textAttributes);
            }
        }
    }

    private static Set<Integer> getLinesOf(Editor editor, PsiMethod method) {
        Set<Integer> range = new HashSet<Integer>();
        range.addAll(getRangeLines(editor, method));
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
