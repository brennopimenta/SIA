package br.gov.go.pm.controller;

import br.gov.go.pm.dao.MarcaArmaDAO;
import br.gov.go.pm.dao.PaisesDAO;
import br.gov.go.pm.enuns.*;
import br.gov.go.pm.modelo.ArmaModelo;
import br.gov.go.pm.modelo.Calibre;
import br.gov.go.pm.modelo.MarcaArma;
import br.gov.go.pm.modelo.Paises;
import br.gov.go.pm.service.ModeloArmaService;
import br.gov.go.pm.service.general.AtualizaPaginaService;
import br.gov.go.pm.util.AppTools;
import br.gov.go.pm.util.exception.NegocioException;
import br.gov.go.pm.util.jsf.FacesUtil;
import br.gov.go.pm.utilitarios.MostraDataAtual;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.primefaces.model.UploadedFileWrapper;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
public class ModeloArmaBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private Logger log = Logger.getLogger(getClass());

	private Paises pais;
	private MarcaArma marca;
	private ArmaModelo modeloArma;
	private ArmaModelo modeloArmaSelecionado;
	private String atributoBusca;

	private List<EspecieArma> especies;
	private List<UnidadeDeMedida> unMedidas;
	private List<Acabamento> acabamentos;
	private List<Funcionamento> funcionamentos;
	private List<SentidoRaia> sentidoRaias;

	private List<Calibre> calibres;
	private List<MarcaArma> marcasDeArma;
	private List<Paises> paises;
	private List<ArmaModelo> modelos;
	private UploadedFile uploadedFile;
	private InputStream in;

	private StreamedContent img;

	@ManagedProperty(value = "#{modeloArmaService}")
	private ModeloArmaService modeloArmaService;

	@ManagedProperty(value = "#{marcaArmaDAO}")
	private MarcaArmaDAO marcaArmaDAO;

	@ManagedProperty(value = "#{paisesDAO}")
	private PaisesDAO paisesDAO;

	@ManagedProperty(value = "#{request}")
	private HttpServletRequest request;

	@ManagedProperty(value = "#{atualizaPaginaService}")
	private AtualizaPaginaService atualizaPaginaService;


	@PostConstruct
	public void inicializar() {
		limpar();
		preencherListas();
		buscarTodos();
	}

	public void preencherListas(){
		especies = Arrays.asList(EspecieArma.values()); //
		sentidoRaias = Arrays.asList(SentidoRaia.values());
		unMedidas = Arrays.asList(UnidadeDeMedida.values()); //
		acabamentos = Arrays.asList(Acabamento.values());

		calibres = modeloArmaService.buscarCalibres();
		marcasDeArma = marcaArmaDAO.buscarTodos(); //
		paises = paisesDAO.buscarTodos();  //
	}

	public void buscar() {
		if (atributoBusca.equals("")) {
			buscarTodos();
		}else {
			modelos = modeloArmaService.buscarPorDescricao(atributoBusca);
		}

	}

	public void buscarTodos() {
		modelos = modeloArmaService.buscarTodos();
		buscaFotoArma();

	}

    /**utilizado para mostrar a data atual em um campo data somente leitura */
    public Date getDataDeHoje(){return MostraDataAtual.getDataDeHoje();}

	/**método utilizado para o componente fileupload para passar a foto como array de bytes antes de salvar*/
	public void upload(FileUploadEvent event) {
		try {

			in = event.getFile().getInputstream();
			FacesUtil.addMsgAnexo(event);

		}catch (Exception e){
			FacesUtil.addAlertMessage("Erro inserir a foto no sistema no Cadastro de arma. " + e.getMessage());

		}
	}

	public void functionsBeforeSave(){
		/**se foi inserido uma foto, verifica e seta no objeto arma*/
		if (in != null) {
			try {
				/**se o modelo está preenchido e o status é vazio então seta como ATIVO*/
				if (modeloArma.getStatus() == null)
					modeloArma.setStatus(StatusSituacao.ATIVO);

				modeloArma.setFoto(IOUtils.toByteArray(in));
			}catch (Exception e){
				log.error(e.getMessage());
			}
		}
	}

	public void salvar() {
		try {
			functionsBeforeSave();
			modeloArmaService.salvar(modeloArma);
			limpar();
			FacesUtil.addSuccessMessage("Modelo de Arma salvo com sucesso!");
			atualizaPaginaService.AtualizaPagina();

		} catch (NegocioException ne) {
			FacesUtil.addErrorMessage("Erro " + ne.getMessage() + " .refaça o processo ou contate o administrador!");
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro " + e.getMessage() + " .refaça o processo ou contate o administrador!");

		}

	}

	public void edicao(){
		if (getModeloArmaSelecionado() != null)
		setModeloArma(getModeloArmaSelecionado());
		else
		FacesUtil.addErrorMessage("Não foi possível editar o item selecionado, verifique sua conexão, refaça o processo. Caso persista contate o Administrador.");
	}

	public void inativaEAtiva() {
		try{
			modeloArma = modeloArmaService.buscarPeloCodigo(modeloArmaSelecionado.getCodigo());

			if (modeloArma.getStatus().equals(StatusSituacao.ATIVO)){

				modeloArmaService.inativar(modeloArma);
				limpar();
				FacesUtil.addSuccessMessage("Modelo INATIVADO com sucesso!");
			} else{
				modeloArmaService.ativar(modeloArma);
				limpar();
				FacesUtil.addSuccessMessage("Modelo ATIVADO com sucesso!");
			}
			atualizaPaginaService.AtualizaPagina();

		}catch (NegocioException e){
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	/** criar arquivo com a foto da arma em pasta temporária para ser listado*/
	public void buscaFotoArma() {
		if (!modelos.isEmpty()) {
			for (ArmaModelo modelo : modelos) {
				String nomeArquivo = "ArmaMod".concat(String.valueOf(modelo.getCodigo()));
				byte[] foto = modelo.getFoto();

				if(foto != null)
					AppTools.geraFotoArma(nomeArquivo, foto);
			}
		}
	}

	public void limpar() {
		calibres = new ArrayList<>();
		modeloArma = new ArmaModelo();
		modeloArmaSelecionado = new ArmaModelo();

		especies = new ArrayList<>(); //
		sentidoRaias = new ArrayList<>();
		unMedidas = new ArrayList<>();
		acabamentos = new ArrayList<>();

		uploadedFile = new UploadedFileWrapper();

	}

	//getters and setters

	public void setModeloArmaService(ModeloArmaService modeloArmaService) {
		this.modeloArmaService = modeloArmaService;
	}

	public void setMarcaArmaDAO(MarcaArmaDAO marcaArmaDAO) {
		this.marcaArmaDAO = marcaArmaDAO;
	}


	public void setPaisesDAO(PaisesDAO paisesDAO) {
		this.paisesDAO = paisesDAO;
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

	public ArmaModelo getModeloArmaSelecionado() {
		return modeloArmaSelecionado;
	}

	public void setModeloArmaSelecionado(ArmaModelo modeloArmaSelecionado) {
		this.modeloArmaSelecionado = modeloArmaSelecionado;
	}

	public List<EspecieArma> getEspecies() {
		return especies;
	}

	public void setEspecies(List<EspecieArma> especies) {
		this.especies = especies;
	}

	public List<UnidadeDeMedida> getUnMedidas() {
		return unMedidas;
	}

	public void setUnMedidas(List<UnidadeDeMedida> unMedidas) {
		this.unMedidas = unMedidas;
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

	public List<Calibre> getCalibres() {
		return calibres;
	}

	public void setCalibres(List<Calibre> calibres) {
		this.calibres = calibres;
	}

	public List<SentidoRaia> getSentidoRaias() {
		return sentidoRaias;
	}

	public void setSentidoRaias(List<SentidoRaia> sentidoRaias) {
		this.sentidoRaias = sentidoRaias;
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

	public List<ArmaModelo> getModelos() {
		return modelos;
	}

	public void setModelos(List<ArmaModelo> modelos) {
		this.modelos = modelos;
	}

	public UploadedFile getUploadedFile() {
		return uploadedFile;
	}

	public void setUploadedFile(UploadedFile uploadedFile) {
		this.uploadedFile = uploadedFile;
	}

	public InputStream getIn() {
		return in;
	}

	public void setIn(InputStream in) {
		this.in = in;
	}

	public StreamedContent getImg() {
		return img;
	}

	public void setImg(StreamedContent img) {
		this.img = img;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public ArmaModelo getModeloArma() {
		return modeloArma;
	}

	public void setModeloArma(ArmaModelo modeloArma) {
		this.modeloArma = modeloArma;
	}

	public String getAtributoBusca() {
		return atributoBusca;
	}

	public void setAtributoBusca(String atributoBusca) {
		this.atributoBusca = atributoBusca;
	}

	public void setAtualizaPaginaService(AtualizaPaginaService atualizaPaginaService) {
		this.atualizaPaginaService = atualizaPaginaService;
	}
}
