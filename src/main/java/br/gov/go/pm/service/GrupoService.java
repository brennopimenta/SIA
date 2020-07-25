package br.gov.go.pm.service;

import br.gov.go.pm.dao.GrupoDAO;
import br.gov.go.pm.dao.UserDAO;
import br.gov.go.pm.modelo.Grupo;
import br.gov.go.pm.security.SegurancaDetalhe;
import br.gov.go.pm.util.exception.NegocioException;
import br.gov.go.pm.util.jsf.FacesUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class GrupoService {
	private final Logger log = Logger.getLogger(this.getClass());

	@Autowired
	private GrupoDAO dao;

	@Autowired
	private UserDAO userDAO;

	@Autowired
	private SegurancaDetalhe segurancaDetalhe;

	/** Busca os grupos para a lista inicial*/
	public List<Grupo> list(){
		return dao.list();
	}

	public Grupo buscarPeloCodigo(final Long codigo) {
		return dao.buscarPeloCodigo(codigo);
	}

	public List<Grupo> buscaGruposPorParametro(String nome){
		return dao.buscaGruposPorParametro(nome);
	}

	public Optional<Grupo> buscaGrupoEspecifico(String nome){
		return dao.buscaGrupoEspecifico(nome);
	}

	/**busca se o grupo está sendo utilizado por algum usuário
	 * @param codigo*/
	public void verificaGrupoUtilizadoPorUser(Long codigo) throws NegocioException {
		Optional<Long> grupoExiste = userDAO.buscaGrupoUtilizadoPorUser(codigo);
		if (grupoExiste.isPresent())
			throw new NegocioException("Esse grupo já está sendo utilizado. Não é possível realizar a operação.");

	}

	/**verifica se algum grupo com o nome escolhido já existe*/
	public void verificaGrupoExistente(Grupo grupo) throws NegocioException{
		Optional<Grupo> grupoExiste = buscaGrupoEspecifico(grupo.getNome());
		if (grupoExiste.isPresent())
			throw new NegocioException("Já existe esse Grupo cadastrado");

	}

	@Transactional
	public void salvar(Grupo grupo) throws Exception{
		try {

			if (grupo != null) {

				/**insere quem criou ou alterou por último o usuário*/
				grupo.setUsuarioAdministrador(segurancaDetalhe.getNomeUsuario());

				dao.salvar(grupo);
				FacesUtil.addSuccessMessage("Usuário salvo com sucesso!");
			} else {
				throw new NullPointerException();

			}
		}catch (NullPointerException ex){
			FacesUtil.addErrorMessage("Erro ao salvar o grupo, objeto está nulo erro " + ex.getMessage() + ". Tente novamente ou contate o administrador.");
		}catch (Exception e){
			FacesUtil.addErrorMessage("Erro ao salvar o grupo, favor verifique, tente novamente ou contate o Administrador. " + e.getMessage());
		}

	}

	@Transactional
	public void excluir(final Grupo grupo){
		try {
				dao.excluir(grupo);

		}catch (Exception e){
			FacesUtil.addErrorMessage("Erro " + e.getMessage() + " ao excluir o grupo! .");

		}
	}




}