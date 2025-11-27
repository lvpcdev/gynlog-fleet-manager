package View.util;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class FiltroNumerico extends DocumentFilter {

        private int lenghtMaximo;

        public FiltroNumerico(int lenghtMaximo){
            this.lenghtMaximo = lenghtMaximo;
        }

        @Override
        public void replace(FilterBypass fb, int offset, int lenght, String texto, AttributeSet attr) throws BadLocationException {

            if(texto == null){
                super.replace(fb, offset, lenght, texto, attr);
                return;
            }

            String textoFiltrado = texto.replace("\\D","");

            int lenghtAtual = fb.getDocument().getLength() - lenght;
            int lenghtNovo = lenghtAtual + textoFiltrado.length();

            if(lenghtNovo <= lenghtMaximo){
                super.replace(fb, offset, lenght, texto, attr);
            }
        }
}
