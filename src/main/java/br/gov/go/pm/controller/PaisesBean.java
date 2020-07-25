package br.gov.go.pm.controller;

import br.gov.go.pm.modelo.Paises;
import br.gov.go.pm.util.exception.NegocioException;
import br.gov.go.pm.service.PaisesService;
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
public class PaisesBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{paisesService}")
	private PaisesService paisesService;

	private List<Paises> paises = new ArrayList<>();
	private List<Paises> paisesFilter = new ArrayList<>();
	private Paises paisSelecionado;	
	private Paises pais;

	@PostConstruct
	public void init(){
		limpar();
		paises = paisesService.listar();
	}
	
	public void limpar(){
		pais = new Paises();
	}
	
	public void salvaE() throws NegocioException{
		throw new NegocioException("Teste exception");
	}
	
	public void salvar(){

		paisesService.salvar(pais);
		FacesUtil.addSuccessMessage("País Salvo com Sucesso!");
		
		limpar();
		paises = paisesService.listar();
	}

	public void atualizarForm(){
		setPais(getPaisSelecionado());
	}
	
	public void excluir() throws NegocioException{
		paisesService.excluir(paisSelecionado);
		paises.remove(paisSelecionado);
		FacesUtil.addSuccessMessage("Pais " + paisSelecionado.getDescricao() + " excluído com sucesso");
		limpar();

	}
	
	
	//Getters and Setters


	public PaisesService getPaisesService() {
		return paisesService;
	}

	public void setPaisesService(PaisesService paisesService) {
		this.paisesService = paisesService;
	}

	public List<Paises> getPaises() {
		return paises;
	}

	public void setPaises(List<Paises> paises) {
		this.paises = paises;
	}

	public Paises getPaisSelecionado() {
		return paisSelecionado;
	}

	public void setPaisSelecionado(Paises paisSelecionado) {
		this.paisSelecionado = paisSelecionado;
	}

	public Paises getPais() {
		return pais;
	}

	public void setPais(Paises pais) {
		this.pais = pais;
	}

	public List<Paises> getPaisesFilter() {
		return paisesFilter;
	}

	public void setPaisesFilter(List<Paises> paisesFilter) {
		this.paisesFilter = paisesFilter;
	}
}//fim
