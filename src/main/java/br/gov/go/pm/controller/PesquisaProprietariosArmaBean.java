
package br.gov.go.pm.controller;

import br.gov.go.pm.enuns.StatusEmissao;
import br.gov.go.pm.modelo.Arma;
import br.gov.go.pm.service.PesquisaArmaService;
import br.gov.go.pm.util.AppTools;
import br.gov.go.pm.util.jsf.FacesUtil;
import br.gov.go.pm.utilitarios.VisualizarListaResource;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@ViewScoped
public class PesquisaProprietariosArmaBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{pesquisaArmaService}")
	private PesquisaArmaService pesquisaArmaService;

	private Arma arma;
	private List<Arma> armasSelecionadas;

	/**No pesquisaArmaBean foi incluído rotina para buscar todos os proprietários da arma*/
	private List<Arma> proprietariosArmas;

	@PostConstruct
	public void inicializar() {
		proprietariosArmas = pesquisaArmaService.buscarProprietarios();
		limpar();

	}

	/**pesquisa as armas pertencentes ao proprietário (que está em arma)*/
	public void pesquisarArmasProprietario(){
		try {
			armasSelecionadas = pesquisaArmaService.buscarArmasPorProprietario(arma.getCpfNovoProprietario());
			listarFotoArmas();
			getTotalReg();
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Houve algum erro na busca de armas para proprietário. Lista possivelmente não foi preenchida.");
		}

	}

	public int getTotalReg(){
		return armasSelecionadas.size();
	}


	/**pesquisa as armas por proprietário, na consulta especifica*/
	public void pesquisaEspecifica(){
		if (arma.getRgProprietario() == null || arma.getRgProprietario().trim().equals("") &&
				(arma.getCpfNovoProprietario() == null ||arma.getCpfNovoProprietario().trim().equals(""))){
			proprietariosArmas = pesquisaArmaService.buscarProprietarios();
		}else{
			proprietariosArmas = pesquisaArmaService.buscarProprietariosEspecificos(arma.getRgProprietario(), arma.getCpfNovoProprietario());

		}
	}

	public void gerarListaProprietariosDeArmas(){
		/**busca todas as armas */
		List<Arma> listaDeArmas = pesquisaArmaService.buscarTodasAsArmas();
		String titulo = "Lista de Proprietários de Armas";
		String lista = null;
		if (!listaDeArmas.isEmpty())
			lista = pesquisaArmaService.gerarListaDeProprietariosDeArmasParaImprimir(listaDeArmas);
		VisualizarListaResource.visualizarLista(lista, titulo, StatusEmissao.PAISAGEM, "inline");

	}

	public void limpar(){
		arma = new Arma();
		armasSelecionadas = new ArrayList<>();

	}

	/** criar arquivo com a foto da arma em pasta temporária para ser listado */
	public void listarFotoArmas() {
		try {
			for (Arma armaSelecionada : armasSelecionadas) {
				String nomeArquivo = "ArmaMod".concat(String.valueOf(armaSelecionada.getModelo().getCodigo()));
				byte[] foto = armaSelecionada.getModelo().getFoto();

				if (foto != null){
					AppTools.geraFotoArma(nomeArquivo, foto);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	//getters and setters

	public List<Arma> getProprietariosArmas() {
		return proprietariosArmas;
	}

	public void setProprietariosArmas(List<Arma> proprietariosArmas) {
		this.proprietariosArmas = proprietariosArmas;
	}


	public List<Arma> getArmaSelecionada() {
		return armasSelecionadas;
	}


	public void setArmaSelecionada(List<Arma> armasSelecionadas) {
		this.armasSelecionadas = armasSelecionadas;
	}


	public Arma getArma() {
		return arma;
	}

	public void setArma(Arma arma) {
		this.arma = arma;
	}

	public List<Arma> getArmasSelecionadas() {
		return armasSelecionadas;
	}

	public void setArmasSelecionadas(List<Arma> armasSelecionadas) {
		this.armasSelecionadas = armasSelecionadas;
	}

	public void setPesquisaArmaService(PesquisaArmaService pesquisaArmaService) {
		this.pesquisaArmaService = pesquisaArmaService;
	}
}