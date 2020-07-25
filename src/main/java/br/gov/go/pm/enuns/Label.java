package br.gov.go.pm.enuns;

public enum Label {
	SIGMA("SIGMA"),
	NOME("NOME"),
	CPF("CPF"),
	RG("RG"),
	NUMERO_CRAF("Nº DO CRAF"),
	TIPO("ARMA/TIPO"),
	MARCA("MARCA"),
	CALIBRE("CALIBRE"),
	MODELO("MODELO"),
	CAPACIDADE("CAPACIDADE DE TIROS"),
	NUMERO_ARMA("Nº DA ARMA"),
	ACABAMENTO("ACABAMENTO"),
	FUNCIONAMENTO("FUNCIONAMENTO"),
	COMPRIMENTO_CANO("COMPRIMENTO DO CANO"),
	TIPO_ALMA("TIPO DE ALMA"),
	RAIAS("QUANTIDADE DE RAIAS");

	private String descricao;

	Label(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}
}
	

