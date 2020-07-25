package br.gov.go.pm.service;

import br.gov.go.pm.dao.GrupoDAO;
import br.gov.go.pm.dao.UserDAO;
import br.gov.go.pm.enuns.AcaoSistema;
import br.gov.go.pm.enuns.Profile;
import br.gov.go.pm.enuns.StatusSituacao;
import br.gov.go.pm.modelo.Grupo;
import br.gov.go.pm.modelo.Usuario;
import br.gov.go.pm.security.Seguranca;
import br.gov.go.pm.security.SegurancaDetalhe;
import br.gov.go.pm.security.util.GeradorSenha;
import br.gov.go.pm.util.exception.NegocioException;
import br.gov.go.pm.util.jsf.FacesUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
	private final Logger log = Logger.getLogger(this.getClass());

	@Autowired
	private UserDAO dao;

	@Autowired
	private GrupoDAO grupoDAO;

	@Autowired
	private SegurancaDetalhe segurancaDetalhe;

	@Autowired
	private UserAuditoriaService auditoriaService;

	public List<Usuario> list(){
		return dao.list();
	}
	boolean chancela = false;

	public Usuario buscarPeloCodigo(final Long codigo) {
		return dao.buscarPeloCodigo(codigo);
	}

	@Transactional
	public void salvar(Usuario usuario, Usuario usuarioSelecionadoAuditoria, AcaoSistema acao) throws Exception {
		if (usuario != null) {
			/**insere uma senha vazia pois a validação acontece no sigu*/
			usuario.setSenha("");
			usuario.setUsuarioSistema(segurancaDetalhe.getUsuarioPadrao());
			/**insere um status inicial para o usuário*/

			if (usuario.getStatus() == null) {
				usuario.setStatus(StatusSituacao.ATIVO);
			}
			dao.salvar(usuario);
			/**salva em auditoria o usuario cliente, o usuario atual caso seja UPDATE é dif. de null, o usuario logado e a ação.*/
			auditoriaService.salvar(usuario, usuarioSelecionadoAuditoria, segurancaDetalhe.getUsuarioCheck(), acao);

		} else {
			throw new NegocioException("Erro! Usuário é nulo no serviço.");

		}

	}

	@Transactional
	public void excluir(final Usuario usuario){
		try {
				dao.excluir(usuario);
			    auditoriaService.salvar(usuario, null, segurancaDetalhe.getUsuarioCheck(), AcaoSistema.DELETE);

		}catch (Exception e){
			FacesUtil.addErrorMessage("Erro " + e.getMessage() + " ao excluir o usuário! .");

		}
	}

	@Transactional
	public void inativar(Usuario usuario) throws NegocioException {
		try {
			usuario.setStatus(StatusSituacao.INATIVO);
			salvar(usuario, null, AcaoSistema.INATIVA);
		}catch (Exception e){
			throw new NegocioException("Erro ao inativar o usuário! .");
		}
	}

	@Transactional
	public void ativar(Usuario usuario) throws NegocioException {
		try {
			usuario.setStatus(StatusSituacao.ATIVO);
			salvar(usuario, null, AcaoSistema.ATIVA);
		}catch (Exception e){
			throw new NegocioException("Erro ao ativar o usuário! .");
		}
	}

	/** REGRAS DE NEGÓCIO PARA LISTA DE GRUPOS DO USUÁRIO*/

	/** Busca os grupos para a lista inicial*/
	public List<Grupo> buscaGrupos(){
		List<Grupo> list = grupoDAO.list() ;

		if(list == null)
			FacesUtil.addErrorMessage("Não foi possivel buscar os grupos do Usuário. Verifique a conexao, caso persista contate o Administrador!");
		else list = permissoesParaGrupos(list);

		return list;
	}

	/**verifica permissões de segurança do usuário logado e verifica se tem acessos permitidos,
	 * se NÃO, exclui da lista de grupos os não permitidos, servindo a lista inicial e a edição */
	public List<Grupo> permissoesParaGrupos(List<Grupo> lista){
		Seguranca seguranca = new Seguranca();

		if (lista !=null && !seguranca.isAdm()){
			lista.removeIf(grupo1 -> grupo1.getNome().equals("ADM"));
		}

		if (lista !=null && !seguranca.isCmt()){
			lista.removeIf(grupo1 -> grupo1.getNome().equals("CHANCELA"));
		}

		if ((lista !=null && !seguranca.isAdm()) && (lista !=null && !seguranca.isCmt()) && (lista !=null && !seguranca.isChefe())){
			lista.removeIf(grupo1 -> grupo1.getNome().equals("CMT"));
		}

		if (lista !=null && seguranca.isCmt()){
			lista.removeIf(grupo1 -> !grupo1.getNome().equals("CHANCELA"));
		}

	 return lista;
	}

	/**END RN LISTA GRUPOS*/


	public List<Usuario> buscarUsuariosEspecificos(String nome, String cpf){
		return dao.buscaUsuariosEspecificos(nome, cpf);
	}
	/**mais utilizado nas funções pois busca um usuario pelo cpf*/
	public Optional<Usuario> usuarioPorCpf(String cpf) throws NegocioException{
		return dao.usuarioPorCpf(cpf);

	}

	@Transactional
    public void salvarSenhaAssinatura(Usuario user) throws NegocioException{
		/***verifica o usuario logado */
		Usuario usuario = segurancaDetalhe.getUsuarioCheck();
		/**verifica se o usuário logado possui perfil de chancela*/
		segurancaDetalhe.getGruposUsuarioLogado().forEach(grupo -> {
			if(grupo.getNome().equals("CHANCELA"))
				chancela = true;
		});

		/**Apenas cmt, autoridade delegada ou chancela podem salvar assinatura*/
		if (usuario.getProfile().equals(Profile.CMT) || usuario.getProfile().equals(Profile.AUTORIDADE_DELEGADA)
				|| chancela){
			/**faz um encode da senha*/
			String senhaCrip = GeradorSenha.encodeSenha(user.getSenhaAssinatura());
			usuario.setSenhaAssinatura(senhaCrip);
			usuario.setGrupos(segurancaDetalhe.getGruposUsuarioLogado());
			try {
				/**salva o usuário atual verificado c/ dados a serem alterados.*/
				dao.salvar(usuario);
			} catch (Exception e) {
				log.error(e, e.getCause());
				throw new NegocioException("Erro no serviço de salvar assinatura");
			}
		}else{
			throw new NegocioException("Desculpe, você não tem perfil para cadastrar essa senha!");
		}

    }

    public String gerarListaDeUsuariosParaImprimir(List<Usuario> list) {
		List<String>lista = new ArrayList<>();
		final String INDICADOR = "- ";

		lista.add("Total de Usuários: ".concat(String.valueOf(list.size())));

		list.forEach(l -> {
			String grupos = "";

			for (Grupo grupo: l.getGrupos()) {
				grupos += grupo.getNome() + ", ";

			}
			String str = grupos.substring(0, grupos.length() - 2);
			String grupoFormatado = "Grupos de Perfil: {" + str + "}";

			lista.add((char)13 + INDICADOR.concat(l.getNome()).concat(",  ")
					.concat((l.getCpf() == null || l.getCpf().equals("0") ? "Sem CPF" : l.getCpf()).concat(",  ")
					.concat((l.getCpf() == null ? "Sem CPF":l.getCpf())).concat(",  ")
					.concat((l.getStatus().getDescricao() == null ? "Sem Status" : l.getStatus().getDescricao())).concat(",  ")
					.concat(grupoFormatado).concat(" ")));
		});

		String str = lista.toString();
		String strFormatado = str.substring(1, str.length() - 1);

		return strFormatado;
    }
}