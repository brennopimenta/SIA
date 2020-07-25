package br.gov.go.pm.modelo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

@Embeddable
public class EnderecoFuncional implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Column(name = "opm", nullable = false, length = 150)
	@NotNull(message = "Erro no armazemento das informações! OPM funcional deve ser inserida. ")
	private String unidadeOrigem;

	@Column(name = "logradouro_funcional", nullable = false, length = 150)
	private String logradouro;

	@Column(name = "bairro_funcional", nullable = false, length = 150)
	@NotNull(message = "Erro no armazemento das informações! BAIRRO funcional deve ser inserido. ")
	private String bairro;

	@Column(name = "cidade_funcional", nullable = false, length = 150)
	@NotNull(message = "Erro no armazemento das informações! CIDADE funcional deve ser inserida. ")
	private String Cidade;
	
	
	public String getUnidadeOrigem() {
		return unidadeOrigem;
	}
	public void setUnidadeOrigem(String unidadeOrigem) {
		this.unidadeOrigem = unidadeOrigem;
	}

	public String getLogradouro() {
		return logradouro;
	}
	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	public String getBairro() {
		return bairro;
	}
	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getCidade() {
		return Cidade;
	}
	public void setCidade(String cidade) {
		Cidade = cidade;
	}



	

}//fim geral
