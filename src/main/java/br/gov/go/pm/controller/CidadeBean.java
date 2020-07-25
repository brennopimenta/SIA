package br.gov.go.pm.controller;

import br.gov.go.pm.modelo.Cidade;
import br.gov.go.pm.service.CidadeService;
import br.gov.go.pm.service.general.AtualizaPaginaService;
import br.gov.go.pm.util.exception.NegocioException;
import br.gov.go.pm.util.jsf.FacesUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@ViewScoped
public class CidadeBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{cidadeService}")
	private CidadeService cidadeService;

	@ManagedProperty(value = "#{atualizaPaginaService}")
	AtualizaPaginaService atualizaPaginaService;

	private List<Cidade> cidades;
	private Cidade cidadeSelecionada;
	private Cidade cidade;
	private String atributoBusca;

	@PostConstruct
	public void init(){
		limpar();
		cidades = cidadeService.listar();
	}

	public void buscarTodos(){
		cidades = cidadeService.listar();
	}

	public void buscar(){

		if (atributoBusca.equals("")) {
			buscarTodos();
		}else {
			cidades = cidadeService.buscarPorDescricao(atributoBusca);
		}
	}
	
	public void limpar(){
		cidade = new Cidade();
		List<Cidade> cidades = new ArrayList<>();
	}
		
	public void salvar(){

		cidadeService.salvar(cidade);
		FacesUtil.addSuccessMessage("Cidade Salva com Sucesso!");
		
		limpar();
		atualizaPaginaService.AtualizaPagina();

	}

	public void edicao(){
		setCidade(cidadeSelecionada);

	}
	
	public void excluir() throws NegocioException{
		cidadeService.excluir(cidadeSelecionada);
		cidades.remove(cidadeSelecionada);
		FacesUtil.addSuccessMessage("cidade " + cidadeSelecionada.getDescricao() + " excluída com sucesso");
		limpar();

	}
	
	
	//Getters and Setters

	public void setCidadeService(CidadeService cidadeService) {
		this.cidadeService = cidadeService;
	}

	public void setAtualizaPaginaService(AtualizaPaginaService atualizaPaginaService) {
		this.atualizaPaginaService = atualizaPaginaService;
	}

	public List<Cidade> getCidades() {
		return cidades;
	}

	public void setCidades(List<Cidade> cidades) {
		this.cidades = cidades;
	}

	public Cidade getCidadeSelecionada() {
		return cidadeSelecionada;
	}

	public void setCidadeSelecionada(Cidade cidadeSelecionada) {
		this.cidadeSelecionada = cidadeSelecionada;
	}

	public Cidade getCidade() {
		return cidade;
	}

	public void setCidade(Cidade cidade) {
		this.cidade = cidade;
	}

	public String getAtributoBusca() {
		return atributoBusca;
	}

	public void setAtributoBusca(String atributoBusca) {
		this.atributoBusca = atributoBusca;
	}


}//fim
