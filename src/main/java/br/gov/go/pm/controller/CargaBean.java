package br.gov.go.pm.controller;

import br.gov.go.pm.enuns.DadosFixos;
import br.gov.go.pm.enuns.StatusEmissao;
import br.gov.go.pm.modelo.Arma;
import br.gov.go.pm.modelo.Carga;
import br.gov.go.pm.modelo.Sigma;
import br.gov.go.pm.service.CargaService;
import br.gov.go.pm.service.general.AtualizaPaginaService;
import br.gov.go.pm.service.general.GeradorDeArquivos;
import br.gov.go.pm.util.FormataData;
import br.gov.go.pm.util.exception.NegocioException;
import br.gov.go.pm.util.jsf.FacesUtil;
import org.apache.log4j.Logger;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@ViewScoped
public class CargaBean implements Serializable{

    private static final long serialVersionUID = 1L;

    private Logger log = Logger.getLogger(getClass());

    /** Atributo para download*/
    private StreamedContent file;
    private UploadedFile uploadedFile;
    private InputStream in;
    private List<Carga> cargas = new ArrayList<>();
    private List<Arma> listaArmas = new ArrayList<>();
    private List<Sigma> sigmas = new ArrayList<>();
    private List<String>list = new ArrayList<>();
    private String busca;
    private Carga carga;
    private Carga cargaSelecionada;
    private Boolean habilitaCarga = false;
    private String strCarga;

    @ManagedProperty(value = "#{cargaService}")
    private CargaService cargaService;

    @ManagedProperty(value = "#{atualizaPaginaService}")
    private AtualizaPaginaService atualizaPaginaService;

    @ManagedProperty(value = "#{geradorDeArquivos}")
    private GeradorDeArquivos geradorDeArquivos;

    @PostConstruct
    public void init(){
        limpar();
        buscarTodos();
    }

    /**monta a carga corretamente durante a busca, ou seja, com 70 armas
     * e as outras regras de negócio, entregando pronto para salvar**/
    public void montaCaga() {
        try {
            /**busca que dará origem ao processo, buscando armas sem sigma e que não foram geradas a carga.*/
            sigmas = cargaService.buscaSigmasComArmasSemNumeroSigma();
            if (!sigmas.isEmpty()) {
                cargaService.gerarListaArma(sigmas);
                habilitaCarga = true;
            }
        }catch (Exception e){
            FacesUtil.addErrorMessage("Erro: " + e.getMessage() + ". Verifique sua conexão e refaça o processo, caso persista contate o Administrador!");
        }
    }

    public void buscarTodos(){
        cargas = cargaService.buscarTodas();
    }

    /**gera a lista de armas a ser visualizada em pdf*/
    public void gerarListaArmasVisualizacao() {
        if(cargaSelecionada != null) {
            cargaService.processoVisualizaArmas(cargaSelecionada);
        }else {
                FacesUtil.addErrorMessage("Erro: Não há carga selecionada para busca por favor verifique o gerenciador de carga, a conexão, ou contate o administrador!");
        }
    }

    

    /**gera lista de carga para ser visualizada e baixada*/
    public String gerarListaCarga() {
        Carga cargaResult = cargaService.buscarCargaComArmas(cargaSelecionada.getCodigo());
        String cargaView = null;

        if (cargaResult != null){

        try {
            if (!cargaResult.getSigmas().isEmpty()) {
                cargaView = cargaService.gerarListaCarga(cargaResult);
                carga = cargaResult;
                strCarga = cargaView;

            } else {
                FacesUtil.addErrorMessage("Não existem dados para gerar a lista de carga. Favor verifique.");
            }

        } catch (Exception e) {
            FacesUtil.addErrorMessage(e.getMessage());
        }
        }else{
            FacesUtil.addErrorMessage("Não há carga disponível para visualização!");
        }
        return cargaView;
    }

    public void visualizarCarga() {
        String cargaView = gerarListaCarga();
        String titulo = "Carga " + carga.getCodigo() + " - gerada dia " + FormataData.dataAbreviadaAnoCompletoComHoras(carga.getDataCriacao());
        if (cargaView !=null)
        visualizarLista(cargaView, titulo, StatusEmissao.PAISAGEM, "inline");
        limpar();
    }

    /**visualiza as listas genericamentE, ou seja, nas diversas formatações*/
    public void visualizarLista(String str, String titulo, StatusEmissao ORIENTACAO, String down) {
        cargaService.visualizarLista(str, titulo, ORIENTACAO, down);
    }

    public void enviarCarga(){
        try {
            cargaService.salvarStatusEnviada(cargaSelecionada);
            FacesUtil.addSuccessMessage("Carga enviada com sucesso!");

            baixarArquivo();

        } catch (IOException e) {
            FacesUtil.addErrorMessage(e.getMessage());
        } catch (ClassNotFoundException ce) {
            FacesUtil.addErrorMessage(ce.getMessage());
        } catch (NegocioException e) {
            FacesUtil.addErrorMessage("Erro ao enviar carga: " + e.getMessage());
        }
    }

    public void baixarArquivo() throws IOException, ClassNotFoundException {

        String nomeArquivo = "PMGO-" + DadosFixos.EXERCITO.getDescricao();
        String cargaView = gerarListaCarga();
        file = cargaService.processoBaixarArquivoCarga(cargaSelecionada, cargaView, nomeArquivo);

        /**zera a lista das armas*/
        listaArmas = new ArrayList<>();

      }

    public StreamedContent getFile() {
        return file;
    }

    public void buscaCargaPelaArma(){
        try {
            cargas = cargaService.listarCargaPelaArma(busca);
          if (busca.equals(""))
            buscarTodos();
        } catch (NegocioException e) {
           FacesUtil.addErrorMessage("Erro na busca da carga pela Arma");
        }
    }

    /*Métodos genéricos para visualização das listas em pdf*/

    /**gerador de listas*/

   public void limpar(){
        strCarga = null;
        cargaSelecionada = new Carga();
        carga = new Carga();
        sigmas = new ArrayList<>();
        listaArmas = new ArrayList<>();
        list = new ArrayList<>();
        file = null;
        busca = "";
     }

   public void salvar() {
       try {
            cargaService.salvar(carga, sigmas);
            FacesUtil.addSuccessMessage("Carga gerada com sucesso!");
            limpar();
            atualizaPaginaService.AtualizaPagina();
        } catch (NegocioException ne) {
           FacesUtil.addErrorMessage("Ops! Erro ao gerar a carga: " + ne.getMessage());
        } catch (Exception e) {
           FacesUtil.addErrorMessage("Erro " + e.getMessage() + " . Favor verifique ou contate o Administrador");
        }
    }

    //Getters and Setters

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

    public void setGeradorDeArquivos(GeradorDeArquivos geradorDeArquivos) {
        this.geradorDeArquivos = geradorDeArquivos;
    }

    public void setCargaService(CargaService cargaService) {
        this.cargaService = cargaService;
    }

    public void setAtualizaPaginaService(AtualizaPaginaService atualizaPaginaService) {
        this.atualizaPaginaService = atualizaPaginaService;
    }

    public List<Carga> getCargas() {
        return cargas;
    }

    public void setCargas(List<Carga> cargas) {
        this.cargas = cargas;
    }

    public void setFile(StreamedContent file) {
        this.file = file;
    }

    public Carga getCarga() {
        return carga;
    }

    public void setCarga(Carga carga) {
        this.carga = carga;
    }

    public Logger getLog() {
        return log;
    }

    public List<Sigma> getSigmas() {
        return sigmas;
    }

    public void setSigmas(List<Sigma> sigmas) {
        this.sigmas = sigmas;
    }

    public List<Arma> getListaArmas() {
        return listaArmas;
    }

    public void setListaArmas(List<Arma> listaArmas) {
        this.listaArmas = listaArmas;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public Boolean getHabilitaCarga() {
        return habilitaCarga;
    }

    public void setHabilitaCarga(Boolean habilitaCarga) {
        this.habilitaCarga = habilitaCarga;
    }

    public Carga getCargaSelecionada() {
        return cargaSelecionada;
    }

    public void setCargaSelecionada(Carga cargaSelecionada) {
        this.cargaSelecionada = cargaSelecionada;
    }

    public String getStrCarga() {
        return strCarga;
    }

    public void setStrCarga(String strCarga) {
        this.strCarga = strCarga;
    }

    public String getBusca() {
        return busca;
    }

    public void setBusca(String busca) {
        this.busca = busca;
    }


}//fim
