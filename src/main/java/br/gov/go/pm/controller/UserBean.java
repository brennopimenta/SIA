
package br.gov.go.pm.controller;

import br.gov.go.pm.dao.MarcaArmaDAO;
import br.gov.go.pm.enuns.AcaoSistema;
import br.gov.go.pm.enuns.Profile;
import br.gov.go.pm.enuns.StatusEmissao;
import br.gov.go.pm.enuns.StatusSituacao;
import br.gov.go.pm.modelo.Arma;
import br.gov.go.pm.modelo.DadosSicad;
import br.gov.go.pm.modelo.Grupo;
import br.gov.go.pm.modelo.Usuario;
import br.gov.go.pm.service.UserService;
import br.gov.go.pm.service.general.AtualizaPaginaService;
import br.gov.go.pm.service.general.ConsumirServicoSicad;
import br.gov.go.pm.service.general.GeradorDeArquivos;
import br.gov.go.pm.util.exception.NegocioException;
import br.gov.go.pm.util.jsf.FacesUtil;
import br.gov.go.pm.utilitarios.MostraDataAtual;
import br.gov.go.pm.utilitarios.VisualizarListaResource;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.*;

@ManagedBean
@ViewScoped
public class UserBean implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@ManagedProperty(value = "#{userService}")
	private UserService userService;

	@ManagedProperty(value = "#{atualizaPaginaService}")
	private AtualizaPaginaService atualizaPaginaService;

	@ManagedProperty(value = "#{geradorDeArquivos}")
	private GeradorDeArquivos geradorDeArquivos;

	@ManagedProperty(value = "#{consumirServicoSicad}")
	private ConsumirServicoSicad consumirServicoSicad;


	private List<Usuario> usuarios = new ArrayList<>();
	private List<Profile> profiles = new ArrayList<>();
	private List<Grupo> grupos = new ArrayList<>();
	private List<Grupo> gruposEdit = new ArrayList<>();
	private Usuario user;
	private Usuario userSelecionado;
	private Boolean pintaCampo = false;



	@ManagedProperty(value = "#{marcaArmaDAO}")
	private MarcaArmaDAO marcaArmaDAO;


	@PostConstruct
	public void init(){
		limpar();
		usuarios = userService.list();
		profiles = Arrays.asList(Profile.values());
		grupos = userService.buscaGrupos();
		gruposEdit.addAll(grupos);

	}

	public void pegarPrimeiroNome(){
		String nome = userSelecionado.getNome();
		int i = 0;
		if (userSelecionado != null) {
			i = nome.indexOf(" ");
			userSelecionado.setNome(nome.substring(0, i));
		}
	}

	public void atualizar(){
		limpar();
		atualizaPaginaService.AtualizaPagina();
	}

	public void salvar(){
		try {
			Optional<Usuario> userExiste = null;

			/**Se usuario selecionado é inexistente significa que não há usuário em edição e então busca usuario existente,
			 * e neste caso, se houver não deixa salvar*/
			if(userSelecionado.getCodigo() == null) {
				userExiste = userService.usuarioPorCpf(user.getCpf());
				if (userExiste.isPresent()) {
					throw new NegocioException("Ops! Já existe um usuário cadastrado para este cpf ");
				}else{
					userService.salvar(user, null, AcaoSistema.CREATE);
				}
			}else{
				Usuario userSelecionadoAuditoria = userService.buscarPeloCodigo(userSelecionado.getCodigo());
				userService.salvar(user, userSelecionadoAuditoria, AcaoSistema.UPDATE);
			}

			FacesUtil.addSuccessMessage("Usuário Salvo com sucesso!");

			if (user.getSenhaAssinatura() == null || user.getSenhaAssinatura().equals("")){
				/**verifica se usuario tem chancela e senha vazia ou nula*/
				if(user.getGrupos().stream().filter(g -> g.getNome().equals("CHANCELA")).count() > 0)
					FacesUtil.addAlertMessage("AVISO! Este usuário pertece ao grupo de CHANCELA e não possui uma senha de assinatura, por favor cadastre o mais breve possível.");
			}
			limpar();
			atualizaPaginaService.AtualizaPagina();

		}catch (NegocioException e){
			FacesUtil.addErrorMessage(e.getMessage());
		}catch (NullPointerException ex){
			FacesUtil.addErrorMessage("Erro ao salvar o usuário, não foi possível obter o usuário. " + ex.getMessage());
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao salvar o usuário: " + e.getMessage());
		}
	}

	public void inativaEAtiva() {
		try{
		user = userService.buscarPeloCodigo(userSelecionado.getCodigo());

		if (user.getStatus().equals(StatusSituacao.ATIVO)){
			userService.inativar(user);
			limpar();
			FacesUtil.addSuccessMessage("Usuário INATIVADO com sucesso!");
		} else{
			userService.ativar(user);
			limpar();
			FacesUtil.addSuccessMessage("Usuário ATIVADO com sucesso!");
		}

		atualizaPaginaService.AtualizaPagina();

	}catch (NegocioException e){
		FacesUtil.addErrorMessage(e.getMessage());
	}
	}

	public void edicao(){
		List<Grupo> novaLista = new ArrayList<>();;
		List<Grupo> novosItens = new ArrayList<>();

		for (Grupo grupoSelecionado: getUserSelecionado().getGrupos()) {
			for (Grupo grupoBase: grupos ) {
				if (grupoSelecionado.getNome().equals(grupoBase.getNome())) {
					gruposEdit.remove(grupoBase);
					novosItens.add(grupoSelecionado);
				}
			}

		}
		novaLista.addAll(gruposEdit);
		novaLista.addAll(novosItens);

		/**todos os grupos que são permitidos ao usuário logado.*/
		novaLista = userService.permissoesParaGrupos(novaLista);

		setGrupos(novaLista);
		setUser(getUserSelecionado());

		procedurePosEdicao();

	}

	public void procedurePosEdicao(){
		gruposEdit = new ArrayList<>();
		/**seta os grupos do usuário editado no objeto adquirido da nova lista.*/
		gruposEdit.addAll(grupos);
		pintaCampo = true;

	}

	public void excluir() throws NegocioException {
		if (!userSelecionado.getGrupos().isEmpty()) {
			FacesUtil.addErrorMessage("Usuário não excluído pois pertence a um grupo!");
		} else {
			userService.excluir(userSelecionado);
			usuarios.remove(userSelecionado);
			FacesUtil.addSuccessMessage("Usuário " + userSelecionado.getNome() + " excluído com sucesso");

			atualizaPaginaService.AtualizaPagina();
		}

	}

	public void salvarSenhaAssinatura(){
		try {
			if (user.getSenhaAssinatura() != null){
				userService.salvarSenhaAssinatura(user);
				FacesUtil.addSuccessMessage("Senha salva com sucesso!");
			}

		}catch (NegocioException ne){
			FacesUtil.addErrorMessage(ne.getMessage());
		}

	}

	public void preencherUsuario(){
		DadosSicad ds = consumirServicoSicad.busca(user.getCpf());

		if(ds != null) {
			user.setNome(ds.getNome());
			user.setGraduacao(ds.getPostoSiglaGrad());
		}else
		 FacesUtil.addAlertMessage("Usuário não encontrado no cadastro mas você pode inserir dados manualmente.");
	}

	public void limpar(){
		user = new Usuario();
		userSelecionado = new Usuario();

	}

	public void pesquisaEspecifica(){
		if (user.getNome() == null || user.getNome().trim().equals("") &&
				(user.getCpf() == null ||user.getCpf().trim().equals(""))){
			usuarios = userService.list();
		}else{
			usuarios = userService.buscarUsuariosEspecificos(user.getNome(), user.getCpf());

		}
	}

	public void gerarListaDeUsuarios(){
		List<Usuario> listaDeUsers = userService.list();
		String titulo = "Lista de Usuarios Cadastrados";
		String lista = null;
		if (!listaDeUsers.isEmpty())
			lista = userService.gerarListaDeUsuariosParaImprimir(listaDeUsers);
		VisualizarListaResource.visualizarLista(lista, titulo, StatusEmissao.PAISAGEM, "inline");

	}



	/**utilizado para mostrar a data atual em um campo data somente leitura*/
	public Date getDataDeHoje(){return MostraDataAtual.getDataDeHoje();
	}

	//Getters and Setters

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public AtualizaPaginaService getAtualizaPaginaService() {
		return atualizaPaginaService;
	}

	public void setAtualizaPaginaService(AtualizaPaginaService atualizaPaginaService) {
		this.atualizaPaginaService = atualizaPaginaService;
	}

	public List<Usuario> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(List<Usuario> usuarios) {
		this.usuarios = usuarios;
	}

	public List<Profile> getProfiles() {
		return profiles;
	}

	public void setProfiles(List<Profile> profiles) {
		this.profiles = profiles;
	}

	public List<Grupo> getGrupos() {
		return grupos;
	}

	public void setGrupos(List<Grupo> grupos) {
		this.grupos = grupos;
	}

	public List<Grupo> getGruposEdit() {
		return gruposEdit;
	}

	public void setGruposEdit(List<Grupo> gruposEdit) {
		this.gruposEdit = gruposEdit;
	}

	public Usuario getUser() {
		return user;
	}

	public void setUser(Usuario user) {
		this.user = user;
	}

	public Usuario getUserSelecionado() {
		return userSelecionado;
	}

	public void setUserSelecionado(Usuario userSelecionado) {
		this.userSelecionado = userSelecionado;
	}

	public Boolean getPintaCampo() {
		return pintaCampo;
	}

	public void setPintaCampo(Boolean pintaCampo) {
		this.pintaCampo = pintaCampo;
	}


	public void setGeradorDeArquivos(GeradorDeArquivos geradorDeArquivos) {
		this.geradorDeArquivos = geradorDeArquivos;
	}

	public void setMarcaArmaDAO(MarcaArmaDAO marcaArmaDAO) {
		this.marcaArmaDAO = marcaArmaDAO;
	}

	public void setConsumirServicoSicad(ConsumirServicoSicad consumirServicoSicad) {
		this.consumirServicoSicad = consumirServicoSicad;
	}
}//end class