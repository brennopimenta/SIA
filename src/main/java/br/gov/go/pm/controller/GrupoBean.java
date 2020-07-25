
package br.gov.go.pm.controller;

import br.gov.go.pm.modelo.Grupo;
import br.gov.go.pm.service.general.AtualizaPaginaService;
import br.gov.go.pm.service.GrupoService;
import br.gov.go.pm.util.exception.NegocioException;
import br.gov.go.pm.util.jsf.FacesUtil;
import br.gov.go.pm.utilitarios.MostraDataAtual;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
public class GrupoBean implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@ManagedProperty(value = "#{grupoService}")
	private GrupoService grupoService;

	@ManagedProperty(value = "#{atualizaPaginaService}")
	private AtualizaPaginaService atualizaPaginaService;

	private List<Grupo> grupos = new ArrayList<>();
	private Grupo grupo;
	private Grupo grupoSelecionado;

	@PostConstruct
	public void init(){
		limpar();
		grupos = grupoService.list();

	}

	public void salvar(){
		try {
			/**se NÃO é edição então verifica se o que digitei já existe no banco*/
			if (grupoSelecionado.getCodigo() == null){
				grupoService.verificaGrupoExistente(grupo);

				}
			/**se é edição então verifica se o que digitei já existe no banco
			 * e ta,bém se algum grupo já utiliza o grupo que estou salvando, se sim não prossegue*/
			if (grupoSelecionado.getCodigo() != null){
				grupoService.verificaGrupoExistente(grupoSelecionado);
				grupoService.verificaGrupoUtilizadoPorUser(grupoSelecionado.getCodigo());
			}

				grupoService.salvar(grupo);

				limpar();
				atualizaPaginaService.AtualizaPagina();


		} catch (Exception e) {
			FacesUtil.addErrorMessage("Ops! " + e.getMessage());
		}
	}

	public void edicao(){
		setGrupo(grupoSelecionado);
	}

	public void excluir() throws NegocioException {
		if(grupoSelecionado != null){

			grupoService.verificaGrupoUtilizadoPorUser(grupoSelecionado.getCodigo());


			grupoService.excluir(grupoSelecionado);
			grupos.remove(grupoSelecionado);
			FacesUtil.addSuccessMessage("Grupo " + grupoSelecionado.getNome() + " excluído com sucesso");

			atualizaPaginaService.AtualizaPagina();
		}
	}

	public void limpar(){
		grupo = new Grupo();
		grupoSelecionado = new Grupo();

	}

	public void pesquisaEspecifica(){
		if (grupo.getNome() == null || grupo.getNome().trim().equals("")){
			grupos = grupoService.list();
		}else{
			grupos = grupoService.buscaGruposPorParametro(grupo.getNome());

		}
	}

	/**utilizado para mostrar a data atual em um campo data somente leitura*/
	public Date getDataDeHoje(){
		return MostraDataAtual.getDataDeHoje();
	}

	//Getters and Setters


	public List<Grupo> getGrupos() {
		return grupos;
	}

	public void setGrupos(List<Grupo> grupos) {
		this.grupos = grupos;
	}

	public Grupo getGrupo() {
		return grupo;
	}

	public void setGrupo(Grupo grupo) {
		this.grupo = grupo;
	}

	public Grupo getGrupoSelecionado() {
		return grupoSelecionado;
	}

	public void setGrupoSelecionado(Grupo grupoSelecionado) {
		this.grupoSelecionado = grupoSelecionado;
	}

	public void setGrupoService(GrupoService grupoService) {
		this.grupoService = grupoService;
	}

	public void setAtualizaPaginaService(AtualizaPaginaService atualizaPaginaService) {
		this.atualizaPaginaService = atualizaPaginaService;
	}
}