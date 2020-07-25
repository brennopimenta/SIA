package br.gov.go.pm.enuns;

public enum UsoArma {

	RESTRITO("USO RESTRITO"), PERMITIDO("USO PERMITIDO"); 
	

	private String descricao;

	
	private UsoArma(String descricao) {
		this.descricao = descricao;
	}


	public String getDescricao() {
		return descricao;
	}

	
	
	
}
