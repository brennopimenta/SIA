
package br.gov.go.pm.controller;

import br.gov.go.pm.enuns.StatusEmissao;
import br.gov.go.pm.modelo.UsuarioAuditoria;
import br.gov.go.pm.service.UserAuditoriaService;
import br.gov.go.pm.service.general.AtualizaPaginaService;
import br.gov.go.pm.service.general.GeradorDeArquivos;
import br.gov.go.pm.util.jsf.FacesUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@ViewScoped
public class UserAuditoriaBean implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@ManagedProperty(value = "#{userAuditoriaService}")
	private UserAuditoriaService userAuditoriaService;

	@ManagedProperty(value = "#{atualizaPaginaService}")
	private AtualizaPaginaService atualizaPaginaService;

	@ManagedProperty(value = "#{geradorDeArquivos}")
	private GeradorDeArquivos geradorDeArquivos;

	private List<UsuarioAuditoria> usuariosAuditoria;
	private List<UsuarioAuditoria> listAuditoriasSelecionadas;

	private UsuarioAuditoria auditoriaSelecionada;
	private String busca;
	private boolean desabilitaSelecao;
	private boolean permitidaExclusao;

	@PostConstruct
	public void init(){
		limpar();
		inicializaListas();

	}

	public void inicializaListas(){
		usuariosAuditoria = userAuditoriaService.list();
	}


	public void limpar(){
		usuariosAuditoria = new ArrayList<>();
		listAuditoriasSelecionadas = new ArrayList<>();
		desabilitaSelecao = true;
		permitidaExclusao = false;
	}

	public void pesquisaEspecifica(){
		if (busca != null){
			usuariosAuditoria = userAuditoriaService.buscaAuditoriaEspecificas(busca);
		}else{
			usuariosAuditoria = userAuditoriaService.list();
		}
	}

	public void gerarAuditoria(){
		/*manda a lista de auditoria para extrair os dados*/
		String auditoriaGerada = userAuditoriaService.geraDadosAuditoria(listAuditoriasSelecionadas);

		userAuditoriaService.visualizarLista(auditoriaGerada, "Auditoria de Usuários Selecionados", StatusEmissao.RETRATO, "inline");

	}

	public void gerarAuditoriaUnica(){
		List<UsuarioAuditoria> list = new ArrayList<>();
		/**passado uma lista pois o método pede uma lista*/
		list.add(auditoriaSelecionada);
		String auditoriaGerada = userAuditoriaService.geraDadosAuditoria(list);

		userAuditoriaService.visualizarLista(auditoriaGerada, "Auditoria de Usuário", StatusEmissao.RETRATO, "inline");

	}

	public void rowSelectCheckbox(){
		permitidaExclusao = true;
	}

	public void rowUnselectCheckbox(){
		permitidaExclusao = false;
	}

	public void excluir(){
		if (listAuditoriasSelecionadas.isEmpty()) {
			FacesUtil.addErrorMessage("Por favor Selecione a(s) auditoria(s) que deseja excluir.");
		}else {
			userAuditoriaService.excluir(listAuditoriasSelecionadas);
			usuariosAuditoria.remove(listAuditoriasSelecionadas);
			limpar();
			FacesUtil.addSuccessMessage("Auditorias excluídas com sucesso");

			atualizaPaginaService.AtualizaPagina();
		}


	}


	//getters and setters


	public List<UsuarioAuditoria> getUsuariosAuditoria() {
		return usuariosAuditoria;
	}

	public void setUsuariosAuditoria(List<UsuarioAuditoria> usuariosAuditoria) {
		this.usuariosAuditoria = usuariosAuditoria;
	}

	public GeradorDeArquivos getGeradorDeArquivos() {
		return geradorDeArquivos;
	}

	public void setGeradorDeArquivos(GeradorDeArquivos geradorDeArquivos) {
		this.geradorDeArquivos = geradorDeArquivos;
	}

	public void setUserAuditoriaService(UserAuditoriaService userAuditoriaService) {
		this.userAuditoriaService = userAuditoriaService;
	}

	public void setAtualizaPaginaService(AtualizaPaginaService atualizaPaginaService) {
		this.atualizaPaginaService = atualizaPaginaService;
	}

	public List<UsuarioAuditoria> getListAuditoriasSelecionadas() {
		return listAuditoriasSelecionadas;
	}

	public void setListAuditoriasSelecionadas(List<UsuarioAuditoria> listAuditoriasSelecionadas) {
		this.listAuditoriasSelecionadas = listAuditoriasSelecionadas;
	}

	public boolean isDesabilitaSelecao() {
		return desabilitaSelecao;
	}

	public void setDesabilitaSelecao(boolean desabilitaSelecao) {
		this.desabilitaSelecao = desabilitaSelecao;
	}

	public UsuarioAuditoria getAuditoriaSelecionada() {
		return auditoriaSelecionada;
	}

	public void setAuditoriaSelecionada(UsuarioAuditoria auditoriaSelecionada) {
		this.auditoriaSelecionada = auditoriaSelecionada;
	}

	public String getBusca() {
		return busca;
	}

	public void setBusca(String busca) {
		this.busca = busca;
	}

	public boolean isPermitidaExclusao() {
		return permitidaExclusao;
	}

	public void setPermitidaExclusao(boolean permitidaExclusao) {
		this.permitidaExclusao = permitidaExclusao;
	}
}//end class