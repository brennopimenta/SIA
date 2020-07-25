package br.gov.go.pm.enuns;

public enum StatusSituacao {

		ATIVO("ATIVO"), INATIVO("INATIVO");

			String descricao;

			private StatusSituacao(String descricao) {
				this.descricao = descricao;
			}

			public String getDescricao() {
				return descricao;
			}

			
			
			
}
