package br.gov.go.pm.enuns;

public enum Status {
	
		BAIXADA("BAIXADA"), EXTRAVIADA("EXTRAVIADA"), TRANSFERIDA_SINARM("TRANSFERIDA SINARM"), ATIVA("ATIVA");

			String descricao;

			private Status(String descricao) {
				this.descricao = descricao;
			}

			public String getDescricao() {
				return descricao;
			}

			
			
			
}
