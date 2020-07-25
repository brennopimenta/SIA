package br.gov.go.pm.controller;

import br.gov.go.pm.enuns.StatusEmissao;
import br.gov.go.pm.modelo.Arma;
import br.gov.go.pm.modelo.Carga;
import br.gov.go.pm.modelo.Craf;
import br.gov.go.pm.modelo.Sigma;
import br.gov.go.pm.service.CrafParaCargaService;
import br.gov.go.pm.service.general.AtualizaPaginaService;
import br.gov.go.pm.service.general.GeradorDeArquivos;
import br.gov.go.pm.util.exception.NegocioException;
import br.gov.go.pm.util.jsf.FacesUtil;
import br.gov.go.pm.utilitarios.FileResource;
import br.gov.go.pm.utilitarios.ImpressaoResource;
import com.itextpdf.text.DocumentException;
import org.apache.log4j.Logger;
import org.primefaces.event.RowEditEvent;

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
import java.util.Optional;

@ManagedBean
@ViewScoped
public class CrafParaCargaBean implements Serializable{

    private static final long serialVersionUID = 1L;

    private Logger log = Logger.getLogger(getClass());

    /** Atributo para download*/

    private List<Carga> cargas = new ArrayList<>();
    private List<Arma> listaArmas = new ArrayList<>();
    private List<Sigma> sigmas = new ArrayList<>();
    private List<String>list = new ArrayList<>();
    private List<Carga>listaCargasSelecionadas = null;
    private Carga carga;
    private Carga cargaSelecionada;
    private String strCarga;
    private String busca;
    private Craf craf;
    private String numCraf;
    private Boolean desabilitaSelecao;
    private boolean statusEnvioEmail;

    @ManagedProperty(value = "#{crafParaCargaService}")
    private CrafParaCargaService crafParaCargaService;

    @ManagedProperty(value = "#{atualizaPaginaService}")
    private AtualizaPaginaService atualizaPaginaService;

    @ManagedProperty(value = "#{geradorDeArquivos}")
    private GeradorDeArquivos geradorDeArquivos;

    @PostConstruct
    public void init(){
        limpar();
        buscarTodos();
        buscarStatusEnvioDeEmail();
    }

   public void verificarErros() {
        try {
            if (listaCargasSelecionadas.isEmpty()) {
                FacesUtil.addAlertMessage("Por favor selecione alguma carga!");
            }else {

                List<String> lista = new ArrayList<>();
                List<Carga> listaCargasParaCraf = new ArrayList<>();
                listaCargasParaCraf.addAll(listaCargasSelecionadas);

                /**percorre a lista de cargas selecionadas, para cada carga percorre os sigamas e suas armas (um processo sigma para cada arma)
                 * verifica no banco de dados, para cada arma, se existe craf não cancelado para ela*/
                listaCargasSelecionadas.forEach(l -> l.getSigmas().forEach(a -> {
                    if (a.getArma().getEnunsArmas().getStatusRestritivo() == null) {
                        Optional<Craf> crafaux = crafParaCargaService.verficaSeExisteCrafParaArma(a.getArma().getNumeroArma());

                        if (crafaux.isPresent()) {
                            lista.add((char) 13 + "Carga " + l.getCodigo() + " - motivo: " + "existe craf para a Arma " + a.getArma().getNumeroArma());
                            listaCargasParaCraf.remove(l);
                        }
                        /**significa que tem armas com status restritvo, então coloca na lista de erros*/
                    } else {
                        lista.add((char) 13 + "Carga " + l.getCodigo() + " - motivo: " + "Arma " + a.getArma().getNumeroArma() + " está " + a.getArma().getEnunsArmas().getStatusRestritivo().getDescricao());
                    }

                }));

                /**se a lista de erros tem itens, então preenche, formata e imprime a lista para o usuário. NÃO gera craf se qualquer item da carga tiver um impedimento*/
                if (!lista.isEmpty()) {
                    String str = lista.toString();
                    String strFormatado = str.substring(1, str.length() - 1).trim().replaceAll(",", "");

                    String titulo = "Cargas não Geradas e Motivos:";
                    visualizaLista(strFormatado, titulo, StatusEmissao.RETRATO, "attachment");
                }

                if (!listaCargasParaCraf.isEmpty()) {
                    gerarCrafParaCargas(listaCargasParaCraf);
                } else {
                    FacesUtil.addErrorMessage("Nenhuma Carga está com todos os itens corretos para gerar Craf, por favor verifique relatório de erros.");
                }
            }
        }catch (Exception e){
            FacesUtil.addErrorMessage("Erro: " + e.getMessage() + ". Verifique sua conexão e refaça o processo, caso persista contate o Administrador!");
        }
    }

    public void gerarCrafParaCargas(List<Carga> listaCargasParaCraf) {
        List<Craf> crafs = new ArrayList<>();
        try{
        numCraf = crafParaCargaService.buscarUltimoCraf();
        /**percorre a lista de carga e monta a lista de craf*/
        listaCargasParaCraf.forEach(c -> c.getSigmas().forEach(a -> {

            atribuir(c, a, numCraf);
            crafs.add(craf);

            /**gera um novo craf para ser atribuído a cada craf*/
            numCraf = crafParaCargaService.ultimoCraf(numCraf);
            craf = new Craf();
        }));
            /**com a lista dos crafs gera e salva os crafs*/
             crafParaCargaService.gerarCrafs(crafs);

             /*envia email para a lista*/
             if(statusEnvioEmail == true)
             crafParaCargaService.enviarEmail(listaCargasParaCraf);

             crafParaCargaService.salvarDadosEmCarga(listaCargasParaCraf);
             FacesUtil.addSuccessMessage("Crafs gerados com sucesso!");
             atualizaPaginaService.AtualizaPagina();

        } catch (NegocioException ne) {
            FacesUtil.addErrorMessage(ne.getMessage());
        } catch (Exception e) {
            FacesUtil.addErrorMessage(e.getMessage());
      }
   }

    private void atribuir(Carga c, Sigma a, String numCraf) {
        try {
            craf.setArma(a.getArma());
            craf.setRg(a.getArma().getRgProprietario());
            craf.setCpf(a.getArma().getCpfNovoProprietario());
            craf.setNome(a.getArma().getNomeNovoProprietario());
            craf.setPublicacao(c.getBoletimInclusao());

            /**busca o último nº do craf e atribui.*/
            craf.setNumeroCraf(numCraf);

        } catch (Exception e){
            FacesUtil.addErrorMessage("Erro na atribuição de itens para o craf na geração para carga: " + e.getMessage()
                    + ". Verifique sua conexão, refaça o processo ou contate o administrador");
        }
    }

   public void buscarTodos(){
        cargas = crafParaCargaService.buscarTodas();
    }

    public void mostrarCargaComCraf(){
        cargas = crafParaCargaService.buscarCargaComCraf();
        desabilitaSelecao = true;
    }

    /**monta a lista de armas para ser exibida para conferêcia (modal)
     * @param sigmasArmas*/
    public  void gerarListaArmasCraf(List<Sigma> sigmasArmas){

        for (Sigma sigma : sigmasArmas) {
            listaArmas.add(sigma.getArma());
        }
    }

    /**gera a lista de armas a ser visualizada em pdf*/
    public void gerarListaArmasVisualizacao() {
        try {
            if (cargaSelecionada != null) {
                carga = crafParaCargaService.buscarCargaPeloCodigo(cargaSelecionada.getCodigo());

                if (carga != null) {
                    /**lista de armas*/
                    gerarListaArmasCraf(carga.getSigmas());
                    try {
                        if (!carga.getSigmas().isEmpty()) {
                            String strFormatado = crafParaCargaService.gerarListaArmasVisualizacao(listaArmas);
                            String titulo = "Lista de Armas  -  Carga " + carga.getCodigo();
                            visualizaLista(strFormatado, titulo, StatusEmissao.RETRATO, "inline");
                            limpar();
                        } else {
                            FacesUtil.addErrorMessage("Não existem armas na carga. Favor verifique.");
                        }

                    } catch (Exception e) {
                        FacesUtil.addErrorMessage(e.getMessage());
                    }
                } else {
                    FacesUtil.addErrorMessage("Não foi encontrada a carga " + cargaSelecionada.getCodigo());
                }
            } else {
                FacesUtil.addErrorMessage("Erro: Não há carga selecionada para busca por favor verifique o gerenciador de carga, a conexão, ou contate o administrador!");
            }
        }catch (Exception e){
            FacesUtil.addErrorMessage("Erro: " + e.getMessage());
            log.error(e.getMessage());
        }
    }



    public void buscaCargaPelaArma(){
        try {
        cargas = crafParaCargaService.listarCargaParaCrafPelaArma(busca);
          if (busca.equals(""))
            buscarTodos();
        } catch (NegocioException e) {
           FacesUtil.addErrorMessage("Erro na busca da carga pela Arma");
        }
    }

    /*Métodos genéricos para visualização das listas em pdf*/

    /**gerador de listas*/


    /**visualiza as listas genericamentE, ou seja, nas diversas formatações*/
    public void visualizaLista(String str, String titulo, StatusEmissao ORIENTACAO, String down) {
        try{
        ImpressaoResource.geraVisualizacao(str, titulo, ORIENTACAO, down, "crafParaCarga");

        } catch(DocumentException de) {
            FacesUtil.addErrorMessage("Erro na geração do documento: " + de.getMessage());
        } catch(IOException ioe) {
            FacesUtil.addErrorMessage("Erro de IOE por favor verfique ou contate o administrador..: " + ioe.getMessage());
        } catch (Exception ex){
            FacesUtil.addErrorMessage("Erro no processo de geração e impressão do arquivo, verifique conexao e refaça o processo, se persistir contate o Administrador..: " + ex.getMessage());
        }

    }

    public void limpar() {
         strCarga = null;
         cargaSelecionada = new Carga();
         carga = new Carga();
         craf = new Craf();
         sigmas = new ArrayList<>();
         listaArmas = new ArrayList<>();
         list = new ArrayList<>();
         desabilitaSelecao = false;

     }

    public void onRowEdit(RowEditEvent event) {
        try {
            carga = crafParaCargaService.buscarCargaPeloCodigo(((Carga) event.getObject()).getCodigo());
            carga.setBoletimInclusao(((Carga) event.getObject()).getBoletimInclusao().toUpperCase());
            crafParaCargaService.salvarBoletim(carga);

            FacesUtil.addSuccessMessage("Boletim incluso na carga " + ((Carga) event.getObject()).getCodigo() + " com sucesso!");
            atualizaPaginaService.AtualizaPagina();
        }catch (NegocioException ne){
            FacesUtil.addErrorMessage(ne.getMessage());
            atualizaPaginaService.AtualizaPagina();
        }
    }

    public void onRowCancel() {
        FacesUtil.addAlertMessage("Cancelada inclusão do boletim, nada foi alterado!");
        atualizaPaginaService.AtualizaPagina();

    }

    public void buscarStatusEnvioDeEmail(){
        try {
            /*se não existir registro fica false*/
            boolean statusEmail = crafParaCargaService.buscarStatusEnvioEmail();
            setStatusEnvioEmail(statusEmail);
        }catch (Exception e){
            FacesUtil.addAlertMessage("Ops! Houve um erro ao buscar suas configurações para envio de email, portanto não vai conseguir enviar os emails! Erro: " + e.getMessage());
            log.error(e.getMessage(), e.getCause());
        }
    }

    //Getters and Setters

    public void setGeradorDeArquivos(GeradorDeArquivos geradorDeArquivos) {
        this.geradorDeArquivos = geradorDeArquivos;
    }

    public void setAtualizaPaginaService(AtualizaPaginaService atualizaPaginaService) {
        this.atualizaPaginaService = atualizaPaginaService;
    }

    public void setCrafParaCargaService(CrafParaCargaService crafParaCargaService) {
        this.crafParaCargaService = crafParaCargaService;
    }

    public List<Carga> getCargas() {
        return cargas;
    }

    public void setCargas(List<Carga> cargas) {
        this.cargas = cargas;
    }

    public List<Carga> getListaCargasSelecionadas() {
        return listaCargasSelecionadas;
    }

    public void setListaCargasSelecionadas(List<Carga> listaCargasSelecionadas) {
        this.listaCargasSelecionadas = listaCargasSelecionadas;
    }

    public Craf getCraf() {
        return craf;
    }

    public void setCraf(Craf craf) {
        this.craf = craf;
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

    public String getNumCraf() {
        return numCraf;
    }

    public void setNumCraf(String numCraf) {
        this.numCraf = numCraf;
    }

    public Boolean getDesabilitaSelecao() {
        return desabilitaSelecao;
    }

    public void setDesabilitaSelecao(Boolean desabilitaSelecao) {
        this.desabilitaSelecao = desabilitaSelecao;
    }

    public String getBusca() {
        return busca;
    }

    public void setBusca(String busca) {
        this.busca = busca;
    }

    public boolean isStatusEnvioEmail() {
        return statusEnvioEmail;
    }

    public void setStatusEnvioEmail(boolean statusEnvioEmail) {
        this.statusEnvioEmail = statusEnvioEmail;
    }
}//fim




//listaCargasParaCraf.forEach(l -> l.getSigmas().forEach(a -> System.out.println("Carga "+l.getCodigo()+ " " + a.getArma().getNumeroArma())));