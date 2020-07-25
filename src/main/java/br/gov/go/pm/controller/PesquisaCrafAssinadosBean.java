package br.gov.go.pm.controller;

import br.gov.go.pm.dao.CrafDAO;
import br.gov.go.pm.enuns.StatusEmissao;
import br.gov.go.pm.modelo.Arma;
import br.gov.go.pm.modelo.Craf;
import br.gov.go.pm.service.general.AtualizaPaginaService;
import br.gov.go.pm.service.CadastroCrafService;
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
public class PesquisaCrafAssinadosBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{crafDAO}")
	private CrafDAO crafDAO;

	@ManagedProperty(value = "#{cadastroCrafService}")
	private CadastroCrafService cadastroCrafService;

	@ManagedProperty(value = "#{emissaoCrafBean}")
	private EmissaoCrafBean emissaoCrafBean;

	@ManagedProperty(value = "#{atualizarPagina}")
	private AtualizaPaginaService atualizarPagina;
	
	private Arma arma;
	private Craf crafSelecionado;
	private Craf craf;
	private List<Craf> listaCrafs;
	private List<Craf> listaCrafSelecionados = null;
	private List<Craf> listaCrafsSalvos = new ArrayList<>();
	private String busca;
	
	@PostConstruct
	public void inicializar() {
	 listaCrafs = crafDAO.buscarTodosAssinadosNaoImpressos();
	 limpar();
	
	}
	
	private void limpar() {
		arma = new Arma();
		craf = new Craf();
		busca = "";
	}

	public void buscar(){
		if (busca.equals("")) {
			buscarTodos();
		}else {
			listaCrafs = crafDAO.buscarTodosCrafAssinadosPorArmaOuNumeroCraf(busca);
		}
	}

	public void limpaPesquisa(){
		limpar();
		buscarTodos();
	}

	public void buscarTodos() {
		listaCrafs = crafDAO.buscarTodosAssinadosNaoImpressos();
	}
	
	public void listarTodosCrafAssinados(){
		listaCrafs = crafDAO.buscarTodosAssinados();
	}
	
	public void imprimirSelecionados(){
		try{

		  	for (Craf crafParaImprimir : listaCrafSelecionados){
		    	for(Craf crafDaListaCrafs : listaCrafs){
		    		if (crafDaListaCrafs.getNumeroCraf().equals(crafParaImprimir.getNumeroCraf())){
		    			
		    			craf = crafDAO.buscarPeloCodigo(crafDaListaCrafs.getCodigo());
						if (craf !=null) {
							craf.setStatusEmissao(StatusEmissao.IMPRESSO);
							cadastroCrafService.salvarStatusImpressao(craf);
							listaCrafsSalvos.add(crafParaImprimir);
						}

		    		} //Fim if
		    		
		    	} //fim 2º for

	    	} //fim 1º for

			if(!listaCrafsSalvos.isEmpty())
			imprimirCrafSelect(listaCrafsSalvos);
		  	atualizarPagina.AtualizaPagina();

		}catch (Exception e) {
			FacesUtil.addErrorMessage("Problema ao imprimir os itens selecionados: " + e.getMessage());
		}//fim try

	}//fim método




	
	public void impressaoUnicoCraf(){
		
		ArrayList<Craf> lista = new ArrayList<>();
		Craf iCraf = new Craf();
		iCraf = crafSelecionado;
		lista.add(iCraf);
		try {
			emissaoCrafBean.imprimirCrafSelecionadosPDF(lista);
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao imprimir um Craf único.");
			
		}
		
	}	
	
	public void imprimirCrafSelect(List<Craf> listaCrafImprimir) {
		emissaoCrafBean.imprimirCrafSelecionadosPDF(listaCrafImprimir);
	
	}
		

	//getters and setters
	
	public Arma getArma() {
		return arma;
	}

	public void setArma(Arma arma) {
		this.arma = arma;
	}

	public List<Craf> getListaCrafs() {
		if (listaCrafs == null){
		listaCrafs = new ArrayList<Craf>();	
		
		}
		return listaCrafs;
	}

	public void setListaCrafs(List<Craf> listaCrafs) {
		this.listaCrafs = listaCrafs;
	}

	public Craf getCrafSelecionado() {
		return crafSelecionado;
	}

	public void setCrafSelecionado(Craf crafSelecionado) {
		this.crafSelecionado = crafSelecionado;
	}

	public Craf getCraf() {
		return craf;
	}

	public void setCraf(Craf craf) {
		this.craf = craf;
	}

	public List<Craf> getListaCrafSelecionados() {
		return listaCrafSelecionados;
	}

	public void setListaCrafSelecionados(List<Craf> listaCrafSelecionados) {
		this.listaCrafSelecionados = listaCrafSelecionados;
	}

	public List<Craf> getListaCrafsSalvos() {
		if (listaCrafsSalvos == null){
			listaCrafsSalvos = new ArrayList<Craf>();	
			
			}
		return listaCrafsSalvos;
	}

	public void setListaCrafsSalvos(List<Craf> listaCrafsSalvos) {
		this.listaCrafsSalvos = listaCrafsSalvos;
	}

	public void setCrafDAO(CrafDAO crafDAO) {
		this.crafDAO = crafDAO;
	}

	public void setCadastroCrafService(CadastroCrafService cadastroCrafService) {
		this.cadastroCrafService = cadastroCrafService;
	}

	public void setEmissaoCrafBean(EmissaoCrafBean emissaoCrafBean) {
		this.emissaoCrafBean = emissaoCrafBean;
	}

	public void setAtualizarPagina(AtualizaPaginaService atualizarPagina) {
		this.atualizarPagina = atualizarPagina;
	}

	public String getBusca() {
		return busca;
	}

	public void setBusca(String busca) {
		this.busca = busca;
	}
}
