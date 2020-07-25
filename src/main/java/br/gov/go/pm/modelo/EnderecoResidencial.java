package br.gov.go.pm.modelo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class EnderecoResidencial implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String logradouroRes;
	private String numero;
	private String bairroRes;
	private String email;
	private String cidadeRes;
	private String uf;


	@Column(name = "endRes_logradouro", nullable = false, length = 150)
	public String getLogradouroRes() {
		return logradouroRes;
	}

	public void setLogradouroRes(String logradouroRes) {
		this.logradouroRes = logradouroRes;
	}

	@Column(name = "endRes_numero", length = 4)
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}


	@Column(name = "endRes_bairro", nullable = false, length = 100)
	public String getBairroRes() {
		return bairroRes;
	}

	public void setBairroRes(String bairroRes) {
		this.bairroRes = bairroRes;
	}


	@Column(name = "endRes_email", nullable = false, length = 150)
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "endRes_cidade", nullable = false, length = 150)
	public String getCidadeRes() {
		return cidadeRes;
	}

	public void setCidadeRes(String cidadeRes) {
		this.cidadeRes = cidadeRes;
	}

	@Column(name = "endRes_uf", nullable = false, length = 150)
	public String getUf() {
		return uf;
	}
	public void setUf(String uf) {
		this.uf = uf;
	}

			

}//fim geral
