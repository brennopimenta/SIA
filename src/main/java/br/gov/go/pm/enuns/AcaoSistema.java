package br.gov.go.pm.enuns;

public enum AcaoSistema {
	CREATE("INSERIDO"),
	UPDATE("ALTERADO"),
	DELETE("EXCLUÍDO"),
	INATIVA("INATIVAÇÃO EFETUADA"),
	ATIVA("ATIVAÇÃO EFETUADA");

	private String descricao;

	AcaoSistema(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}
}
	

