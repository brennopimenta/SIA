package br.gov.go.pm.modelo;

import org.springframework.cglib.core.Local;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Embeddable
public class DadosPessoaisSigma implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@NotNull(message = "Erro ao persistir os dados! Insira a Data de Nascimento.")
	@Column(name = "data_nascimento")
	private Date dataNascimento;

	@NotNull(message = "Erro ao persistir os dados! Insira a Órgão expedidor.")
	@Column(name = "rg_orgao_expedidor")
	private String rgOrgaoExpedidor = "PMGO";

	@NotNull(message = "Erro ao persistir os dados! Insira UF em que foi expedida rg.")
	@Column(name = "rg_uf_expedidor")
	private String rgUfExpedidor = "GO";

	@NotNull(message = "Erro ao persistir os dados! Insira a Data de expedição.")
	@Column(name = "rg_data_expedicao")
	private Date rgDataExpedicao;

	@NotNull(message = "Erro ao persistir os dados! Insira o nome do pai")
	private String pai;

	@NotNull(message = "Erro ao persistir os dados! Insira o nome da mãe")
	private String mae;
		
	public DadosPessoaisSigma() {
		
	}

	public DadosPessoaisSigma(Date dataNascimento, String rgOrgaoExpedidor, String rgUfExpedidor,
			Date rgDataExpedicao, String pai, String mae) {
		this.dataNascimento = dataNascimento;
		this.rgOrgaoExpedidor = rgOrgaoExpedidor;
		this.rgUfExpedidor = rgUfExpedidor;
		this.rgDataExpedicao = rgDataExpedicao;
		this.pai = pai;
		this.mae = mae;
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public Date getRgDataExpedicao() {
		return rgDataExpedicao;
	}

	public void setRgDataExpedicao(Date rgDataExpedicao) {
		this.rgDataExpedicao = rgDataExpedicao;
	}

	public String getRgOrgaoExpedidor() {
		return rgOrgaoExpedidor;
	}
	public void setRgOrgaoExpedidor(String rgOrgaoExpedidor) {
		this.rgOrgaoExpedidor = rgOrgaoExpedidor;
	}
	public String getRgUfExpedidor() {
		return rgUfExpedidor;
	}
	public void setRgUfExpedidor(String rgUfExpedidor) {
		this.rgUfExpedidor = rgUfExpedidor;
	}
	public String getPai() {
		return pai;
	}
	public void setPai(String pai) {
		this.pai = pai;
	}
	public String getMae() {
		return mae;
	}
	public void setMae(String mae) {
		this.mae = mae;
	}
	
	
}
