package br.gov.go.pm.enuns;

public enum Profile {
	
		CMT("COMANDANTE"), AUTORIDADE_DELEGADA("AUTORIDADE DELEGADA"), ADM("ADMINISTRADOR"), SIA("USUARIO SIA");

			String descricao;

			private Profile(String descricao) {
				this.descricao = descricao;
			}

			public String getDescricao() {
				return descricao;
			}

			
			
			
}


