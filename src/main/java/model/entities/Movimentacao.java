package model.entities;




import model.enums.StatusMovimentacao;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Movimentacao {

    private Long id;
    private String descricao;
    private LocalDate data;
    private BigDecimal valor;
    private Double quilometragemAtual;

    private Veiculo veiculo;
    private TipoDespesa tipoDespesa;

    private StatusMovimentacao statusMovimentacao;

    public Movimentacao() {
    }

    public Movimentacao(Veiculo veiculo, TipoDespesa tipoDespesa, String descricao, LocalDate data, BigDecimal valor) {
        this.veiculo = veiculo;
        this.tipoDespesa = tipoDespesa;
        this.descricao = descricao;
        this.data = data;
        this.valor = valor;
        this.statusMovimentacao = StatusMovimentacao.PENDENTE;
        this.quilometragemAtual = null;
    }

    public Movimentacao(Veiculo veiculo, TipoDespesa tipoDespesa, String descricao, LocalDate data, BigDecimal valor,Double quilometragemAtual, StatusMovimentacao  statusMovimentacao) {
        this.veiculo = veiculo;
        this.tipoDespesa = tipoDespesa;
        this.descricao = descricao;
        this.data = data;
        this.valor = valor;
        this.statusMovimentacao = statusMovimentacao;
        this.quilometragemAtual = quilometragemAtual;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
    }

    public TipoDespesa getTipoDespesa() {
        return tipoDespesa;
    }

    public void setTipoDespesa(TipoDespesa tipoDespesa) {
        this.tipoDespesa = tipoDespesa;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public StatusMovimentacao getStatusMovimentacao() {
        return statusMovimentacao;
    }

    public void setStatusMovimentacao(StatusMovimentacao statusMovimentacao) {
        this.statusMovimentacao = statusMovimentacao;
    }

    public Double getQuilometragemAtual() {
        return quilometragemAtual;
    }

    public void setQuilometragemAtual(Double quilometragemAtual) {
        this.quilometragemAtual = quilometragemAtual;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatadorData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        NumberFormat formatadorMoeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));


        String placaVeiculo;
        if (veiculo != null) {
            placaVeiculo = veiculo.getPlaca();
        } else {
            placaVeiculo = "N/A";
        }

        String descTipoDespesa;
        if (tipoDespesa != null) {
            descTipoDespesa = tipoDespesa.getDescricao();
        } else {
            descTipoDespesa = "N/A";
        }

        return "Movimentação ID: " + id + "\n" +
                "  - Veículo (Placa): " + placaVeiculo + "\n" +
                "  - Data: " + data.format(formatadorData) + "\n" +
                "  - Tipo: " + descTipoDespesa + "\n" +
                "  - Descrição: " + descricao + "\n" +
                "  - Valor: " + formatadorMoeda.format(valor);
    }
}



