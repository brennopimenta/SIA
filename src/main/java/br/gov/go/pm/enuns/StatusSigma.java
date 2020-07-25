package br.gov.go.pm.enuns;

public enum StatusSigma {

	ATRIBUIDO("ATRIBUIDO");

	String descricao;

	private StatusSigma(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

	
}
