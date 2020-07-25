package br.gov.go.pm.controller;

import br.gov.go.pm.enuns.StatusEmissao;
import br.gov.go.pm.modelo.Arma;
import br.gov.go.pm.modelo.Carga;
import br.gov.go.pm.modelo.Sigma;
import br.gov.go.pm.service.CargaService;
import br.gov.go.pm.service.ItemBoletimService;
import br.gov.go.pm.service.general.AtualizaPaginaService;
import br.gov.go.pm.service.general.GeradorDeArquivos;
import br.gov.go.pm.util.FormataData;
import br.gov.go.pm.util.exception.NegocioException;
import br.gov.go.pm.util.jsf.FacesUtil;
import br.gov.go.pm.utilitarios.ImpressaoResource;
import com.itextpdf.text.DocumentException;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@ViewScoped
public class ItemBoletimBean implements Serializable{

    private static final long serialVersionUID = 1L;

    private Logger log = Logger.getLogger(getClass());

    /** Atributo para download*/
    private StreamedContent file;
    private UploadedFile uploadedFile;
    private InputStream in;
    private List<Carga> cargas;
    private List<Arma> listaArmas;
    private List<Sigma> sigmas;
    private List<String>list;
    private List<Carga>listaCargasSelecionadas;
    private String busca;
    private Carga carga;
    private Carga cargaSelecionada;
    private Boolean habilitaCarga;
    private Boolean habilitarGerar;
    private Boolean desabilitaSelecao;
    private String strCarga;
    private String valueLimpar;

    @ManagedProperty(value = "#{itemBoletimService}")
    private ItemBoletimService itemBoletimService;

    @ManagedProperty(value = "#{atualizaPaginaService}")
    private AtualizaPaginaService atualizaPaginaService;

    @PostConstruct
    public void init(){
        limpar();
        buscarTodasParaEnvioBoletim();
    }

    /**gerador de listas*/

    public void gerarItemCargasEscolhidas () {
       try {

            visualizarItem(itemBoletimService.gerarItemCargasEscolhidas(listaCargasSelecionadas));

            FacesUtil.addSuccessMessage("Itens gerados com sucesso!");
            limpar();
            atualizaPaginaService.AtualizaPagina();
        } catch (NegocioException ne) {
           FacesUtil.addErrorMessage("Ops!" + ne.getMessage());
        } catch (ParseException e) {
           FacesUtil.addErrorMessage("Ops!" + e.getMessage());
       }
    }

    public void gerarItemCarga(){
        String cargaView = gerarItem();
        try {
            visualizarItem(cargaView);
        } catch (ParseException e) {
            FacesUtil.addErrorMessage("Ops!" + e.getMessage());
        }

    }

    public void visualizarItem(String itemGerado) throws ParseException {
        String titulo = "";
        if (itemGerado !=null)
            visualizarLista(itemGerado, titulo, StatusEmissao.PAISAGEM, "inline");
        limpar();
        atualizaPaginaService.AtualizaPagina();
    }


    public void baixarArquivo() {
        String cargaView = gerarItem();
        file = itemBoletimService.processoBaixarArquivoCarga(cargaSelecionada, cargaView, "itemCarga");

    }

    public StreamedContent getFile() {
        return file;
    }

    public void buscarTodasParaEnvioBoletim(){
        limpar();
        cargas = itemBoletimService.buscarTodasParaEnvioBoletim();
        if (cargas.isEmpty())
            habilitarGerar = false;
    }

    public void buscarCargaEnviadaParaBoletim(){
        cargas = itemBoletimService.buscarCargasEnviadasParaBoletim();
        desabilitaSelecao = true;

    }

    /**busca todas as cargas com boletim*/
    public void buscarCargasComBoletim(){
        cargas = itemBoletimService.buscarCargasComBoletim();
    }

    /**gera a lista de armas a ser visualizada em pdf*/
    public void gerarListaArmasVisualizacao() {
        if(cargaSelecionada != null) {
            itemBoletimService.processoVisualizaArmas(cargaSelecionada);
            limpar();
        }else {
            FacesUtil.addErrorMessage("Erro: Não há carga selecionada para busca por favor verifique o gerenciador de carga, a conexão, ou contate o administrador!");
        }
    }

    public void buscaCargaPelaArma(){
        try {
            valueLimpar = "Limpar";
            cargas = itemBoletimService.buscarCargaPelaArma(busca);
            if (busca.equals(""))
                buscarTodasParaEnvioBoletim();
        } catch (NegocioException e) {
            FacesUtil.addErrorMessage("Erro na busca da carga pela Arma");
        }
    }

    /**gera o item de boletim (da carga)*/
    public String gerarItem() {
        String itemGerado = null;
        try {
            if (!cargaSelecionada.getSigmas().isEmpty()) {
                itemGerado = itemBoletimService.gerarListaItem(cargaSelecionada);

            } else {
                throw new NegocioException("Não existem dados para gerar a lista de carga. Favor verifique.");
            }
        } catch (NegocioException e) {
            FacesUtil.addErrorMessage(e.getMessage());
        }
        return itemGerado;
    }

    /*Métodos genéricos para visualização das listas em pdf*/

    /**visualiza as listas genericamentE, ou seja, nas diversas formatações*/
    public void visualizarLista(String str, String titulo, StatusEmissao ORIENTACAO, String down) {
        itemBoletimService.visualizarLista(str, titulo, ORIENTACAO, down);
    }


     public void limpar(){

         uploadedFile = null;
         in = null;
         cargas = new ArrayList<>();
         file = null;
         busca = "";
         strCarga = null;
         valueLimpar = "Atualizar";
         carga = new Carga();
         cargaSelecionada = new Carga();
         listaCargasSelecionadas = new ArrayList<>();
         sigmas = new ArrayList<>();
         listaArmas = new ArrayList<>();
         list = new ArrayList<>();
         desabilitaSelecao = false;
         habilitaCarga = false;
     }

   //Getters and Setters

    public void setFile(StreamedContent file) {
        this.file = file;
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

    public List<Carga> getCargas() {
        return cargas;
    }

    public void setCargas(List<Carga> cargas) {
        this.cargas = cargas;
    }

    public List<Arma> getListaArmas() {
        return listaArmas;
    }

    public void setListaArmas(List<Arma> listaArmas) {
        this.listaArmas = listaArmas;
    }

    public List<Sigma> getSigmas() {
        return sigmas;
    }

    public void setSigmas(List<Sigma> sigmas) {
        this.sigmas = sigmas;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public List<Carga> getListaCargasSelecionadas() {
        return listaCargasSelecionadas;
    }

    public void setListaCargasSelecionadas(List<Carga> listaCargasSelecionadas) {
        this.listaCargasSelecionadas = listaCargasSelecionadas;
    }

    public String getBusca() {
        return busca;
    }

    public void setBusca(String busca) {
        this.busca = busca;
    }

    public Carga getCarga() {
        return carga;
    }

    public void setCarga(Carga carga) {
        this.carga = carga;
    }

    public Carga getCargaSelecionada() {
        return cargaSelecionada;
    }

    public void setCargaSelecionada(Carga cargaSelecionada) {
        this.cargaSelecionada = cargaSelecionada;
    }

    public Boolean getHabilitaCarga() {
        return habilitaCarga;
    }

    public void setHabilitaCarga(Boolean habilitaCarga) {
        this.habilitaCarga = habilitaCarga;
    }

    public String getStrCarga() {
        return strCarga;
    }

    public void setStrCarga(String strCarga) {
        this.strCarga = strCarga;
    }

    public String getValueLimpar() {
        return valueLimpar;
    }

    public void setValueLimpar(String valueLimpar) {
        this.valueLimpar = valueLimpar;
    }

    public Boolean getDesabilitaSelecao() {
        return desabilitaSelecao;
    }

    public void setDesabilitaSelecao(Boolean desabilitaSelecao) {
        this.desabilitaSelecao = desabilitaSelecao;
    }

    public Boolean getHabilitarGerar() {
        return habilitarGerar;
    }

    public void setHabilitarGerar(Boolean habilitarGerar) {
        this.habilitarGerar = habilitarGerar;
    }

    public void setItemBoletimService(ItemBoletimService itemBoletimService) {
        this.itemBoletimService = itemBoletimService;
    }

    public void setAtualizaPaginaService(AtualizaPaginaService atualizaPaginaService) {
        this.atualizaPaginaService = atualizaPaginaService;
    }

}//fim
