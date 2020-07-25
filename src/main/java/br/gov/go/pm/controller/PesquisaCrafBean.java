package br.gov.go.pm.controller;

import br.gov.go.pm.dao.ArmaDAO;
import br.gov.go.pm.dao.CrafDAO;
import br.gov.go.pm.modelo.Arma;
import br.gov.go.pm.modelo.Craf;
import br.gov.go.pm.service.general.AtualizaPaginaService;
import br.gov.go.pm.util.exception.NegocioException;
import br.gov.go.pm.util.jsf.FacesUtil;
import org.primefaces.context.RequestContext;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@ManagedBean
@ViewScoped
public class PesquisaCrafBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{armaDAO}")
	ArmaDAO armaDAO;

	@ManagedProperty(value = "#{crafDAO}")
	CrafDAO crafDAO;

	@ManagedProperty(value = "#{emissaoCrafBean}")
	private EmissaoCrafBean emissaoCrafBean;

	@ManagedProperty(value = "#{atualizaPaginaService}")
	private AtualizaPaginaService atualizaPaginaService;


	
	private Arma arma;
	private Arma armaSelecionada;
	private Craf crafSelecionado;
	private Craf craf;
	private String busca;

	private List<Craf> listaCrafs;
	
	@PostConstruct
	public void inicializar() {
	 listaCrafs = crafDAO.buscarTodos();
	 limpar();
	
	}
	
	private void limpar() {
		arma = new Arma();
		craf = new Craf();
		crafSelecionado = new Craf();
		busca = "";
		
	}

	public void buscar(){

		if (busca.equals("")) {
			buscarTodos();
		}else {
			listaCrafs = crafDAO.buscarTodosCrafPorArmaOuNumeroCraf(busca);
		}
	}

	public void buscarTodos() {
		listaCrafs = crafDAO.buscarTodos();
	}

	public void limpaPesquisa(){
		limpar();
		buscarTodos();
	}
	
	/**método de cancelamento de craf*/
	public void cancelarCraf(){
		try {
			crafDAO.cancelarCraf(crafSelecionado);
			FacesUtil.addSuccessMessage("Craf " + crafSelecionado.getNumeroCraf() + " Cancelado com sucesso!");
			atualizaPaginaService.AtualizaPagina();
		} catch (NegocioException e) {
			FacesUtil.addErrorMessage("Erro " + e.getMessage() + " ao cancelar o craf. Procure o Administrador.");
		}
		
	}
	
	/**utilizado para abrir o diálogo de pesquisa de armas.*/
	public void abrirDialogo(){
		Map<String, Object> opcoes = new HashMap<>();
		opcoes.put("modal", true);
		opcoes.put("resizable", false);
		opcoes.put("contentHeight", 500);
		opcoes.put("responsive", true);
		opcoes.put("appendTo","@body");
				
		RequestContext.getCurrentInstance().openDialog("/arma/SelecaoDeArmas", opcoes, null);
		
	}
	
	public void selectArma(Arma arma){
		RequestContext.getCurrentInstance().closeDialog(arma);
	}
	
	public void impressaoUnicoCraf(){

		ArrayList<Craf> lista = new ArrayList<Craf>();  
		Craf iCraf = new Craf();
		iCraf = crafSelecionado;
		lista.add(iCraf);
		try {
			emissaoCrafBean.imprimirCrafSelecionadosPDF(lista);
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao imprimir um Craf único.");

		}

	}	

	public void imprimirCrafSelecionadosPDF(List<Craf> listaCrafImprimir){
		emissaoCrafBean.imprimirCrafSelecionadosPDF(listaCrafImprimir);

	}

		
	
	//getters and setters
	
	public Arma getArmaSelecionada() {
		return armaSelecionada;
	}

	public void setArmaSelecionada(Arma armaSelecionada) {
		this.armaSelecionada = armaSelecionada;
	}

	public Arma getArma() {
		return arma;
	}

	public void setArma(Arma arma) {
		this.arma = arma;
	}

	public List<Craf> getListaCrafs() {
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

	public void setArmaDAO(ArmaDAO armaDAO) {
		this.armaDAO = armaDAO;
	}

	public void setCrafDAO(CrafDAO crafDAO) {
		this.crafDAO = crafDAO;
	}

	public void setAtualizaPaginaService(AtualizaPaginaService atualizaPaginaService) {
		this.atualizaPaginaService = atualizaPaginaService;
	}

	public String getBusca() {
		return busca;
	}

	public void setBusca(String busca) {
		this.busca = busca;
	}

	public void setEmissaoCrafBean(EmissaoCrafBean emissaoCrafBean) {
		this.emissaoCrafBean = emissaoCrafBean;
	}
}
