package br.gov.go.pm.enuns;

public enum StatusCarga {

		ENVIADA("ENVIADA"), NAO_ENVIADA("NÃO ENVIADA"), GERADA_CARGA("GERADA A CARGA"), GERADO_ITEM_BOLETIM("GERADO ITEM BOLETIM");

			String descricao;

			StatusCarga(String descricao) {
				this.descricao = descricao;
			}

			public String getDescricao() {
				return descricao;
			}

			
			
			
}
