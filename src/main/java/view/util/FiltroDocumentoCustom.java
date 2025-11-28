package view.util;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class FiltroDocumentoCustom extends DocumentFilter {

    private int lenghtMaximo;
    private boolean caixaAlta;

    public FiltroDocumentoCustom(int lenghtMaximo, boolean caixaAlta) {
        this.lenghtMaximo = lenghtMaximo;
        this.caixaAlta = caixaAlta;
    }

    @Override
    public void replace(FilterBypass fb, int offset, int lenght,String texto, AttributeSet attr) throws BadLocationException {

            if(texto == null){
                super.replace(fb, offset, lenght, texto, attr);
                return;
            }

        int lenghtAtual = fb.getDocument().getLength() - lenght;
        int lenghtNovo = lenghtAtual + texto.length();

        if (lenghtNovo <= lenghtMaximo){
            if (caixaAlta){
                texto = texto.toUpperCase();
            }
            super.replace(fb, offset, lenght, texto, attr);
        }
    }
}
