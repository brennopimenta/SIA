package br.gov.go.pm.service;

import br.gov.go.pm.dao.AssinaturaDAO;
import br.gov.go.pm.dao.UserDAO;
import br.gov.go.pm.enuns.StatusSituacao;
import br.gov.go.pm.modelo.Assinaturas;
import br.gov.go.pm.modelo.Usuario;
import br.gov.go.pm.security.SegurancaDetalhe;
import br.gov.go.pm.util.exception.InfraException;
import br.gov.go.pm.util.exception.NegocioException;
import br.gov.go.pm.util.jsf.FacesUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AssinaturaService {
	private Logger log = Logger.getLogger(getClass());

	@Autowired
	private AssinaturaDAO dao;

	@Autowired
	private UserDAO userDAO;

	@Autowired
	private SegurancaDetalhe segurancaDetalhe;

	@Transactional
	public void salvar(Assinaturas assinatura) throws NegocioException {

		  try {
			  if (assinatura == null) {
				  throw new NegocioException("falta usuário! Por favor entre com usuário novamente ou contate o Administrador");
			  } else {

				  if (assinatura.getStatus() == null)
					  assinatura.setStatus(StatusSituacao.ATIVO);

				  /**insere quem criou ou alterou por último o usuário*/
				  assinatura.setUsuarioSistema(segurancaDetalhe.getUsuarioPadrao());


				  dao.salvar(assinatura);

			  }
		  }catch (InfraException ie){
			  throw new NegocioException("Falha ao salvar assinatura! Verifique sua conexão e refaça, caso persista contate o administrador.");
		  }

	}

	public void excluir(final Assinaturas assinatura) throws Exception {
		dao.excluir(assinatura);
	}

	@Transactional
	public void inativar(Assinaturas assinatura){
		try {
			assinatura.setStatus(StatusSituacao.INATIVO);
			salvar(assinatura);
		}catch (Exception e){
			FacesUtil.addErrorMessage("Erro ao inativar a Assinaturas! Se persistir o erro contate o Administrador.");
		}
	}


	/**buscas em Assinaturas*/
	public List<Assinaturas> buscarTodas(){
		return dao.buscarTodos();
	}

	public Assinaturas buscarPeloCodigo(final Long codigo) {
		return dao.buscarPeloCodigo(codigo);
	}


	/**buscas em Usuarios*/
	public List<Usuario> buscarUsuariosParaAssinatura(){
		return userDAO.buscaUsuarioParaAssinatura();
	}

	public void verificaAutoridadeExistente(Assinaturas assinatura) throws NegocioException{
		Optional<Assinaturas> existe = dao.verificaAutoridadeExistente(assinatura.getUsuario().getProfile());
		if (existe.isPresent())
			throw new NegocioException("já existe um usuario com perfil de " + assinatura.getUsuario().getProfile());
	}


}
