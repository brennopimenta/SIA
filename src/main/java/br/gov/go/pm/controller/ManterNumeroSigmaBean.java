package br.gov.go.pm.controller;

import br.gov.go.pm.dao.ArmaDAO;
import br.gov.go.pm.dao.BaseNumeroSigmaDAO;
import br.gov.go.pm.modelo.Arma;
import br.gov.go.pm.modelo.ArmaNumeroSigma;
import br.gov.go.pm.service.ManterNumeroSigmaService;
import br.gov.go.pm.util.exception.NegocioException;
import br.gov.go.pm.util.jsf.FacesUtil;
import br.gov.go.pm.utilitarios.MostraDataAtual;
import org.apache.log4j.Logger;
import org.primefaces.event.SelectEvent;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
public class ManterNumeroSigmaBean implements Serializable {

	private Logger log = Logger.getLogger(ManterNumeroSigmaBean.class);

	private static final long serialVersionUID = 1L;
	
	private ArmaNumeroSigma armaNumeroSigma;
	private Arma arma; 
	private List<ArmaNumeroSigma> armasNumerosSigma;
	private List<Arma> armas = new ArrayList<>();
	private ArmaNumeroSigma numeroSigmaSelecionado;
	private Boolean habilitar = true;

	@ManagedProperty(value = "#{manterNumeroSigmaService}")
	private ManterNumeroSigmaService manterNumeroSigmaService;

	@ManagedProperty(value = "#{baseNumeroSigmaDAO}")
	private BaseNumeroSigmaDAO baseNumeroSigmaDAO;

	@ManagedProperty(value = "#{armaDAO}")
	private ArmaDAO armaDAO;

	public void preRender() throws IOException {
		/*String param = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("armaNumeroSigma");*/
	}


	@PostConstruct
	public void inicializar() {
		limpar();
		armasNumerosSigma = this.baseNumeroSigmaDAO.buscarTodos();//lista todos os numeros de sigma cadastrados.
		armas = armaDAO.buscarTodos();
	    
	}
	
	/** métodos exclusivos para seleção da arma */
	public void armaSelecionada(SelectEvent evento){
		Arma arma = (Arma) evento.getObject();
		armaNumeroSigma.setNumeroArma(arma.getNumeroArma());
		buscarArmaExistenteNoCadastroNumeroSigma();

	}

	/**utilizado para receber e exibir a arma após ela ser consultada no seleção de armas.*/
	@NotNull
	public String getNumeroArmaDoBean(){
		return armaNumeroSigma.getNumeroArma() == null ? null : armaNumeroSigma.getNumeroArma();
	}
	
	public void setNumeroArmaDoBean(String arma){
		
	}

	/**utilizado para mostrar a data atual em um campo data somente leitura*/
	public Date getDataDeHoje(){return MostraDataAtual.getDataDeHoje();
	}


	public void salvar() {
		try {
			manterNumeroSigmaService.salvar(armaNumeroSigma);
			/**busca novamente para apresentar na lista*/
			armasNumerosSigma = baseNumeroSigmaDAO.buscarTodos();

			FacesUtil.addSuccessMessage("Numero salvo na base de numero sigma com sucesso!");

			habilitar = true;
			limpar();

			
		} catch (NegocioException e) {
			habilitar = false;
			log.error(e);
			FacesUtil.addErrorMessage(e.getMessage());

		}catch (Exception ex){
			FacesUtil.addErrorMessage("Erro refaça o processo. Caso persista o erro contatar o administrador");
		}
	

	}

	public void edicao(){
		setArmaNumeroSigma(getNumeroSigmaSelecionado());
		habilitar = false;
	}

	public void disponibilizar(){
		habilitar = false;

	}


	public void excluir() throws Exception{
		try{
		baseNumeroSigmaDAO.excluir(numeroSigmaSelecionado);
		this.armasNumerosSigma.remove(numeroSigmaSelecionado);
		
		FacesUtil.addSuccessMessage("Numero sigma " + numeroSigmaSelecionado.getNumeroSigma() + " excluído com sucesso");
		limpar();
		
		}catch(Exception e){
		FacesUtil.addErrorMessage("Erro na exclusão no serviço de manutenção sigma. Causa:  " + e.getMessage() );
		}
		
	}
	
	public void limpar() {
		armaNumeroSigma = new ArmaNumeroSigma();
		arma = new Arma();
			
	}

	public void buscarArmaExistenteNoCadastroNumeroSigma(){
		numeroSigmaSelecionado =  this.baseNumeroSigmaDAO.buscarArmaSelecionada(armaNumeroSigma.getNumeroArma());
        if(numeroSigmaSelecionado !=null){
		    limpar(); 
        	FacesUtil.addErrorMessage("AVISO: Esta Arma já possui o sigma nº " + numeroSigmaSelecionado.getNumeroSigma() + ". Edite a arma e faça as alterações desejadas.");
			 
         }
	
	}
	
	public void pesquisar(){
		
		if((armaNumeroSigma.getNumeroArma() == null ||armaNumeroSigma.getNumeroArma().trim().equals("")) && 
				(armaNumeroSigma.getNumeroSigma() == null ||armaNumeroSigma.getNumeroSigma().trim().equals(""))){
			armasNumerosSigma = this.baseNumeroSigmaDAO.buscarTodos();
			this.armaNumeroSigma = new ArmaNumeroSigma();
		}else{
			armasNumerosSigma =  this.baseNumeroSigmaDAO.buscarPelaArma(armaNumeroSigma.getNumeroArma(), armaNumeroSigma.getNumeroSigma());
			this.armaNumeroSigma = new ArmaNumeroSigma();
		}
	
	}

	//getters and setters
	public ArmaNumeroSigma getArmaNumeroSigma() {
		if (armaNumeroSigma == null){
			armaNumeroSigma = new ArmaNumeroSigma();
		}
		
		return armaNumeroSigma;
	}

	public void setArmaNumeroSigma(ArmaNumeroSigma armaNumeroSigma) {
		this.armaNumeroSigma = armaNumeroSigma;
	}

	public Arma getArma() {
		return arma;
	}

	public void setArma(Arma arma) {
		this.arma = arma;
	}

	public List<ArmaNumeroSigma> getArmasNumerosSigma() {
		return armasNumerosSigma;
	}

	public void setArmasNumerosSigma(List<ArmaNumeroSigma> armasNumerosSigma) {
		this.armasNumerosSigma = armasNumerosSigma;
	}

	public List<Arma> getArmas() {
		return armas;
	}

	public void setArmas(List<Arma> armas) {
		this.armas = armas;
	}

	public ArmaNumeroSigma getNumeroSigmaSelecionado() {
		return numeroSigmaSelecionado;
	}

	public void setNumeroSigmaSelecionado(ArmaNumeroSigma numeroSigmaSelecionado) {
		this.numeroSigmaSelecionado = numeroSigmaSelecionado;
	}

	public ManterNumeroSigmaService getManterNumeroSigmaService() {
		return manterNumeroSigmaService;
	}

	public void setManterNumeroSigmaService(ManterNumeroSigmaService manterNumeroSigmaService) {
		this.manterNumeroSigmaService = manterNumeroSigmaService;
	}

	public BaseNumeroSigmaDAO getBaseNumeroSigmaDAO() {
		return baseNumeroSigmaDAO;
	}

	public void setBaseNumeroSigmaDAO(BaseNumeroSigmaDAO baseNumeroSigmaDAO) {
		this.baseNumeroSigmaDAO = baseNumeroSigmaDAO;
	}

	public ArmaDAO getArmaDAO() {
		return armaDAO;
	}

	public void setArmaDAO(ArmaDAO armaDAO) {
		this.armaDAO = armaDAO;
	}

	public Boolean getHabilitar() {
		return habilitar;
	}

	public void setHabilitar(Boolean habilitar) {
		this.habilitar = habilitar;
	}
}
