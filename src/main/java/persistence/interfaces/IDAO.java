package persistence.interfaces;


import java.util.List;


// Interface onde T é o tipo da entidade
// e K é o tipo da chave primária
public interface IDAO<T, K> {
    void salvar(T entidade);
    void atualizar(T entidade);
    void excluir(K id);
    T buscarPorId(K id);
    List<T> listarTodos();
}