package br.gov.go.pm.enuns;

public enum UnidadeFederacao {
    AMAZONAS("Amazonas", "AM", "2"),
    ALAGOAS("Alagoas", "AL", "4"),
    ACRE("Acre", "AC", "1"),
    AMAPA("Amapá", "AP", "3"),
    BAHIA("Bahia", "BA", "5"),
    PARA("Pará", "PA", "14"),
    MATO_GROSSO("Mato Grosso", "MT", "13"),
    MINAS_GERAIS("Minas Gerais", "MG", "12"),
    MATO_GROSSO_DO_SUL("Mato Grosso do Sul", "MS", "11"),
    GOIAS("Goiás", "GO", "8"),
    MARANHAO("Maranhão", "MA", "9"),
    RIO_GRANDE_DO_SUL("Rio Grande do Sul", "RS", "23"),
    TOCANTINS("Tocantins", "TO", "8"),
    PIAUI("Piauí", "PI", "17"),
    SAO_PAULO("São Paulo", "SP", "26"),
    RONDONIA("Rondônia", "RO", "21"),
    RORAIMA("Roraima", "RR", "22"),
    PARANA("Paraná", "PR", "18"),
    CEARA("Ceará", "CE", "6"),
    PERNAMBUCO("Pernambuco", "PE", "16"),
    SANTA_CATARINA("Santa Catarina", "SC", "24"),
    PARAIBA("Paraíba", "PB", "15"),
    RIO_GRANDE_DO_NORTE("Rio Grande do Norte", "RN", "20"),
    ESPIRITO_SANTO("Espírito Santo", "ES", "8"),
    RIO_DE_JANEIRO("Rio de Janeiro", "RJ", "19"),
    SERGIPE("Sergipe", "SE", "25"),
    DISTRITO_FEDERAL("Distrito Federal", "DF", "7");

    private String nome;
    private String sigla;
    private String codigo;

    /**
     * Construtor do enum
     *
     * @param nome    nome da unidade da federação completo
     * @param sigla   sigla da unidade da federação
     * @param codigo  da unidade da federação
     */
    UnidadeFederacao(final String nome, final String sigla, final String codigo) {
        this.nome = nome;
        this.sigla = sigla;
        this.codigo = codigo;
    }

    /**
     * Obtém a sigla da UF
     *
     * @return a sigla da UF
     */
    public String sigla() {
        return this.sigla;
    }

    /**
     * Nome da UF
     *
     * @return nome completo da UF
     */
    public String nome() {
        return this.nome;
    }

    /**
     * Nome da codigo da UF
     *
     * @return nome da codigo da UF
     */
    public String codigo() {
        return this.codigo;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UnidadeFederacao{");
        sb.append("nome='").append(nome).append('\'');
        sb.append(", sigla='").append(sigla).append('\'');
        sb.append(", codigo='").append(codigo).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
