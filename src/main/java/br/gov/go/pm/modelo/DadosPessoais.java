package br.gov.go.pm.modelo;

/** A classe DadosPessoais é utilizada apenas para dados que podem ser persistidos*/
public class DadosPessoais {
	private Long codigo;
	private String cpf;
	private String rg;
	private String nome;
	private String postoGraduacao;
	private String dataNascimento;
	private String rgOrgaoExpedidor = "PMGO";
	private String rgUfExpedidor = "GO";
	private String rgDataExpedicao;
	private String pai;
	private String mae;
	private String unidadeOrigem;
	private String logradouro;
	private String bairro;
	private String Cidade;
	private String resLogradouro;
	private Integer resNumero;
	private String resBairro;
	private String resCidade;
	private String resEmail;
	private String resUf;
	private String status;
	
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
	public String getRg() {
		return rg;
	}
	public void setRg(String rg) {
		this.rg = rg;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getPostoGraduacao() {
		return postoGraduacao;
	}
	public void setPostoGraduacao(String postoGraduacao) {
		this.postoGraduacao = postoGraduacao;
	}
	public String getDataNascimento() {
		return dataNascimento;
	}
	public void setDataNascimento(String dataNascimento) {
		this.dataNascimento = dataNascimento;
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
	public String getRgDataExpedicao() {
		return rgDataExpedicao;
	}
	public void setRgDataExpedicao(String rgDataExpedicao) {
		this.rgDataExpedicao = rgDataExpedicao;
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
	public String getResLogradouro() {
		return resLogradouro;
	}
	public void setResLogradouro(String resLogradouro) {
		this.resLogradouro = resLogradouro;
	}
	public Integer getResNumero() {
		return resNumero;
	}
	public void setResNumero(Integer resNumero) {
		this.resNumero = resNumero;
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
	public String getResEmail() {
		return resEmail;
	}
	public void setResEmail(String resEmail) {
		this.resEmail = resEmail;
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
	
		
	
}
