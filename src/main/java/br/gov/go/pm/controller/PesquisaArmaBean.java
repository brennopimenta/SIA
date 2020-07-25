package br.gov.go.pm.controller;

import br.gov.go.pm.dao.CrafDAO;
import br.gov.go.pm.enuns.StatusCarga;
import br.gov.go.pm.enuns.StatusEmissao;
import br.gov.go.pm.modelo.Arma;
import br.gov.go.pm.modelo.Carga;
import br.gov.go.pm.modelo.Sigma;
import br.gov.go.pm.service.CargaService;
import br.gov.go.pm.service.PesquisaArmaService;
import br.gov.go.pm.service.general.AtualizaPaginaService;
import br.gov.go.pm.util.AppTools;
import br.gov.go.pm.util.exception.InfraException;
import br.gov.go.pm.util.exception.NegocioException;
import br.gov.go.pm.util.jsf.FacesUtil;
import br.gov.go.pm.utilitarios.VisualizarListaResource;
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
public class PesquisaArmaBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private Logger log = Logger.getLogger(getClass());

	@ManagedProperty(value = "#{pesquisaArmaService}")
	private PesquisaArmaService pesquisaArmaService;

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
		listaArmas = buscaTodasAsArmas();

	}

	private List<Arma> buscaTodasAsArmas() {
		return pesquisaArmaService.buscarTodasAsArmas();
	}

	public void limpar(){
		arma = new Arma();
		listaArmas = new ArrayList<>();
		filteredCargas = new ArrayList<>();

	}
	
	public void pesquisar(){
		if (arma.getNumeroArma() == null ||arma.getNumeroArma().trim().equals("")){
			listaArmas = buscaTodasAsArmas();
		}else{
			listaArmas = pesquisaArmaService.buscarArmaEspecifica(arma.getNumeroArma());
			arma = new Arma();
		}
	}




	/**utilizado para abrir o diálogo de pesquisa de armas.*/
	public void abrirDialogo(){
		 AppTools.openDialogModal(true,false, false, "350", "1000", "700",	"/paginas/arma/SelecaoDeArmas");
	}

	public void selectArma(Arma arma){
		RequestContext.getCurrentInstance().closeDialog(arma);
	}

	public void liberarArmaParaCarga(){
		try {

			List<Sigma> novaListaSigmas = new ArrayList<>();
			/**se a arma selecionada não tem sigma e não foi enviada então pode ser liberada para carga*/
			if (armaSelecionada != null && armaSelecionada.getNumeroSigma() == null && armaSelecionada.getEnunsArmas().getStatusCarga() != null) {
				List<Carga> cargasPelaArma = cargaService.listarCargaPelaArma(armaSelecionada.getNumeroArma());

				if (cargasPelaArma.isEmpty()) {
					FacesUtil.addErrorMessage("Esta arma não está vinculada a nenhuma carga!");
				} else {
					for (Carga carga : cargasPelaArma) {
						/**busca novamente a carga específica para popular os sigmas pois na primeira busca não vêm todos os sigmas da carga.*/
						Carga cg = cargaService.buscarCargaComArmas(carga.getCodigo());
						/**A carga não pode ter sido enviada*/
						if (!cg.getStatus().equals(StatusCarga.ENVIADA)) {
							List<Sigma> sigmas = cg.getSigmas();
							for (Sigma sigma : sigmas) {
								/**percorre a lista de sigmas, verifica se a arma é diferente da que queremos liberar*/
								if (!sigma.getArma().getNumeroArma().equals(armaSelecionada.getNumeroArma())) {
									/**se sim, é gerada uma nova lista de sigmas(armas) com as outras armas da carga buscada */
									novaListaSigmas.add(sigma);

								}
							}

							armaSelecionada.getEnunsArmas().setStatusCarga(null);
							cargaService.salvaArma(armaSelecionada);
							/**é necessário salvar primeiro, mesmo se for excluir a carga.*/
							cargaService.salvarEdit(carga, novaListaSigmas);

							/**verifica se a nova lista(formada pelas armas que )*/
							if (novaListaSigmas.isEmpty())
								/**remove da carga a arma*/
								cargaService.excluir(carga);

							FacesUtil.addSuccessMessage("Arma " + armaSelecionada.getNumeroArma() + " disponível para próxima carga.");
						} else {
							FacesUtil.addErrorMessage("A arma " + armaSelecionada.getNumeroArma() + " não pode ser liberada pois está na carga " + cg.getCodigo() + " que já foi enviada");
						}
					}
				}

			} else {
				FacesUtil.addErrorMessage("Erro. Arma não está disponível para liberação!");
			}
		}catch (InfraException ie){
			FacesUtil.addErrorMessage("Erro ao salvar: " + ie.getMessage());
		}catch (NegocioException ne){
			FacesUtil.addErrorMessage(ne.getMessage());
		}catch (Exception e){
			FacesUtil.addErrorMessage("Erro: " + e.getMessage() + " .Por favor verifique sua conexão, caso persista contate o administrador!");
		}

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

	/*Relatorio de Armas em pdf*/

	public void gerarListaArmasParaImprimir() {
		listasDeArmasUtil(buscaTodasAsArmas(), "Lista de Todas as Armas Cadastradas");
	}

	public void gerarListaArmasParaImprimirSemSigma(){
		listasDeArmasUtil(pesquisaArmaService.buscarArmasSemSigma(), "Lista de Todas as Armas Sem Sigma");
	}

	public void gerarListaArmasParaImprimirSemProprietarios(){
		listasDeArmasUtil(pesquisaArmaService.buscarArmasSemProprietarios(), "Lista de Todas as Armas Sem Proprietários");
	}

	public void gerarListaArmasParaImprimirComRestricao(){
		listasDeArmasUtil(pesquisaArmaService.buscarArmasComRestricao(), "Lista de Todas as Armas COM Restrição");
	}

	private void listasDeArmasUtil(List<Arma> armas, String tit) {
		List<Arma> listaDeArmas = armas ;
		String titulo = tit;
		String lista = null;
		if (!listaDeArmas.isEmpty())
			lista = pesquisaArmaService.gerarListaArmasParaImprimir(listaDeArmas);
		VisualizarListaResource.visualizarLista(lista, titulo, StatusEmissao.PAISAGEM, "inline");
	}

	/**/

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

	public void setPesquisaArmaService(PesquisaArmaService pesquisaArmaService) {
		this.pesquisaArmaService = pesquisaArmaService;
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
