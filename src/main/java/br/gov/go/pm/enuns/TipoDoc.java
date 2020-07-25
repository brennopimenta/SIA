package br.gov.go.pm.enuns;

public enum TipoDoc {
	GED("SEI/GED"),
	OFICIO("OFÍCIO"),
	CORREIO("CORREIO"),
	MEMO("MEMORANDO"),
	INTERNO("CONTROLE INTERNO");

	private String descricao;

	TipoDoc(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}
}
	

