package br.gov.go.pm.controller;

import br.gov.go.pm.dao.CrafDAO;
import br.gov.go.pm.enuns.Assinatura;
import br.gov.go.pm.enuns.Profile;
import br.gov.go.pm.enuns.StatusSituacao;
import br.gov.go.pm.modelo.Arma;
import br.gov.go.pm.modelo.Assinaturas;
import br.gov.go.pm.modelo.Craf;
import br.gov.go.pm.modelo.Usuario;
import br.gov.go.pm.service.AssinaCrafService;
import br.gov.go.pm.service.general.AtualizaPaginaService;
import br.gov.go.pm.util.exception.InfraException;
import br.gov.go.pm.util.exception.NegocioException;
import br.gov.go.pm.util.jsf.FacesUtil;
import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@ViewScoped
public class PesquisaCrafNaoAssinadosBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private Logger log = Logger.getLogger(getClass());

	@ManagedProperty(value = "#{crafDAO}")
	private CrafDAO crafDAO;

	@ManagedProperty(value = "#{assinaCrafService}")
	private AssinaCrafService assinaCrafService;

	@ManagedProperty(value = "#{emissaoCrafBean}")
	private EmissaoCrafBean emissaoCrafBean;

	@ManagedProperty(value = "#{atualizaPaginaService}")
	private AtualizaPaginaService atualizaPaginaService;

	private Arma arma;
	private Arma armaSelecionada;
	private Craf crafSelecionado;
	private String senhaAssinatura;
	private Craf craf;
	private List<Craf> listaCrafs = new ArrayList<>();
	private List<Craf> listaCrafSelecionados = null;
	private List<Craf> listaCrafsSalvos = new ArrayList<>();
	private String busca;
	
	@PostConstruct
	public void inicializar() {
	 listaCrafs = crafDAO.buscarTodosNaoAssinados();
	  limpar();
	  senhaAssinatura = "";
	}
	
	private void limpar() {
		arma = new Arma();
		craf = new Craf();
		crafSelecionado = new Craf();
		
	}

	public void buscar(){

		if (busca.equals("")) {
			buscarTodos();
		}else {
			listaCrafs = crafDAO.buscarTodosCrafPendentesPorArmaOuNumeroCraf(busca);
		}
	}

	public void limpaPesquisa(){
		limpar();
		buscarTodos();
	}

	public void buscarTodos() {
		listaCrafs = crafDAO.buscarTodosNaoAssinados();
	}

	public void confirmaSenha(){
		try {
			if(!senhaAssinatura.trim().isEmpty()) {
				Boolean confirm = assinaCrafService.confirmaSenha(senhaAssinatura);
				if (confirm)
					assinar();
				else FacesUtil.addErrorMessage("Senha não confere!");
			}else{
				FacesUtil.addErrorMessage("Por favor digite a senha");
			}
		}catch (NegocioException e){
			FacesUtil.addErrorMessage("Erro: " + e.getMessage());

		} catch (Exception e) {
			log.error(e);
			FacesUtil.addErrorMessage("Erro em confirmar a senha. Saia e entre novamente do sistema, refaça o processo, caso persista o erro contate o Administrador!");
		}
	}

	/** assinar todos os crafs que não foram assinados*/
	public void assinar() throws NegocioException{
		/**verifica o perfil do usuário*/
		String userProfile = assinaCrafService.getPerfilUsuario();
		StatusSituacao statusUser = assinaCrafService.getUsuario().getStatus();

	try {
		if (statusUser.equals(StatusSituacao.ATIVO)) {
            /**Verfifica se o usuário logado tem profile de CMT ou Aut. Delegada*/
            if (userProfile.equals(Profile.CMT.name()) || userProfile.equals(Profile.AUTORIDADE_DELEGADA.name()))
                assinaAux();
            else
                FacesUtil.addErrorMessage("Desculpe! É necessário ter perfil de Comandante ou Autoridade por ele delegada para assinar o CRAF.");
        }else {
            FacesUtil.addErrorMessage("Desculpe! O Usuário, por alguma razão, não está Ativo. Por favor contate o Administrador!");
        }

		}catch (NegocioException ne){
			FacesUtil.addErrorMessage(ne.getMessage());
		} catch (InfraException ie){
			FacesUtil.addErrorMessage(ie.getMessage());
			log.error(ie, ie.getCause());
		} catch (Exception e){
			FacesUtil.addErrorMessage("Erro ao definir assinatura para o Craf " + e.getMessage());
			log.error(e, e.getCause());
		}
	}//fim método

	public void assinaAux() throws NegocioException {

		if (listaCrafSelecionados.isEmpty())
			throw new NegocioException("Não há craf selecionados para assinar.");

		Usuario usuarioLogado = assinaCrafService.getUsuario();
		try {
			/**pelo usuário atual, após verificado seu perfil de cmt ou aut. deleg., verifica se tem assinatura*/
			Assinaturas assinaturaAtiva = assinaCrafService.buscaAssinaturaAtiva(usuarioLogado.getCpf());

			if(assinaturaAtiva != null) {
				for (Craf crafParaAssinar : listaCrafSelecionados) {
					for (Craf crafDaListaCrafs : listaCrafs) {
						if (crafDaListaCrafs.getNumeroCraf().equals(crafParaAssinar.getNumeroCraf())) {
							craf = crafDAO.buscarPeloCodigo(crafDaListaCrafs.getCodigo());
								definirPerfilAssinatura();
								craf.setAutorAssinatura(assinaturaAtiva);

							assinaCrafService.salvarAssinatura(craf);
							listaCrafsSalvos.add(crafParaAssinar);
							atualizaPaginaService.AtualizaPagina();
						} //FIM IF
					} //fim 2º for
				} //fim 1º for
			}else{
				throw new NegocioException("Ops! Autoridade não possui assinatura ativa. Contate o Administrador!");
			}

			/**trata a excessão de não achar assinatura ativa para o usuário*/
		}catch (NegocioException ne){
			FacesUtil.addErrorMessage(ne.getMessage());
		}
	}

	/**seta a assinatura a ser inserida para o craf.
	 * É NECESSÁRIO TER UM PROFILE DE CMT*/
	public void definirPerfilAssinatura() throws NegocioException {

		String userProfile = assinaCrafService.getPerfilUsuario();

		/** pergunta se o profile do usuário é igual ao enum CMT ou AUTORIDADE DELEGADA, se sim grava na assinatura o enum autoridade ou
		 * autoridade_delegada. Lembrando que no CHANCELA tem po profile de CMT, assinando como este*/

		switch (userProfile) {
			case "CMT":
				craf.setAssinatura(Assinatura.AUTORIDADE);
				break;
			case "AUTORIDADE_DELEGADA":
				craf.setAssinatura(Assinatura.AUTORIDADE_DELEGADA);
				break;
			default:
				craf.setAssinatura(null);
		}
	}

	public void impressaoUnicoCraf() {

		listaCrafs = new ArrayList<>();
		Craf iCraf = new Craf();
		iCraf = crafSelecionado;
		/**passado uma lista pois o método pede uma lista*/
		listaCrafs.add(iCraf);
		try {
			emissaoCrafBean.imprimirCrafSelecionadosPDF(listaCrafs);
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao imprimir um Craf único.");
		}

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

	public void setAssinaCrafService(AssinaCrafService assinaCrafService) {
		this.assinaCrafService = assinaCrafService;
	}

	public void setEmissaoCrafBean(EmissaoCrafBean emissaoCrafBean) {
		this.emissaoCrafBean = emissaoCrafBean;
	}

	public void setAtualizaPaginaService(AtualizaPaginaService atualizaPaginaService) {
		this.atualizaPaginaService = atualizaPaginaService;
	}

	public String getSenhaAssinatura() {
		return senhaAssinatura;
	}

	public void setSenhaAssinatura(String senhaAssinatura) {
		this.senhaAssinatura = senhaAssinatura;
	}

	public String getBusca() {
		return busca;
	}

	public void setBusca(String busca) {
		this.busca = busca;
	}
}
