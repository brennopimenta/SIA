package br.gov.go.pm.enuns;

public enum StatusCraf {
	
		PENDENTE("PENDENTE"), ASSINADO("ASSINADO"), CANCELADO("CANCELADO");

			String descricao;

			private StatusCraf(String descricao) {
				this.descricao = descricao;
			}

			public String getDescricao() {
				return descricao;
			}


}
