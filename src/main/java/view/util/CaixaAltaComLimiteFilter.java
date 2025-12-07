package view.util;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class CaixaAltaComLimiteFilter extends DocumentFilter {

    private final int MAX_LENGTH;


    public CaixaAltaComLimiteFilter(int maxLength) {
        this.MAX_LENGTH = maxLength;
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
            throws BadLocationException {

        if (string != null) {
            String upperCaseString = string.toUpperCase();


            if ((fb.getDocument().getLength() + upperCaseString.length()) <= MAX_LENGTH) {
                super.insertString(fb, offset, upperCaseString, attr);
            }
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
            throws BadLocationException {

        if (text != null) {
            String upperCaseText = text.toUpperCase();


            if ((fb.getDocument().getLength() - length + upperCaseText.length()) <= MAX_LENGTH) {
                super.replace(fb, offset, length, upperCaseText, attrs);
            }
        } else {
            super.replace(fb, offset, length, text, attrs);
        }
    }
}