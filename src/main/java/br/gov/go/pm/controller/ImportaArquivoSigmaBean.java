package br.gov.go.pm.controller;

import br.gov.go.pm.modelo.ArmaNumeroSigma;
import br.gov.go.pm.modelo.Usuario;
import br.gov.go.pm.security.SegurancaDetalhe;
import br.gov.go.pm.service.general.AtualizaPaginaService;
import br.gov.go.pm.service.ImportaArquivoSigmaService;
import br.gov.go.pm.util.jsf.FacesUtil;
import org.apache.commons.io.IOUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@ViewScoped
public class ImportaArquivoSigmaBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private UploadedFile uploadedFile;
    private InputStream in;
    private ArmaNumeroSigma numeroSigmaExercito;
    private Boolean cancelar = false;
    private Integer barraProgresso;
    private boolean visivel = false;

    @ManagedProperty(value = "#{atualizaPaginaService}")
    private AtualizaPaginaService atualizaPaginaService;

    @ManagedProperty(value = "#{segurancaDetalhe}")
    private SegurancaDetalhe segurancaDetalhe;

    @ManagedProperty(value = "#{importaArquivoSigmaService}")
    private ImportaArquivoSigmaService importaArquivoSigmaService;


    public void upload(FileUploadEvent event) {
        try {
            in = event.getFile().getInputstream();

            if(in != null) {
                FacesUtil.addMsgAnexo(event);
            }

        }catch (Exception e){

        }
    }

    public void salvar () {
        habilitaVisivel();

        try {

            if (in != null) {

               /**faz uma busca no arquivo e reparte nas colunas na seguinte ordem:
                * 1 - Sigma, 2 - Arma */
               List<String> arquivo = IOUtils.readLines(in, "UTF-8");

               List<ArmaNumeroSigma> lista = new ArrayList<>();
               Usuario user  = segurancaDetalhe.getUsuarioPadrao();

               arquivo.stream().forEach(t -> { final String[] partes = t.replaceAll(";", ",").replaceAll("\"", "").split(",");
                        ArmaNumeroSigma armaNumeroSigma = new ArmaNumeroSigma();
                        armaNumeroSigma.setNumeroSigma(partes[0].toUpperCase());
                        armaNumeroSigma.setNumeroArma(partes[1].toUpperCase());
                        armaNumeroSigma.setUsuarioSistema(user);
                        lista.add(armaNumeroSigma);
                });

                importaArquivoSigmaService. salvar(lista);

            }else{
                limpar();
                FacesUtil.addErrorMessage("Por favor insira o arquivo primeiro!");
            }

        } catch (Exception e) {
            FacesUtil.addErrorMessage("Erro na leitura do arquivo externo de Sigma " + e.getMessage());
        }
    }

    public void cancel() {
        limpar();
    }

    public void onComplete() {
        FacesUtil.addSuccessMessage("Processo concluído!");
        limpar();
    }

    public void limpar(){
        barraProgresso = null;
        cancelar = true;
        in = null;
        visivel = false;
        atualizaPaginaService.AtualizaPagina();

    }

    public void habilitaVisivel(){
        visivel = true;
    }

    public void habilitarBtnProcesar(){
        RequestContext.getCurrentInstance().execute("habilitarBtnProcessar();");
    }

    public Integer getBarraProgresso() {
        if(barraProgresso == null) barraProgresso = 0;
        else {
            barraProgresso = barraProgresso + (int)(Math.random() * 35);


            if(barraProgresso > 100)
                barraProgresso = 100;
        }

        return barraProgresso;
    }

    //Getters e Setters

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

    public ArmaNumeroSigma getNumeroSigmaExercito() {
        return numeroSigmaExercito;
    }

    public void setNumeroSigmaExercito(ArmaNumeroSigma numeroSigmaExercito) {
        this.numeroSigmaExercito = numeroSigmaExercito;
    }

    public Boolean getCancelar() {
        return cancelar;
    }

    public void setCancelar(Boolean cancelar) {
        this.cancelar = cancelar;
    }

    public void setBarraProgresso(Integer barraProgresso) {
        this.barraProgresso = barraProgresso;
    }

    public boolean isVisivel() {
        return visivel;
    }

    public void setVisivel(boolean visivel) {
        this.visivel = visivel;
    }

    public void setAtualizaPaginaService(AtualizaPaginaService atualizaPaginaService) {
        this.atualizaPaginaService = atualizaPaginaService;
    }

    public SegurancaDetalhe getSegurancaDetalhe() {
        return segurancaDetalhe;
    }

    public void setSegurancaDetalhe(SegurancaDetalhe segurancaDetalhe) {
        this.segurancaDetalhe = segurancaDetalhe;
    }

    public void setImportaArquivoSigmaService(ImportaArquivoSigmaService importaArquivoSigmaService) {
        this.importaArquivoSigmaService = importaArquivoSigmaService;
    }
}
