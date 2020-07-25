package br.gov.go.pm.enuns;

public enum TipoAlma {

	/**A DESCRIÇÃO de L e R segue a convenção do exército, caso precise de outras criar outro*/
	LISA("L"), RAIADA("R");

	String descricao;

	TipoAlma(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}
}
