package br.gov.go.pm.enuns;

public enum StatusAuditoria {

		INSERT("INSERIDO"), UPDATE("ALTERADO");

			String descricao;

			private StatusAuditoria(String descricao) {
				this.descricao = descricao;
			}

			public String getDescricao() {
				return descricao;
			}

			
			
			
}
