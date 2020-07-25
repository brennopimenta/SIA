package br.gov.go.pm.controller;

import br.gov.go.pm.dao.ArmaDAO;
import br.gov.go.pm.dao.CrafDAO;
import br.gov.go.pm.enuns.StatusCarga;
import br.gov.go.pm.modelo.Arma;
import br.gov.go.pm.modelo.Carga;
import br.gov.go.pm.modelo.Sigma;
import br.gov.go.pm.service.CadastroArmaService;
import br.gov.go.pm.service.CargaService;
import br.gov.go.pm.service.general.AtualizaPaginaService;
import br.gov.go.pm.util.AppTools;
import br.gov.go.pm.util.exception.InfraException;
import br.gov.go.pm.util.exception.NegocioException;
import br.gov.go.pm.util.jsf.FacesUtil;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@ManagedBean
@ViewScoped
public class PesquisaArmaRestricaoBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private Logger log = Logger.getLogger(getClass());

	@ManagedProperty(value = "#{armaDAO}")
	ArmaDAO armaDAO;

	@ManagedProperty(value = "#{cadastroArmaService}")
	private CadastroArmaService armaService;

	@ManagedProperty(value = "#{cargaService}")
	private CargaService cargaService;

	@ManagedProperty(value = "#{crafDAO}")
	private CrafDAO crafDAO;

	@ManagedProperty(value = "#{atualizaPaginaService}")
	private AtualizaPaginaService atualizaPaginaService;


	private Arma arma;
	private Arma armaSelecionada;
	private List<Arma> listaArmas;
	private List<Carga> cargas;
	private List<Carga> filteredCargas;
	
	@PostConstruct
	public void inicializar() {
	//lazyArmas = new LazyArmaDataModel(armaDAO);
		limpar();
		listaArmas = armaDAO.buscarTodos();

	}

	public void limpar(){
		arma = new Arma();
		listaArmas = new ArrayList<>();
		filteredCargas = new ArrayList<>();

	}
	
	public void pesquisar(){
		if (arma.getNumeroArma() == null ||arma.getNumeroArma().trim().equals("")){
			listaArmas = armaDAO.buscarTodos();
		}else{
			listaArmas = armaDAO.buscarArmaEspecifica(arma.getNumeroArma());
			arma = new Arma();
		}
	}

	/** pesquisa itens e seta no objeto, para consulta e apresentacao na view para exclusao e edicao*/
	public String parametroView(){
		return "/paginas/arma/CadastroArma?=";
	}
	
	/**utilizado para abrir o diálogo de pesquisa de armas.*/
	public void abrirDialogo(){
		 AppTools.openDialogModal(true,false, false, "300", "600", "700",	"/paginas/arma/SelecaoDeArmas");
	}

	public void selectArma(Arma arma){
		RequestContext.getCurrentInstance().closeDialog(arma);
	}


	/**busca as cargas pela arma usando o serviço de carga.*/
	public void cargasDaArma(){
		if (armaSelecionada != null) {
			try {
				cargas = cargaService.listarCargaPelaArma(armaSelecionada.getNumeroArma());
			} catch (NegocioException e) {
				FacesUtil.addErrorMessage("Erro " + e.getMessage() + " na busca de cargas da pesquisa de arma");
			}
		}

	}

	public void atualizar(){
		limpar();
		atualizaPaginaService.AtualizaPagina();
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

	public List<Arma> getListaArmas() {
		return listaArmas;
	}

	public void setListaArmas(List<Arma> listaArmas) {
		this.listaArmas = listaArmas;
	}

	public ArmaDAO getArmaDAO() {
		return armaDAO;
	}

	public void setArmaDAO(ArmaDAO armaDAO) {
		this.armaDAO = armaDAO;
	}

	public void setArmaService(CadastroArmaService armaService) {
		this.armaService = armaService;
	}

	public List<Carga> getCargas() {
		return cargas;
	}

	public void setCargaService(CargaService cargaService) {
		this.cargaService = cargaService;
	}

	public void setAtualizaPaginaService(AtualizaPaginaService atualizaPaginaService) {
		this.atualizaPaginaService = atualizaPaginaService;
	}

	public List<Carga> getFilteredCargas() {
		return filteredCargas;
	}

	public void setFilteredCargas(List<Carga> filteredCargas) {
		this.filteredCargas = filteredCargas;
	}

	public void setCrafDAO(CrafDAO crafDAO) {
		this.crafDAO = crafDAO;
	}
}
