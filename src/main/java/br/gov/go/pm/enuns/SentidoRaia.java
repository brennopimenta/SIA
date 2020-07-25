package br.gov.go.pm.enuns;

public enum SentidoRaia {

	/**A DESCRIÇÃO de D e E segue a convenção do exército, caso precise de outras criar outro*/
	DIREITA("D"), ESQUERDA("E");

	String descricao;

	SentidoRaia(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}
}
