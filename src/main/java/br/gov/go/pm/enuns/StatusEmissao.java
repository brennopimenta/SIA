package br.gov.go.pm.enuns;

public enum StatusEmissao {
	
	IMPRESSO("IMPRESSO"), NAO_IMPRESSO("NÃO IMPRESSO"), RETRATO("RETRATO"), PAISAGEM("PAISAGEM");
	
	String descricao;
	
	private StatusEmissao(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

	
}
