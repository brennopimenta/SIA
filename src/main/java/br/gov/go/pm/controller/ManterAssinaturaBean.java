package br.gov.go.pm.controller;

import br.gov.go.pm.enuns.StatusEmissao;
import br.gov.go.pm.modelo.Assinaturas;
import br.gov.go.pm.modelo.Usuario;
import br.gov.go.pm.service.AssinaturaService;
import br.gov.go.pm.service.general.AtualizaPaginaService;
import br.gov.go.pm.service.general.GeradorDeArquivos;
import br.gov.go.pm.util.exception.NegocioException;
import br.gov.go.pm.util.jsf.FacesUtil;
import br.gov.go.pm.utilitarios.FileResource;
import br.gov.go.pm.utilitarios.ImagensEmArquivo;
import br.gov.go.pm.utilitarios.ImpressaoResource;
import com.itextpdf.text.DocumentException;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.servlet.ServletContext;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@ViewScoped
public class ManterAssinaturaBean implements Serializable{

    private static final long serialVersionUID = 1L;

    private Logger log = Logger.getLogger(getClass());

    private UploadedFile uploadedFile;
    private List<Usuario> usuarios = new ArrayList<>();
    private List<Assinaturas> assinaturas = new ArrayList<>();
    private Assinaturas assinatura;
    private Assinaturas assinaturaSelecionada;
    private Boolean editado;
    private InputStream in;

    @ManagedProperty(value = "#{assinaturaService}")
    private AssinaturaService assinaturaService;

    @ManagedProperty(value = "#{atualizaPaginaService}")
    private AtualizaPaginaService atualizaPaginaService;

    @ManagedProperty(value = "#{geradorDeArquivos}")
    private GeradorDeArquivos geradorDeArquivos;

    @PostConstruct
    public void init(){
        limpar();
        assinaturas = assinaturaService.buscarTodas();
        buscaUsuarios();
        listarFotoArmas();
        editado = false;
    }

    /* Baixa listagem de assinaturas*/
    private StreamedContent file;

    /**monta a lista para ser exibida/baixada*/
    public  List<String> gerarLista(){
        List<String>list = new ArrayList<>();
        final String DELIM_INI = "[";
        final String DELIM_FIM = "]";


        for (Assinaturas u : assinaturas) {
            list.add((char)13 + DELIM_INI.concat(String.valueOf(u.getCodigo()).concat(DELIM_FIM)
                    .concat(DELIM_INI).concat(u.getUsuario().getNome()).concat(DELIM_FIM)
                    .concat(DELIM_INI).concat(u.getStatus().getDescricao()).concat(DELIM_FIM)));
            }
        return list;
    }

    public void baixarAssinatura() throws IOException, ClassNotFoundException {

        List<String>list = gerarLista();

        String str = list.toString();
        final InputStream stream = new ByteArrayInputStream(str.substring(1, str.length()-1).trim().replaceAll(",", "").getBytes());
        file = new DefaultStreamedContent(stream, "application/txt", "assinatura.txt");
      }

    public StreamedContent getFile() {
        return file;
    }

    //Master
    public void visualizarLista() {
        List<String>list = gerarLista();

        /**str e list vão para o gerador de arquivo*/
        String str = list.toString();

        try{
            ImpressaoResource.geraVisualizacao(str.substring(1, str.length() - 1).trim().replaceAll(",", ""), "",
                    StatusEmissao.RETRATO, "inline", "assinatura");

        } catch(DocumentException de) {
            FacesUtil.addErrorMessage("Erro na geração do documento: " + de.getMessage());
        } catch(IOException ioe) {
            FacesUtil.addErrorMessage("Erro de IOE por favor verfique ou contate o administrador..: " + ioe.getMessage());
        } catch (Exception ex){
            FacesUtil.addErrorMessage("Erro no processo de geração e impressão do arquivo, verifique conexao e refaça o processo, se persistir contate o Administrador..: " + ex.getMessage());
        }

    }

    public void buscaUsuarios(){
        usuarios = assinaturaService.buscarUsuariosParaAssinatura();
    }

    public void limpar(){
        assinatura = new Assinaturas();
        assinaturaSelecionada = new Assinaturas();
        usuarios = new ArrayList<>();

    }

    public void upload(FileUploadEvent event) {
        try {
            in = event.getFile().getInputstream();

            if(in != null) {
                FacesUtil.addMsgAnexo(event);
            }

        }catch (Exception e){

        }
    }

    public void functionsBeforeSave() {
        /**se foi inserido uma foto, verifica e seta no objeto arma*/
            if (in != null) {

                try {
                    assinatura.setAssinaturaImg(IOUtils.toByteArray(in));
                } catch (IOException e) {
                    FacesUtil.addErrorMessage("Erro antes de salvar assinatura, contate o Administrador.");
                    log.error(e.getMessage(), e.getCause());
                }
            }

    }

    public void salvar() {
        try {
            functionsBeforeSave();

            if (assinaturaSelecionada == null || assinaturaSelecionada.getCodigo() == null)
                assinaturaService.verificaAutoridadeExistente(assinatura);

            assinaturaService.salvar(assinatura);
            FacesUtil.addSuccessMessage("Assinaturas salva com sucesso!");
            limpar();
            atualizaPaginaService.AtualizaPagina();

        } catch (NegocioException e) {
            FacesUtil.addErrorMessage(e.getMessage());
        }
    }

    public void edicao(){

        usuarios.removeAll(usuarios);
        usuarios.add(getAssinaturaSelecionada().getUsuario());
        setAssinatura(getAssinaturaSelecionada());
        editado = true;

    }

    public void excluir() {

        try {
            assinaturaService.excluir(assinaturaSelecionada);
            assinaturas.remove(assinaturaSelecionada);
            FacesUtil.addSuccessMessage("Assinaturas " + assinaturaSelecionada.getCodigo() +" - "+ assinaturaSelecionada.getUsuario().getNome() + " excluída com sucesso");
            limpar();
            atualizaPaginaService.AtualizaPagina();
        } catch (Exception e) {
            FacesUtil.addErrorMessage("Erro na exclusão da assinatura, por favor verifique ou contate o administrador: " + e.getMessage());
        }

    }



    public void inativar() throws Exception{

        assinatura = assinaturaService.buscarPeloCodigo(assinaturaSelecionada.getCodigo());
        assinaturaService.inativar(assinatura);
        FacesUtil.addSuccessMessage("Assinaturas Inativada com sucesso!");
        limpar();
        atualizaPaginaService.AtualizaPagina();

    }

    /** criar arquivo com a foto da assinatura em pasta temporária para ser listado */
    public void listarFotoArmas(){
        try {
            ServletContext sContext = ImagensEmArquivo.folder();

            for (Assinaturas assinatura : assinaturas) {
                String nomeArquivo = String.valueOf(assinatura.getCodigo());
                String arquivo = ImagensEmArquivo.criarArquivoEmPastaTemp(sContext, nomeArquivo);
                if (assinatura.getAssinaturaImg() != null){
                    ImagensEmArquivo.criaArquivo(assinatura.getAssinaturaImg(), arquivo);
                }
            }
        } catch (Exception ex) {
            FacesUtil.addErrorMessage("Problema ao renderizar as imagens da assinatura");
            ex.printStackTrace();
        }


    }

    //Getters and Setters

    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    public Assinaturas getAssinatura() {
        return assinatura;
    }

    public void setAssinatura(Assinaturas assinatura) {
        this.assinatura = assinatura;
    }

    public Assinaturas getAssinaturaSelecionada() {
        return assinaturaSelecionada;
    }

    public void setAssinaturaSelecionada(Assinaturas assinaturaSelecionada) {
        this.assinaturaSelecionada = assinaturaSelecionada;
    }

    public List<Assinaturas> getAssinaturas() {
        return assinaturas;
    }

    public void setAssinaturas(List<Assinaturas> assinaturas) {
        this.assinaturas = assinaturas;
    }

    public void setAssinaturaService(AssinaturaService assinaturaService) {
        this.assinaturaService = assinaturaService;
    }

    public Boolean getEditado() {
        return editado;
    }

    public void setEditado(Boolean editado) {
        this.editado = editado;
    }

    public void setAtualizaPaginaService(AtualizaPaginaService atualizaPaginaService) {
        this.atualizaPaginaService = atualizaPaginaService;
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
}//fim
