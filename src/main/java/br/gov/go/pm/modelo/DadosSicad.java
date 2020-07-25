package br.gov.go.pm.modelo;

import br.gov.go.pm.converter.LocalDateConverter;

import javax.persistence.Convert;
import java.time.LocalDate;
import java.util.Date;

/** a classe DadosSicad guarda os dados obtidos do serviço que busca o dos dados do Sicad (ConsumirServicoSicad)
 * e não deve ser utilizada para persistência. Caso seja necessário persistencia utilizar a Classe DadosPessoais*/
public class DadosSicad {
	private Long codigo;
	private String cpf;
	private String rgMilitar;
	private String nome;
	private String postoSiglaGrad;


	private String dtNascimento;

	private String rgOrgaoExpedidor = "PMGO";
	private String rgDataExpedicao;
	private String rgUfOrgaoExpedidor;
	private String nomePai;
	private String nomeMae;
	
	/**dados da unidade a que pertence*/
	private String siglaUnidade;
	private String logradouro;
	private String bairro;
	private String Cidade;
	
	/**endereço residencial*/
	private String resLogradouro;
	private String resNumero;
	private String resQuadra;
	private String resLote;
	private String resBairro;
	private String resCidade;
	private String resUf; 
	//
	private String email;
	/**não utilizado para sigma*/
	private String status;
	
	//Getters and setters
	public Long getCodigo() {
		return codigo;
	}
	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}
	public String getCpf() {
		return cpf;
	}
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	public String getRgMilitar() {
		return rgMilitar;
	}
	public void setRgMilitar(String rgMilitar) {
		this.rgMilitar = rgMilitar;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getPostoSiglaGrad() {
		return postoSiglaGrad;
	}
	public void setPostoSiglaGrad(String postoSiglaGrad) {
		this.postoSiglaGrad = postoSiglaGrad;
	}

	public String getDtNascimento() {
		return dtNascimento;
	}

	public void setDtNascimento(String dtNascimento) {
		this.dtNascimento = dtNascimento;
	}

	public String getRgOrgaoExpedidor() {
		return rgOrgaoExpedidor;
	}
	public void setRgOrgaoExpedidor(String rgOrgaoExpedidor) {
		this.rgOrgaoExpedidor = rgOrgaoExpedidor;
	}
	public String getRgDataExpedicao() {
		return rgDataExpedicao;
	}
	public void setRgDataExpedicao(String rgDataExpedicao) {
		this.rgDataExpedicao = rgDataExpedicao;
	}
	public String getNomePai() {
		return nomePai;
	}
	public void setNomePai(String nomePai) {
		this.nomePai = nomePai;
	}
	public String getNomeMae() {
		return nomeMae;
	}
	public void setNomeMae(String nomeMae) {
		this.nomeMae = nomeMae;
	}
	
	public String getSiglaUnidade() {
		return siglaUnidade;
	}
	public void setSiglaUnidade(String siglaUnidade) {
		this.siglaUnidade = siglaUnidade;
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
	public String getResLogradouro() {
		return resLogradouro;
	}
	public void setResLogradouro(String resLogradouro) {
		this.resLogradouro = resLogradouro;
	}


	public String getResBairro() {
		return resBairro;
	}
	public void setResBairro(String resBairro) {
		this.resBairro = resBairro;
	}
	public String getResCidade() {
		return resCidade;
	}
	public void setResCidade(String resCidade) {
		this.resCidade = resCidade;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getResQuadra() {
		return resQuadra;
	}
	public void setResQuadra(String resQuadra) {
		this.resQuadra = resQuadra;
	}
	public String getResLote() {
		return resLote;
	}
	public void setResLote(String resLote) {
		this.resLote = resLote;
	}
	public String getResUf() {
		return resUf;
	}
	public void setResUf(String resUf) {
		this.resUf = resUf;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getResNumero() {
		return resNumero;
	}
	public void setResNumero(String resNumero) {
		this.resNumero = resNumero;
	}

	public String getRgUfOrgaoExpedidor() {
		return rgUfOrgaoExpedidor;
	}

	public void setRgUfOrgaoExpedidor(String rgUfOrgaoExpedidor) {
		this.rgUfOrgaoExpedidor = rgUfOrgaoExpedidor;
	}
}
