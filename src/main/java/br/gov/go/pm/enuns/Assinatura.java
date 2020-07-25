package br.gov.go.pm.enuns;

public enum Assinatura {
	
	AUTORIDADE("AUTORIDADE"),
	AUTORIDADE_DELEGADA("AUTORIDADE_DELEGADA");
	
	String assinatura;
	
	private Assinatura(String assinatura) {
		this.assinatura = assinatura;
	}

	public String getAssinatura() {
		return assinatura;
	}

	
}
