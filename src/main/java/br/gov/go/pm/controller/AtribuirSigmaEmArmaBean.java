package br.gov.go.pm.controller;

import br.gov.go.pm.dao.ArmaDAO;
import br.gov.go.pm.dao.BaseNumeroSigmaDAO;
import br.gov.go.pm.enuns.StatusCarga;
import br.gov.go.pm.enuns.StatusEmissao;
import br.gov.go.pm.enuns.StatusSigma;
import br.gov.go.pm.modelo.Arma;
import br.gov.go.pm.modelo.ArmaNumeroSigma;
import br.gov.go.pm.service.CadastroArmaService;
import br.gov.go.pm.service.ManterNumeroSigmaService;
import br.gov.go.pm.util.exception.NegocioException;
import br.gov.go.pm.util.jsf.FacesUtil;
import br.gov.go.pm.utilitarios.ImpressaoResource;
import com.itextpdf.text.DocumentException;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

@ManagedBean
@ViewScoped
public class AtribuirSigmaEmArmaBean implements Serializable{

	private static final long serialVersionUID = 1L;
	private Logger log = Logger.getLogger(getClass());

	@ManagedProperty(value = "#{armaDAO}")
	private ArmaDAO armaDAO;

	private Map<String,ArmaNumeroSigma> armaSigmas;

	@ManagedProperty(value = "#{baseNumeroSigmaDAO}")
	private BaseNumeroSigmaDAO baseNumeroSigmaDAO;

	@ManagedProperty(value = "#{cadastroArmaService}")
	private CadastroArmaService cadastroArmaService;

	@ManagedProperty(value = "#{manterNumeroSigmaService}")
	private ManterNumeroSigmaService manterNumeroSigmaService;

	private Arma arma;
	private List<Arma>armas = new ArrayList<>();
	private List<ArmaNumeroSigma> numerosSigma = new ArrayList<>();
	private List<Arma> armasParaComparar = new ArrayList<>();
	private List<String> naoAtribuidos = new ArrayList<>();
	private List<String> atribuidos = new ArrayList<>();
	private Integer progress;
	private Boolean cancelar = false;
	private boolean visivel = false;
	private String mensagem = null;
	private Integer contador = 0;
	private Integer progressEnd = 0;

	@PostConstruct
	public void inicializar(){
		limpar();
	}

	/**
	 * 1.para cada arma percorrida da lista busca se ela existe na base de nª sigma passando o numero da arma
	 * 2.garante que o processo seja feito apenas para novos sigmas(pois os objetos se encarregam da atualização automática),
	 * e verifica se a arma buscada  na base é a mesma da lista de armas.
	 * 3.seta o numero do sigma na arma
	 * 4.salva o objeto numero do Sigma na arma, utilizando uma função já definida para a tal (em cadastroArmaService).
	 * */
	public void atribuir(){
		/**Estabelece condições: tem que ter algo na lista da base numero sigma,
		 * cancelar não foi selecionado, verifica se a arma tem proprietário, arma não tem numero de sigma, o numero da arma do cadastro de arma é igual ao numero da
		 * arma da base numero sigma(o qual vem do exército). Todas essas condições devem ser seguidas para
		 * atribuir em arma o numero de sigma do exército, se alguma falhar não atribui.*/

		/**busca as armas sem sigma cadastradas no banco. Busca
		 * numeros sigmas na base de numeros sigma e atribui em memória*/
		armas = armaDAO.buscarArmasSemSigma();
		armaSigmas = new HashMap<>();
		baseNumeroSigmaDAO.buscarTodos().forEach(s -> {armaSigmas.put(s.getNumeroArma(),s);});

		progressEnd = armas.size();
		ArmaNumeroSigma armaNumerosSigma;
		habilitaVisivel();
		try {

			if(armas == null || armas.isEmpty()) {
				FacesUtil.addErrorMessage("Não existem armas para receberem sigma! Clique em CANCELAR.");
			}else {
				/**percorre a lista das armas sem sigma vindas do banco de dados*/
				for (final Arma arma : armas) {
					contador ++;
					if (cancelar)
						break;
					if (arma.getCpfNovoProprietario() == null || arma.getCpfNovoProprietario().equals("0"))
						armaNumerosSigma = null;
					else
						armaNumerosSigma = armaSigmas.get(arma.getNumeroArma()); /**seta uma variável com o map da baseNumeroSigma(armasSigmas)*/
					/**verifica se o Nª sigma da arma vinda do BD(armas) é igual ao da baseNumeroSigma (armaNumerosSigma) */
					if (armaNumerosSigma != null && arma.getNumeroArma().equals(armaNumerosSigma.getNumeroArma())) {

						/**verifica se o sigma vindo da base numero sigma já existe cadastrado para alguma arma do sistema*/
						Optional<Arma> armaOptional = manterNumeroSigmaService.buscarSigmaExistenteEmArma(armaNumerosSigma.getNumeroSigma());
						if(armaOptional.isPresent()) {
							Arma armaEncontrada = armaOptional.get();
							/**grava na lista de erros*/
							naoAtribuidos.add((char) 13 + "Erro: a arma "+ armaEncontrada.getNumeroArma() + " já possui o sigma " + armaNumerosSigma.getNumeroSigma() +
									".  Arma nº " + armaNumerosSigma.getNumeroArma() + " permanece sem sigma.");

							/**verifica se foi gerada uma carga para a arma (este status é salvo na própria arma quando se gera uma carga: CargaService)*/
						}else if(arma.getEnunsArmas().getStatusCarga() != StatusCarga.GERADA_CARGA){
							naoAtribuidos.add((char) 13 + "Erro: Arma nº " + arma.getNumeroArma() + " não foi atribuída a nenhuma carga");

						}else{
							/**continua o processo*/
							arma.setNumeroSigma(armaNumerosSigma.getNumeroSigma());
							cadastroArmaService.salvarSigmaEmArma(arma); /**salva em Arma*/

							armaNumerosSigma.setStatus(StatusSigma.ATRIBUIDO);
							manterNumeroSigmaService.salvarAtribuidoNaBase(armaNumerosSigma); /**salva em armaNumeroSigma*/
							atribuidos.add((char) 13 + " Arma nº: " + arma.getNumeroArma() + " - Sigma nº: " + arma.getNumeroSigma());

						}
					}

				}// fim do for
			}

		}catch (NegocioException ne){
			log.error(ne.getMessage());
			FacesUtil.addErrorMessage("Sigmas não atribuídos em arma. Motivo: " + ne.getMessage());
		}catch (Exception e){
			log.error(e.getMessage(), e.getCause());
			System.out.println(e.getMessage());
		}
	}//fim método

	public void onComplete() {
		progress = null;
		FacesUtil.addSuccessMessage("Processo concluído com sucesso!");
	}

	public void imprimeErros(){
		if (!naoAtribuidos.isEmpty()) {
			String str = naoAtribuidos.toString();
			String strFormatado = str.substring(1, str.length() - 1).trim().replaceAll(",", "");

			String titulo = "Erros na atribuição de sigma";
			visualizarLista(strFormatado, titulo, StatusEmissao.RETRATO, "inline");
		}else{
			String strFormatado = "Não houve nenhum erro!";
			visualizarLista(strFormatado, null, StatusEmissao.RETRATO, "inline");
		}

	}

	public void imprimeAtribuidos(){
		if (!atribuidos.isEmpty()) {
			String str = atribuidos.toString();
			String strFormatado = str.substring(1, str.length() - 1).trim().replaceAll(",", "");

			String titulo = "Lista de Sigmas atribuídos em Armas - total de " + atribuidos.size() + " arma(s).";
			visualizarLista(strFormatado, titulo, StatusEmissao.RETRATO, "inline");
		}else{
			String strFormatado = "Nenhum sigma atribuído às armas do sistema!";
			visualizarLista(strFormatado, null, StatusEmissao.RETRATO, "inline");
		}
	}

	/**visualiza as listas genericamentE, ou seja, nas diversas formatações*/
	public void visualizarLista(String str, String titulo, StatusEmissao ORIENTACAO, String down) {
		try{
			ImpressaoResource.geraVisualizacao(str, titulo, ORIENTACAO, down,"atribSigma");

		} catch(DocumentException de) {
			FacesUtil.addErrorMessage("Erro na geração do documento: " + de.getMessage());
		} catch(IOException ioe) {
			FacesUtil.addErrorMessage("Erro de IOE por favor verfique ou contate o administrador..: " + ioe.getMessage());
		} catch (Exception ex){
			FacesUtil.addErrorMessage("Erro no processo de geração e impressão do arquivo, verifique conexao e refaça o processo, se persistir contate o Administrador..: " + ex.getMessage());
		}

	}


	public void habilitaVisivel(){
		visivel = true;
	}

	public void cancel() {
		cancelar = true;
		limpar();

	}

	public void limpar(){
		progress = null;
		mensagem = null;
		arma = new Arma();
	}

	public Integer getProgress() {
		if (progress == null) {
			progress = 0;
		} else {
			if (contador > 0) {
				double d =  (double) contador / progressEnd * 100;
				int resultado = (int) Math.ceil(d);
				progress = progress + resultado;


				if (progress == 100)
					progress = 100;
			}
		}
		return progress;
	}



	public void setProgress(Integer progress) {
		this.progress = progress;
	}

	//getters and setters
	public Arma getArma() {
		return arma;
	}


	public void setArma(Arma arma) {
		this.arma = arma;
	}


	public List<ArmaNumeroSigma> getNumerosSigma() {
		return numerosSigma;
	}


	public void setNumerosSigma(List<ArmaNumeroSigma> numerosSigma) {
		this.numerosSigma = numerosSigma;
	}

	public List<Arma> getArmas() {
		return armas;
	}

	public void setArmas(List<Arma> armas) {
		this.armas = armas;
	}

	public Boolean getCancelar() {
		return cancelar;
	}

	public void setCancelar(Boolean cancelar) {
		this.cancelar = cancelar;
	}

	public boolean isVisivel() {
		return visivel;
	}

	public void setVisivel(boolean visivel) {
		this.visivel = visivel;
	}

	public ArmaDAO getArmaDAO() {
		return armaDAO;
	}

	public void setArmaDAO(ArmaDAO armaDAO) {
		this.armaDAO = armaDAO;
	}

	public BaseNumeroSigmaDAO getBaseNumeroSigmaDAO() {
		return baseNumeroSigmaDAO;
	}

	public void setBaseNumeroSigmaDAO(BaseNumeroSigmaDAO baseNumeroSigmaDAO) {
		this.baseNumeroSigmaDAO = baseNumeroSigmaDAO;
	}

	public CadastroArmaService getCadastroArmaService() {
		return cadastroArmaService;
	}

	public void setCadastroArmaService(CadastroArmaService cadastroArmaService) {
		this.cadastroArmaService = cadastroArmaService;
	}

	public void setManterNumeroSigmaService(ManterNumeroSigmaService manterNumeroSigmaService) {
		this.manterNumeroSigmaService = manterNumeroSigmaService;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public List<Arma> getArmasParaComparar() {
		return armasParaComparar;
	}

	public void setArmasParaComparar(List<Arma> armasParaComparar) {
		this.armasParaComparar = armasParaComparar;
	}

	public List<String> getNaoAtribuidos() {
		return naoAtribuidos;
	}

	public void setNaoAtribuidos(List<String> naoAtribuidos) {
		this.naoAtribuidos = naoAtribuidos;
	}

	public List<String> getAtribuidos() {
		return atribuidos;
	}

	public void setAtribuidos(List<String> atribuidos) {
		this.atribuidos = atribuidos;
	}

	public Integer getContador() {
		return contador;
	}

	public void setContador(Integer contador) {
		this.contador = contador;
	}

	public Integer getProgressEnd() {
		return progressEnd;
	}

	public void setProgressEnd(Integer progressEnd) {
		this.progressEnd = progressEnd;
	}
}//fim geral

//return armasParaComparar.stream().filter(a -> a.getNumeroSigma() != null).filter(a -> a.getNumeroSigma().equals(numeroSigma)).count() > 0;