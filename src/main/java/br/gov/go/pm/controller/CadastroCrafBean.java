package br.gov.go.pm.controller;

import br.gov.go.pm.dao.ArmaDAO;
import br.gov.go.pm.dao.CrafDAO;
import br.gov.go.pm.enuns.StatusCraf;
import br.gov.go.pm.modelo.Arma;
import br.gov.go.pm.modelo.Craf;
import br.gov.go.pm.security.SegurancaDetalhe;
import br.gov.go.pm.service.CadastroCrafService;
import br.gov.go.pm.util.exception.NegocioException;
import br.gov.go.pm.util.jsf.FacesUtil;
import br.gov.go.pm.utilitarios.MostraDataAtual;
import org.apache.log4j.Logger;
import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

@ManagedBean
@ViewScoped
public class CadastroCrafBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private Logger log = Logger.getLogger(getClass());
	
	private Craf craf;
	private Craf crafAux;
	/**Dados da arma a ser buscada e setada em craf. Também utilizada para mostrar dados da arma ao usuário.*/
	private Arma arma; 
	private List<Arma> armas = new ArrayList<>();
	private List<StatusCraf> statusCraf;
	private Boolean camposDisabled = false;
	private String param = null;
	private boolean statusEnvioEmail;

	@Autowired
	private SegurancaDetalhe segurancaDetalhe;

	@ManagedProperty(value = "#{cadastroCrafService}")
	private CadastroCrafService cadastroCrafService;

	@ManagedProperty(value = "#{armaDAO}")
	private ArmaDAO armaDAO;

	@ManagedProperty(value = "#{crafDAO}")
	private CrafDAO crafDAO;

	public void preRender() throws IOException {

		param = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("craf");

		if(param != null){
			edicao(param);
		}
	}


	@PostConstruct
	public void init(){
	    limpar();
	    armas = armaDAO.buscarTodos();
	    statusCraf = Arrays.asList(StatusCraf.values());
	    camposDisabled = true;
	    buscarStatusEnvioDeEmail();

	}

	/**utilizado para mostrar a data atual em um campo data somente leitura*/
	public Date getDataDeHoje(){
		return MostraDataAtual.getDataDeHoje();

	}


	/** métodos exclusivos para seleção da arma */
	public void armaSelecionada(SelectEvent evento){
		camposDisabled = false;
		Arma arma = (Arma) evento.getObject();
		craf.setArma(arma);
		buscaESetaDadosArma();
	}
	
	@NotNull(message = "Erro ao salvar no banco de dados. Arma inexistente!")
	public String getNumeroArmaDoBean(){
		return craf.getArma() == null ? null : craf.getArma().getNumeroArma();
	}
	
	public void setNumeroArmaDoBean(String arma){
		
	}
	
	/** busca a arma selecionada*/
	public void buscaESetaDadosArma() {
		Optional<Craf> crafAux;
		/**verifica a arma buscadando pelo codigo*/
		arma = armaDAO.buscarPeloCodigo(craf.getArma().getCodigo());
		
		/**com a arma encontrada: garante que tem sigma e não tem status restritivo cadastrado. */
		if (arma !=null && arma.getNumeroSigma() != null && arma.getEnunsArmas().getStatusRestritivo() == null){
			
			/**faz uma busca em Craf pela arma verificada com intuito de ver se já existe um craf(<> de Cancelado) para a mesma, pois só vai existir um.*/
			crafAux = cadastroCrafService.verficaSeExisteCrafParaArma(craf.getArma().getNumeroArma());
			
			/** se não encontrado um craf na busca, significa que não foram encontrados craf <> de Cancelado e pode gerar  */
			if (!crafAux.isPresent()){ //se não achou nenhum craf com a arma passada
				atribuir();
			/**significa que foi encontrado UM CRAF não Cancelado e não permite gerar até o cancelamento*/
			}else {
				FacesUtil.addErrorMessage("Ops! Existe um Craf que não foi cancelado para esta Arma. Por favor efetue o cancelamento antes.");
				limpar();
			}
				
		}else{
			if (arma.getNumeroSigma() == null && arma.getEnunsArmas().getStatusRestritivo() == null){
				FacesUtil.addErrorMessage("NÃO é possivel emitir craf pra esta arma pois ela ainda não possui SIGMA");
			}else{
				FacesUtil.addErrorMessage("NÃO é possivel emitir craf pra esta arma pois ela possui STATUS de " + arma.getEnunsArmas().getStatusRestritivo().getDescricao());
			}
			
			limpar();
		
		}
	}
	
	/**utilizado no setar*/
	public void atribuir() {
		try {

			craf.setArma(arma);
			craf.setRg(arma.getRgProprietario());
			craf.setCpf(arma.getCpfNovoProprietario());
			craf.setNome(arma.getNomeNovoProprietario());

			/**busca o último nº do craf e atribui.*/
			craf.setNumeroCraf(cadastroCrafService.ultimoCraf());
		}catch (NegocioException ne){
			FacesUtil.addErrorMessage(ne.getMessage());

		}catch (Exception e){
			FacesUtil.addErrorMessage("Erro na atribuição dos dados da arma ou numero Craf " + e.getMessage());
		}
	}

	public void salvar() {
		try {
			if (param == null || param.isEmpty()) {
				if (cadastroCrafService.buscaCrafExistente(craf).isPresent()) {

					FacesUtil.addErrorMessage("Craf já utilizado. Foi sugerido um craf disponível.");
				} else {
					atribuir();
					cadastroCrafService.salvar(craf);

					/**envia email avisando que o craf está pronto*/
					if (statusEnvioEmail == true)
						cadastroCrafService.enviarEmail(craf);

					FacesUtil.addSuccessMessage("Craf Salvo com Sucesso!!");
					limpar();
				}

			}
		}catch (NegocioException e){
			FacesUtil.addErrorMessage("Erro ao salvar: " + e.getMessage() + "  ou contate o administrador");
			atribuir();

		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao salvar craf. Verifique sua conexão ou contate o administrador. Erro: " + e.getMessage());
			log.debug(e.getMessage(), e.getCause());
		}

	}

	public void edicao(String param) throws IOException {
		Craf resultado = crafDAO.buscarPeloCodigo(Long.valueOf(param));
		camposDisabled = false;
		setCraf(resultado);
	}

	public void buscarStatusEnvioDeEmail(){
		try {
			/*se não existir registro fica false*/
			boolean statusEmail = cadastroCrafService.buscarStatusEnvioEmail();
			setStatusEnvioEmail(statusEmail);
		}catch (Exception e){
			FacesUtil.addAlertMessage("Ops! Houve um erro na busca de suas configurações para envio de email, portanto não vai conseguir enviar os emails! Erro: " + e.getMessage());
			log.error(e.getMessage(), e.getCause());
		}
	}

	public void limpar() {
		 craf = new Craf();
		 arma = new Arma();
		 crafAux = new Craf();
	}
	
	//getters and setters
	
	public Date getHoje(){
		return new Date();
	}


	public Craf getCraf() {
		if (craf == null){
			craf = new Craf();
		}
		return craf;
	}


	public void setCraf(Craf craf) {
		this.craf = craf;
	}


	public Arma getArma() {
		if (arma == null){
			arma = new Arma();
		}
		return arma;
	}
	
	public void setArma(Arma arma) {
		this.arma = arma;
	}


	public Craf getCrafAux() {
		if (craf == null){
			craf = new Craf();
		}
		return crafAux;
	}

	public void setCrafAux(Craf crafAux) {
		this.crafAux = crafAux;
	}
	public List<Arma> getArmas() {
		return armas;
	}
	public void setArmas(List<Arma> armas) {
		this.armas = armas;
	}
	public List<StatusCraf> getStatusCraf() {
		return statusCraf;
	}
	public void setStatusCraf(List<StatusCraf> statusCraf) {
		this.statusCraf = statusCraf;
	}
	public Boolean getCamposDisabled() {
		return camposDisabled;
	}
	public void setCamposDisabled(Boolean camposDisabled) {
		this.camposDisabled = camposDisabled;
	}

	public CadastroCrafService getCadastroCrafService() {
		return cadastroCrafService;
	}

	public void setCadastroCrafService(CadastroCrafService cadastroCrafService) {
		this.cadastroCrafService = cadastroCrafService;
	}

	public void setArmaDAO(ArmaDAO armaDAO) {
		this.armaDAO = armaDAO;
	}

	public void setCrafDAO(CrafDAO crafDAO) {
		this.crafDAO = crafDAO;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public boolean isStatusEnvioEmail() {
		return statusEnvioEmail;
	}

	public void setStatusEnvioEmail(boolean statusEnvioEmail) {
		this.statusEnvioEmail = statusEnvioEmail;
	}
}//fim