package view.util;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class DocumentoCaixaAlta extends DocumentFilter {
    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
        throws BadLocationException{
        if(string != null){
            super.insertString(fb, offset, string.toUpperCase(), attr);
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int lenght, String text, AttributeSet attrs)
        throws BadLocationException{
        if(text != null){
            super.replace(fb, offset, lenght, text.toUpperCase(), attrs);
        }else{
            super.replace(fb, offset, lenght, text, attrs);
        }
    }
}
