package br.gov.go.pm.controller;

import br.gov.go.pm.enuns.Profile;
import br.gov.go.pm.modelo.Usuario;
import br.gov.go.pm.service.general.GeradorDeArquivos;
import br.gov.go.pm.modelo.Craf;
import br.gov.go.pm.service.EmissaoCrafService;
import br.gov.go.pm.util.exception.NegocioException;
import br.gov.go.pm.util.jsf.FacesUtil;
import br.gov.go.pm.utilitarios.FileResource;
import br.gov.go.pm.utilitarios.ImageResource;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.persistence.NoResultException;
import java.awt.*;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
public class EmissaoCrafBean implements Serializable {

	private static final long serialVersionUID = 1L;


	@ManagedProperty(value = "#{emissaoCrafService}")
	private EmissaoCrafService emissaoCrafService;

	@ManagedProperty(value = "#{geradorDeArquivos}")
	private GeradorDeArquivos geradorDeArquivos;

	String nomeComandante = "";
	String gradComandante = "";
	String nomeAutoridadeDelegada = "";
	String gradAutoridadeDelegada = "";


	/**utilizado para impressão tratada de todos os crafs em especial a pesquisa de crafs assinados*/
	public void imprimirTodosCrafPDF() {
		try {

			/**busca todos os crafs para a lista de crafs assinados*/
			List<Craf> lc = emissaoCrafService.buscaTodosCraf();
			imprimirTodosOsCraf(lc);

		} catch (NegocioException ne) {
			FacesUtil.addErrorMessage("Problema ao gerar pdf dos craf selecionados: " + ne.getMessage());

		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao gerar pdf de todos os craf: " + e.getMessage());
		}
	}


	/**imprimi os crafs selecionados nas lista de pesquisa de craf ou enviados pra esse método uma lista.*/
	public void imprimirCrafSelecionadosPDF(List<Craf> listaCrafImprimir) {
		try {

			setaDadosEmissaoCraf(listaCrafImprimir);

		} catch (NegocioException ne) {
 				FacesUtil.addErrorMessage("Problema ao gerar pdf dos craf selecionados: " + ne.getMessage());
		} catch (Exception e) {
				FacesUtil.addErrorMessage("Erro no(s) craf selecionados: " + e.getMessage());
		}
	}

	/**Métodos utilizado para relatórios*/

	public void imprimirTodosCrafAssinados() {

		try{

			List<Craf> listaCrafsAssinados = emissaoCrafService.buscartodosCrafAssinados();
			if (listaCrafsAssinados.size() != 0 || listaCrafsAssinados != null) {
				imprimirTodosOsCraf(listaCrafsAssinados);
			}else{
				FacesUtil.addErrorMessage("Não foi encontrado nenhum Craf para impressão!");
			}

		} catch (NoResultException e ){
			FacesUtil.addErrorMessage("Não encontrada assinatura para imprimir ou não encontrado Craf");

		} catch (Exception ex) {
			FacesUtil.addErrorMessage("Problema ao gerar pdf dos craf selecionados: " + ex.getMessage());
		}
	}

	public void imprimirTodosCrafNaoAssinados() {

		try {

			List<Craf> listaCrafNaoAssinados = emissaoCrafService.buscarTodosCrafNaoAssinados();
			if (listaCrafNaoAssinados == null || listaCrafNaoAssinados.size() == 0){
				FacesUtil.addErrorMessage("Não foi encontrado nenhum Craf para impressão!");
			}else{
				imprimirTodosOsCraf(listaCrafNaoAssinados);
			}

		} catch (NoResultException e) {
			FacesUtil.addErrorMessage("Não encontrada assinatura para imprimir ou não encontrado Craf");

		} catch (Exception ex) {
			FacesUtil.addErrorMessage("Problema ao gerar pdf dos craf selecionados: " + ex.getMessage());
		}
	}

	/*end*/

	/**Método utilizado para imprimir, de forma genérica, todos os crafs. É chamado por outros métodos.*/
	public void imprimirTodosOsCraf(List<Craf>lista) throws Exception{
		setaDadosEmissaoCraf(lista);
	}

	public void addDadosAutoridade(){
		try {

			List<Usuario> autoridades = emissaoCrafService.buscaDadosAutoridade();
			autoridades.forEach(a -> {
				if (a.getProfile().equals(Profile.CMT)) {
					nomeComandante = a.getNome();
					gradComandante = a.getGraduacao();
				}else{
					nomeAutoridadeDelegada = a.getNome();
					gradAutoridadeDelegada = a.getGraduacao();
				}

			});
		}catch (NegocioException ne){
			FacesUtil.addErrorMessage(ne.getMessage());
		}
	}

	/**método para atribuição de parâmetros e responsável por interagir com GeradorCraf*/
	private void setaDadosEmissaoCraf(List<Craf> listaCrafImprimir) throws Exception {

		List<Craf> novaListaCrafImprimir = emissaoCrafService.converteAssinaturaAutoridade(listaCrafImprimir);

		addDadosAutoridade();

		/**busca as assinaturas das autoridade no AssinaturaDAO e em EmissaoCrafService trata das regras de negócio. Ex: caso não haja assinatura
		 * autoridade delegada(único caso)*/

		final Image imgFundo      = ImageResource.getMakeImage(FileResource.getStream("/WEB-INF/classes/images/carteira_fundo.jpg"));
		final Image imgCancelado      = ImageResource.getMakeImage(FileResource.getStream("/WEB-INF/classes/images/cancelado.jpg"));

		geradorDeArquivos.adicionaParametroRelatorio("nomeCmt", nomeComandante);
		geradorDeArquivos.adicionaParametroRelatorio("gradCmt", gradComandante);
		geradorDeArquivos.adicionaParametroRelatorio("nomeAutoridadeDelegada", nomeAutoridadeDelegada);
		geradorDeArquivos.adicionaParametroRelatorio("gradAutoridadeDelegada", gradAutoridadeDelegada);
		geradorDeArquivos.adicionaParametroRelatorio("fundo", imgFundo);
		geradorDeArquivos.adicionaParametroRelatorio("cancelado", imgCancelado);

		final InputStream in = FileResource.getStream("/WEB-INF/classes/jasper/".concat("Carteiras").concat(".jasper"));
		geradorDeArquivos.imprimirPDF(novaListaCrafImprimir, "Carteiras", in);
	}


	//getters and setters
	public void setEmissaoCrafService(EmissaoCrafService emissaoCrafService) {
		this.emissaoCrafService = emissaoCrafService;
	}

	public void setGeradorDeArquivos(GeradorDeArquivos geradorDeArquivos) {
		this.geradorDeArquivos = geradorDeArquivos;
	}



}//end class