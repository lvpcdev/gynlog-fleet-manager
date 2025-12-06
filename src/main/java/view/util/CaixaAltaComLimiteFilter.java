package view.util;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class CaixaAltaComLimiteFilter extends DocumentFilter {

    private final int MAX_LENGTH;

    // Construtor que recebe o tamanho máximo
    public CaixaAltaComLimiteFilter(int maxLength) {
        this.MAX_LENGTH = maxLength;
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
            throws BadLocationException {

        if (string != null) {
            String upperCaseString = string.toUpperCase();

            // Verifica se a adição não ultrapassará o limite
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

            // Calcula o novo comprimento: (Comprimento Atual - Texto Removido + Novo Texto)
            if ((fb.getDocument().getLength() - length + upperCaseText.length()) <= MAX_LENGTH) {
                super.replace(fb, offset, length, upperCaseText, attrs);
            }
        } else {
            // Se o texto for nulo (apenas remoção)
            super.replace(fb, offset, length, text, attrs);
        }
    }
}