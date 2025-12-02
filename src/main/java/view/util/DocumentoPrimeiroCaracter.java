package view.util;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

public class DocumentoPrimeiroCaracter extends DocumentFilter {

    private String capitalize(String text, int offset, Document doc) throws BadLocationException {
        String currentText = doc.getText(0, doc.getLength());
        if (offset == 0 && (currentText.isEmpty() || currentText.length() == offset)) {
            if (!text.isEmpty()) {
                return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
            }
        }
        return text.toLowerCase();
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
            throws BadLocationException {
        if (string != null) {
            String filteredString = capitalize(string, offset, fb.getDocument());
            super.insertString(fb, offset, filteredString, attr);
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
            throws BadLocationException {
        if (text != null) {
            String filteredText = capitalize(text, offset, fb.getDocument());
            super.replace(fb, offset, length, filteredText, attrs);
        } else {
            super.replace(fb, offset, length, text, attrs);
        }
    }
}

