package util;

import exceptions.FilaVaziaException;

public class Fila<T> {

    private No<T> inicio;
    private No<T> fim;
    private int tamanho;

    public void inserirFim(T dado) {
        No<T> novoNo  = new No<>();
        novoNo.setDado(dado);
        novoNo.setProximo(null);

        if (fim == null) {
            inicio = novoNo;
            fim = novoNo;
        } else {
            fim.setProximo(novoNo);
            fim = novoNo;
        }
        tamanho++;
    }

    public T removerInicio() {
        if (inicio == null) {
            throw new FilaVaziaException("A fila está vazia");
        }

        T dado = inicio.getDado();
        inicio = inicio.getProximo();
        if (inicio == null) {
            fim = null;
        }
        tamanho--;
        return dado;
    }

    public T visualizarPrimeiro() {
        if (inicio == null) {
            throw new FilaVaziaException("A fila está vazia");
        }
        return inicio.getDado();
    }

    public boolean estaVazia() {
        return tamanho == 0;
    }

    public int tamanho() {
        return tamanho;
    }

}
