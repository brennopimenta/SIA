package br.gov.go.pm.controller;

import br.gov.go.pm.dao.ArmaDAO;
import br.gov.go.pm.dao.MarcaArmaDAO;
import br.gov.go.pm.dao.PaisesDAO;
import br.gov.go.pm.enuns.*;
import br.gov.go.pm.modelo.*;
import br.gov.go.pm.service.CadastroArmaService;
import br.gov.go.pm.util.AppTools;
import br.gov.go.pm.util.exception.NegocioException;
import br.gov.go.pm.util.jsf.FacesUtil;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ManagedBean
@ViewScoped
public class CadastroArmaBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private Logger log = Logger.getLogger(getClass());

	private Arma arma;
	private Paises pais;
	private MarcaArma marca;
	private List<String> armaSelecionada;
	private List<ArmaModelo> modelos;
	private boolean desabilitar = true;
	private boolean desabilitarRaias = true;

	private List<TipoAlma> tipoAlmas;
	private List<Acabamento> acabamentos;
	private List<Funcionamento> funcionamentos;
	private List<EspecieArma> especies;
	private List<Calibre> calibres;
	private List<SentidoRaia> sentidoRaias;
	private List<UnidadeDeMedida> unMedidas;
	private List<UsoArma> usos;
	private List<Status> statusRestritivos;
	private List<MarcaArma> marcasDeArma = new ArrayList<>();
	private List<Paises> paises = new ArrayList<>();
	private UploadedFile uploadedFile;
	private InputStream in;

	private StreamedContent img;


	@ManagedProperty(value = "#{cadastroArmaService}")
	private CadastroArmaService cadastroArmaService;

	@ManagedProperty(value = "#{marcaArmaDAO}")
	private MarcaArmaDAO marcaArmaDAO;

	@ManagedProperty(value = "#{armaDAO}")
	private ArmaDAO armaDAO;

	@ManagedProperty(value = "#{paisesDAO}")
	private PaisesDAO paisesDAO;

	@ManagedProperty(value = "#{request}")
	private HttpServletRequest request;


	 public void preRender(){
	 	try {
			String param = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("arma");
			if (param != null) {
				edicao(param);
			}
		}catch (Exception e ){
	 		log.error(e.getMessage(), e);
		}
	}

	@PostConstruct
	public void inicializar() {
		limpar();
        preencherListas();
    }

	public void preencherListas(){
        tipoAlmas = Arrays.asList(TipoAlma.values());
        acabamentos = Arrays.asList(Acabamento.values());
        funcionamentos = Arrays.asList(Funcionamento.values());
        especies = Arrays.asList(EspecieArma.values());
        sentidoRaias = Arrays.asList(SentidoRaia.values());
        unMedidas = Arrays.asList(UnidadeDeMedida.values());
        usos = Arrays.asList(UsoArma.values());
        statusRestritivos = Arrays.asList(Status.values());

        calibres = cadastroArmaService.buscarCalibres();
        marcasDeArma = marcaArmaDAO.buscarTodos();
        paises = paisesDAO.buscarTodos();
    }

	public void buscarModelosPorFabricante(){
		modelos = cadastroArmaService.buscarModeloPorFabricante(arma.getMarca().getCodigo());
	}

	/**preenche itens do modelo como referencia e a foto deste modelo.*/
	public void preencheItensModelo(){
	 	try {
			ArmaModelo modelo = arma.getModelo();
			arma.setNumeroDeCanos(modelo.getNumeroDeCanos());
			arma.setCapacidade(modelo.getCapacidade());
			arma.setComprimentoCano(modelo.getComprimentoCano());
			arma.getEnunsArmas().setUnMedComprimentoDoCano(modelo.getUnMedComprimentoDoCano());
			arma.setPais(modelo.getPais());
			arma.setMarca(modelo.getMarca());
			arma.getEnunsArmas().setEspecie(modelo.getEspecie());
			arma.getEnunsArmas().setUso(modelo.getCalibre().getGrupoCalibre().getGrauRestricao());
			arma.getEnunsArmas().setAcabamento(modelo.getAcabamento());
			arma.setCalibre(modelo.getCalibre());

			if (modelo.getFoto() !=null) {
				String nomeArquivo = "ArmaMod".concat(String.valueOf(modelo.getCodigo()));
					buscaFotoArma(nomeArquivo, modelo.getFoto());
			}

	 	}catch (Exception e){
			FacesUtil.addErrorMessage("Erro ao preencher itens do modelo de arma. " + e.getMessage());
		}

	 }

	/**Metodo localizarArmaExistente não permite que, no Cadastro de Arma.xhtml, seja seja duplicado numero de arma. Quando usuário sair do campo ele acusará e limpará
	 * o formulário.*/
	public void localizarArmaExistente() {
		armaSelecionada =  armaDAO.buscarArmaPeloNumero(arma.getNumeroArma());
        if(!armaSelecionada.isEmpty()){
		    FacesUtil.addErrorMessage("Arma " + armaSelecionada +" já está Cadastrada");
		    limpar();
		    desabilitar = true;
         } else {
        	desabilitar = false;
		}
	}

	public void salvar() {
		try {
			/**resolve problema do banco de dados que salva zero no bd*/
			if (arma.getCpfNovoProprietario() == null)
				arma.setCpfNovoProprietario("0");

			cadastroArmaService.salvar(arma);
			limpar();
			FacesUtil.addSuccessMessage("Arma salva com sucesso!");

		} catch (NegocioException e) {
			FacesUtil.addErrorMessage(e.getMessage());
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro, verifique, refaça o processo. Caso persista o erro contatar o administrador");

		}

	}

	public void edicao(String param){
		try {

			Arma resultado = armaDAO.buscarPeloCodigo(Long.valueOf(param));
			desabilitar = false;
			if (resultado.getModelo().getCodigo() != null) {
				String nomeArquivo = "ArmaMod".concat(String.valueOf(resultado.getModelo().getCodigo()));
				buscaFotoArma(nomeArquivo, resultado.getModelo().getFoto());
			}

			modelos.add(resultado.getModelo());
			setArma(resultado);

		}catch (Exception e){
			FacesUtil.addErrorMessage("Erro ao editar a arma. " + e.getMessage());
		}

	}

	public void limpar() {
		arma = new Arma();
		modelos = new ArrayList<>();

	}

	public void habilitarRelacionadoATipoAlma(){

		if (arma.getEnunsArmas().getTipoAlma().equals(TipoAlma.RAIADA)){
			desabilitarRaias = false;
			arma.setQtdDeRaias(null);
			arma.getEnunsArmas().setSentidoRaia(null);

		}else if(arma.getEnunsArmas().getTipoAlma().equals(TipoAlma.LISA)){
			desabilitarRaias = true;
			arma.setQtdDeRaias(0);
			arma.getEnunsArmas().setSentidoRaia(null);
		}
	}

		/** criar arquivo com a foto da arma em pasta temporária para ser listado
	 * @param foto*/
	public void buscaFotoArma(String nomeArquivo, byte[] foto) {
		AppTools.geraFotoArma(nomeArquivo, foto);
	}

	//getters and setters

	public Arma getArma() {
		if (arma == null){
			arma = new Arma();
		}
		return arma;
	}

	public void setArma(Arma arma) {
		this.arma = arma;
	}

	public Paises getPais() {
		return pais;
	}

	public void setPais(Paises pais) {
		this.pais = pais;
	}

	public MarcaArma getMarca() {
		return marca;
	}

	public void setMarca(MarcaArma marca) {
		this.marca = marca;
	}

	public List<String> getArmaSelecionada() {
		return armaSelecionada;
	}

	public void setArmaSelecionada(List<String> armaSelecionada) {
		this.armaSelecionada = armaSelecionada;
	}

	public boolean isDesabilitar() {
		return desabilitar;
	}

	public void setDesabilitar(boolean desabilitar) {
		this.desabilitar = desabilitar;
	}

	public boolean isDesabilitarRaias() {
		return desabilitarRaias;
	}

	public void setDesabilitarRaias(boolean desabilitarRaias) {
		this.desabilitarRaias = desabilitarRaias;
	}

    public List<Calibre> getCalibres() {
        return calibres;
    }

    public void setCalibres(List<Calibre> calibres) {
        this.calibres = calibres;
    }

    public List<TipoAlma> getTipoAlmas() {
		return tipoAlmas;
	}

	public void setTipoAlmas(List<TipoAlma> tipoAlmas) {
		this.tipoAlmas = tipoAlmas;
	}

	public List<Acabamento> getAcabamentos() {
		return acabamentos;
	}

	public void setAcabamentos(List<Acabamento> acabamentos) {
		this.acabamentos = acabamentos;
	}

	public List<Funcionamento> getFuncionamentos() {
		return funcionamentos;
	}

	public void setFuncionamentos(List<Funcionamento> funcionamentos) {
		this.funcionamentos = funcionamentos;
	}

	public List<EspecieArma> getEspecies() {
		return especies;
	}

	public void setEspecies(List<EspecieArma> especies) {
		this.especies = especies;
	}


    public List<SentidoRaia> getSentidoRaias() {
		return sentidoRaias;
	}

	public void setSentidoRaias(List<SentidoRaia> sentidoRaias) {
		this.sentidoRaias = sentidoRaias;
	}

	public List<UnidadeDeMedida> getUnMedidas() {
		return unMedidas;
	}

	public void setUnMedidas(List<UnidadeDeMedida> unMedidas) {
		this.unMedidas = unMedidas;
	}

	public List<UsoArma> getUsos() {
		return usos;
	}

	public void setUsos(List<UsoArma> usos) {
		this.usos = usos;
	}

	public List<Status> getStatusRestritivos() {
		return statusRestritivos;
	}

	public void setStatusRestritivos(List<Status> statusRestritivos) {
		this.statusRestritivos = statusRestritivos;
	}

	public List<MarcaArma> getMarcasDeArma() {
		return marcasDeArma;
	}

	public void setMarcasDeArma(List<MarcaArma> marcasDeArma) {
		this.marcasDeArma = marcasDeArma;
	}

	public List<Paises> getPaises() {
		return paises;
	}

	public void setPaises(List<Paises> paises) {
		this.paises = paises;
	}

	public UploadedFile getUploadedFile() {
		return uploadedFile;
	}

	public void setUploadedFile(UploadedFile uploadedFile) {
		this.uploadedFile = uploadedFile;
	}

	public CadastroArmaService getCadastroArmaService() {
		return cadastroArmaService;
	}

	public void setCadastroArmaService(CadastroArmaService cadastroArmaService) {
		this.cadastroArmaService = cadastroArmaService;
	}

	public MarcaArmaDAO getMarcaArmaDAO() {
		return marcaArmaDAO;
	}

	public void setMarcaArmaDAO(MarcaArmaDAO marcaArmaDAO) {
		this.marcaArmaDAO = marcaArmaDAO;
	}

	public ArmaDAO getArmaDAO() {
		return armaDAO;
	}

	public void setArmaDAO(ArmaDAO armaDAO) {
		this.armaDAO = armaDAO;
	}

	public PaisesDAO getPaisesDAO() {
		return paisesDAO;
	}

	public void setPaisesDAO(PaisesDAO paisesDAO) {
		this.paisesDAO = paisesDAO;
	}

    public StreamedContent getImg() {
        return img;
    }

    public void setImg(StreamedContent img) {
        this.img = img;
    }

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public List<ArmaModelo> getModelos() {
		return modelos;
	}

	public void setModelos(List<ArmaModelo> modelos) {
		this.modelos = modelos;
	}
}


//RequestContext.getCurrentInstance().execute("limpaCampoSentidoRaia();");