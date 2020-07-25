package br.gov.go.pm.modelo;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class NumeroSigma implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String numero;
	
	public NumeroSigma(){
		
	}
	
	public NumeroSigma(String numero) {
		this.numero = numero;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}
	
	

}
