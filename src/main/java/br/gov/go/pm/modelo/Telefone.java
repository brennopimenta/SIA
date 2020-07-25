package br.gov.go.pm.modelo;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class Telefone implements Serializable{
	
	private static final long serialVersionUID = 6542220314172245125L;
	
	private String fixoResidencial;
	private String fixoFuncional;
	private String celularPessoal;
	private String celularFuncional;
	
	//getters and setters
	public String getFixoResidencial() {
		return fixoResidencial;
	}
	public void setFixoResidencial(String fixoResidencial) {
		this.fixoResidencial = fixoResidencial;
	}
	public String getFixoFuncional() {
		return fixoFuncional;
	}
	public void setFixoFuncional(String fixoFuncional) {
		this.fixoFuncional = fixoFuncional;
	}
	public String getCelularPessoal() {
		return celularPessoal;
	}
	public void setCelularPessoal(String celularPessoal) {
		this.celularPessoal = celularPessoal;
	}
	public String getCelularFuncional() {
		return celularFuncional;
	}
	public void setCelularFuncional(String celularFuncional) {
		this.celularFuncional = celularFuncional;
	}
	
	
	
}
