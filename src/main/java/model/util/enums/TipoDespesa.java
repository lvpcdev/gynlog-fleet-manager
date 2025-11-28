package model.util.enums;

public enum TipoDespesa {

        COMBUSTIVEL("Combustível"),
        MANUTENCAO("Manutenção"),
        IPVA("IPVA"),
        MULTA("Multa"),
        OUTROS("Outros Gastos");


        private final String descricao;

        TipoDespesa(String descricao) {
            this.descricao = descricao;
        }
        public String getDescricao() {
            return descricao;

        }
    }
