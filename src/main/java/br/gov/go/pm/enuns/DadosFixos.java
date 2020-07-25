package br.gov.go.pm.enuns;

public enum DadosFixos {
	/*Código da PM no exército*/
	EXERCITO("[900000421]"),
	TIPOPUBLICACAO("1"),
	PROFISSAO("POLICIAL MILITAR"),
	TIPO_PROPRIETARIO("7");

	private String descricao;
	
	DadosFixos(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}
}
	

