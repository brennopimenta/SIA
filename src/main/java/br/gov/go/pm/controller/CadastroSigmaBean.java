package br.gov.go.pm.controller;

import br.gov.go.pm.dao.ArmaDAO;
import br.gov.go.pm.dao.SigmaDAO;
import br.gov.go.pm.enuns.Status;
import br.gov.go.pm.enuns.TipoDoc;
import br.gov.go.pm.modelo.*;
import br.gov.go.pm.service.CadastroSigmaService;
import br.gov.go.pm.service.general.ConsumirServicoSicad;
import br.gov.go.pm.util.exception.NegocioException;
import br.gov.go.pm.util.jsf.FacesUtil;
import br.gov.go.pm.service.general.ManipulaData;
import br.gov.go.pm.utilitarios.MostraDataAtual;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIViewRoot;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
public class CadastroSigmaBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private Logger log = Logger.getLogger(getClass());

	private Sigma sigma;
	private Sigma sigmaSelecionado;
	private DadosSicad ds;
	private DadosPessoais dp;
	private Arma arma;
	private List<Arma> armas = new ArrayList<>();
	private List<TipoDoc> tiposDoc;
	private boolean zeraArma = false;
	private Boolean camposReadyOnly;
	private boolean erroAoSalvarSigma = false;
	private boolean liberaNumDoc = false;

	@ManagedProperty(value = "#{cadastroSigmaService}")
	private CadastroSigmaService cadastroSigmaService;

	@ManagedProperty(value = "#{armaDAO}")
	private ArmaDAO armaDAO;

	@ManagedProperty(value = "#{sigmaDAO}")
	private SigmaDAO sigmaDAO;

	@ManagedProperty(value = "#{consumirServicoSicad}")
	private ConsumirServicoSicad consumirServicoSicad;

	@ManagedProperty(value = "#{manipulaData}")
	private ManipulaData manipulaData;

    public void preRender() throws IOException {

        String param = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("sigma");

        if (param != null) {
            edicao(param);

        }
    }

    @PostConstruct
	public void init(){
		ds = new DadosSicad();
	    dp =  new DadosPessoais();
	    sigma = new Sigma();
	    arma = new Arma();
	    armas = armaDAO.buscarTodos();
	    tiposDoc = Arrays.asList(TipoDoc.values());
        camposReadyOnly = true;
    }

	/**utilizado para mostrar a data atual em um campo data somente leitura*/
	public Date getDataDeHoje(){
		return MostraDataAtual.getDataDeHoje();
	}


	/** Busca dados no Web Service do Sicad */
	public void buscarNoSicad() {
				
		ds = consumirServicoSicad.busca(sigma.getCpf());
		
		if (ds != null){

			setarDadosPessoais();
			setarEnderecoResidencial();
			setarEnderecoFuncional(); 
		}else{
			FacesUtil.addErrorMessage("Não foi possível encontrar o CPF no cadastro de Policiais Militares. Campos liberados!");
			RequestContext.getCurrentInstance().execute("limpaCamposSigma();");
			
			sigma.setEnderecoResidencial(new EnderecoResidencial());
			sigma.setEnderecoFuncional(new EnderecoFuncional());
			sigma.setArma(new Arma());
			camposReadyOnly = false;
		}
	}
	
	/** Atribui aos dados pessoais as informações pertinentes buscadas pelo web service. */
	public void setarDadosPessoais() {
		/**dados que vêm do sicad e passam por DadosPessoais*/
		sigma.setRg(ds.getRgMilitar());
		sigma.setCpf(ds.getCpf());
		sigma.setNome(ds.getNome());
		sigma.setPostoGraduacao(ds.getPostoSiglaGrad());

		if(ds.getDtNascimento() !=null)
		sigma.getDadosPessoaisSigma().setDataNascimento(manipulaData.converteLongEmDate(Long.valueOf(ds.getDtNascimento())));
		sigma.getDadosPessoaisSigma().setRgOrgaoExpedidor(ds.getRgOrgaoExpedidor());
		sigma.getDadosPessoaisSigma().setRgUfExpedidor(ds.getRgUfOrgaoExpedidor());

		if(ds.getRgDataExpedicao() !=null)
		sigma.getDadosPessoaisSigma().setRgDataExpedicao(manipulaData.converteLongEmDate(Long.valueOf(ds.getRgDataExpedicao())));
		sigma.getDadosPessoaisSigma().setPai(ds.getNomePai());
		sigma.getDadosPessoaisSigma().setMae(ds.getNomeMae());

		if(ds.getRgUfOrgaoExpedidor() == null)
		sigma.getDadosPessoaisSigma().setRgUfExpedidor("GO"); /**Insere um Estado padrão quando o do sigma está null*/
	}

	/** Atribui ao endereço residencial as informações pertinentes buscadas pelo web service. */
	public void setarEnderecoResidencial() {
		sigma.getEnderecoResidencial().setLogradouroRes(ds.getResLogradouro());
		sigma.getEnderecoResidencial().setNumero(ds.getResNumero());
		sigma.getEnderecoResidencial().setBairroRes(ds.getResBairro());
		sigma.getEnderecoResidencial().setEmail(ds.getEmail());
		sigma.getEnderecoResidencial().setCidadeRes(ds.getResCidade());
		sigma.getEnderecoResidencial().setUf(ds.getResUf());

	}
	
	/** Atribui ao endereço funcional as informações pertinentes buscadas pelo web service. */
	public void setarEnderecoFuncional() {
		sigma.getEnderecoFuncional().setUnidadeOrigem(ds.getSiglaUnidade());
		sigma.getEnderecoFuncional().setLogradouro(ds.getLogradouro());
		sigma.getEnderecoFuncional().setBairro(ds.getBairro());
		sigma.getEnderecoFuncional().setCidade(ds.getCidade());
		
	}


	public void setarEmArma(){
		arma  = this.armaDAO.buscarPeloCodigo(sigma.getArma().getCodigo());
		/** seta o cpf e o nome do proprietário(Sigma) na Arma */
		if (arma != null){
		arma.setCpfNovoProprietario(sigma.getCpf());
		arma.setNomeNovoProprietario(sigma.getNome());
		arma.setRgProprietario(sigma.getRg());
		
		}else{
			/**exceção difícil de acontecer pois sempre vai buscar por arma existente, prevenção por erro sistemico.*/
			FacesUtil.addErrorMessage("Dados da arma estão nulos, verifique a arma ou contate o Administrador.");
			limpar();
		}
	}
	
	/** métodos exclusivos para seleção da arma */
	public void armaSelecionada(SelectEvent evento){
		Arma arma = (Arma) evento.getObject();
		sigma.setArma(arma);
		if (sigma.getCodigo() == null){
		verificaArmaExistenteNoSigma();
		}

	}

	@NotNull
	public String getNumeroArmaDoBean(){

		return sigma.getArma() == null ? null : sigma.getArma().getNumeroArma();
	}
	
	public void setNumeroArmaDoBean(String arma){
		camposReadyOnly = false;
	}
	//
	
	 public void verificaArmaExistenteNoSigma(){

			sigmaSelecionado =  this.sigmaDAO.buscarArmaSelecionada(sigma.getArma().getNumeroArma());
		     if(sigmaSelecionado != null){ //aqui ele recebe o sigma caso tenha sido encontrado pelo numero da arma.
			    this.zeraArma = true;
			    this.erroAoSalvarSigma = true;
	         }else{
	        	 this.zeraArma = false;
				 this.erroAoSalvarSigma = false;
	         }

		}
		
	 public void zeraCampo(){
			
			FacesContext facesContext = FacesContext.getCurrentInstance();
			UIViewRoot uiViewRoot = facesContext.getViewRoot();
			
			if (zeraArma){
			//HtmlSelectOneMenu inputSelect = (HtmlSelectOneMenu) uiViewRoot.findComponent("frmSigma:arma");
			HtmlInputText inputText = (HtmlInputText) uiViewRoot.findComponent("frmSigma:arma");
			inputText.setSubmittedValue("");
			inputText.setOnfocus("frmSigma:arma");
			}
		 	       
		}

	// VERIFICAR COMO CAPTURAR E TRATAR SqlExceptionHelper (unique=true)
	public void salvar() throws NegocioException {
		if (sigma.getCodigo() == null){
		   verificaArmaExistenteNoSigma();
		   
		}
        try{
			setarEmArma();
			if(erroAoSalvarSigma == false){
			  			
			   cadastroSigmaService.salvar(sigma);
			   cadastroSigmaService.salvarProprietarioArma(arma);
			
			   FacesUtil.addSuccessMessage("Cadastro de Sigma salvo com sucesso!");
			   limpar();
			}else{
				FacesUtil.addErrorMessage("Atenção: Arma " + sigmaSelecionado.getArma().getNumeroArma() + " já atribuída ao sigma de código " +sigmaSelecionado.getCodigo());
				zeraCampo();
			}
			
			}catch (Exception e) {
            FacesUtil.addErrorMessage("Erro, Sigma não incluído. Verifique se a Refaça o processo ou contate o Administrador");
            	erroAoSalvarSigma = true;
            log.error(e);

           }

		}

    public void edicao(String param) throws IOException {
         Sigma resultado = this.sigmaDAO.buscarPeloCodigo(Long.valueOf(param));

	     camposReadyOnly = false;
         setSigma(resultado);

    }
	
	public void limpar() {
		sigma = new Sigma();
		dp = new DadosPessoais();
		arma = new Arma();
		
	}
	
	public void liberaNumDoc(){
		liberaNumDoc = true;
	}
	
	//getters and setters
	
	public Sigma getSigma() {
		if (sigma == null){
	    	sigma = new Sigma();
	    	
	     }
		
		return sigma;
	}

	public void setSigma(Sigma sigma) {
		this.sigma = sigma;
	}

	public DadosSicad getDs() {
		return ds;
	}

	public void setDs(DadosSicad ds) {
		this.ds = ds;
	}

	public DadosPessoais getDp() {
		return dp;
	}

	public void setDp(DadosPessoais dp) {
		this.dp = dp;
	}

	public List<Arma> getArmas() {
		return armas;
	}

	public void setArmas(List<Arma> armas) {
		this.armas = armas;
	}

	public Arma getArma() {
		return arma;
	}

	public void setArma(Arma arma) {
		this.arma = arma;
	}

	public boolean isZeraCpf() {
		return zeraArma;
	}

	public void setZeraCpf(boolean zeraCpf) {
		this.zeraArma = zeraCpf;
	}

	public Sigma getSigmaSelecionado() {
		return sigmaSelecionado;
	}

	public void setSigmaSelecionado(Sigma sigmaSelecionado) {
		this.sigmaSelecionado = sigmaSelecionado;
	}

	public Boolean getCamposReadyOnly() {
		return camposReadyOnly;
	}

	public void setCamposReadyOnly(Boolean camposReadyOnly) {
		this.camposReadyOnly = camposReadyOnly;
	}

	public boolean isErroAoSalvarSigma() {
		return erroAoSalvarSigma;
	}

	public void setErroAoSalvarSigma(boolean erroAoSalvarSigma) {
		this.erroAoSalvarSigma = erroAoSalvarSigma;
	}

	public boolean isZeraArma() {
		return zeraArma;
	}

	public void setZeraArma(boolean zeraArma) {
		this.zeraArma = zeraArma;
	}

	public CadastroSigmaService getCadastroSigmaService() {
		return cadastroSigmaService;
	}

	public void setCadastroSigmaService(CadastroSigmaService cadastroSigmaService) {
		this.cadastroSigmaService = cadastroSigmaService;
	}

	public ArmaDAO getArmaDAO() {
		return armaDAO;
	}

	public void setArmaDAO(ArmaDAO armaDAO) {
		this.armaDAO = armaDAO;
	}


	public void setSigmaDAO(SigmaDAO sigmaDAO) {
		this.sigmaDAO = sigmaDAO;
	}

	public ConsumirServicoSicad getConsumirServicoSicad() {
		return consumirServicoSicad;
	}

	public void setConsumirServicoSicad(ConsumirServicoSicad consumirServicoSicad) {
		this.consumirServicoSicad = consumirServicoSicad;
	}

	public void setManipulaData(ManipulaData manipulaData) {
		this.manipulaData = manipulaData;
	}

	public List<TipoDoc> getTiposDoc() {
		return tiposDoc;
	}

	public void setTiposDoc(List<TipoDoc> tiposDoc) {
		this.tiposDoc = tiposDoc;
	}

	public boolean isLiberaNumDoc() {
		return liberaNumDoc;
	}

	public void setLiberaNumDoc(boolean liberaNumDoc) {
		this.liberaNumDoc = liberaNumDoc;
	}
}//fim

/* *************************************************************************************************************************************
 * EXEMPLOS:

 
 DO IMPUT DA ARMA: <p:ajax event="change" update="@form, frmSigma" listener="#{cadastroSigmaBean.verificaArmaExistenteNoSigma()}" process="@this, arma" />
	
  
  
  */
