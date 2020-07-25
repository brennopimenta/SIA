package br.gov.go.pm.dao;

import br.gov.go.pm.modelo.ArmaInativa;
import br.gov.go.pm.modelo.Carga;
import br.gov.go.pm.util.exception.InfraException;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class InativacaoArmaDAO {

	private Logger log = Logger.getLogger(getClass());

	@PersistenceContext
	private EntityManager manager;

	public ArmaInativa buscarPeloCodigo(Long codigo) {

		try {
			return manager.find(ArmaInativa.class, codigo);
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new InfraException(e);
		}

	}

	public void salvar(ArmaInativa armaInativa) {

		try {
			manager.merge(armaInativa);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new InfraException(e);
		}
	}

	public List<ArmaInativa> buscarTodos() {
		return manager.createQuery("select i from ArmaInativa i").getResultList();
	}


	/**busca inativações pela arma*/
	public List<ArmaInativa> listarAtivacaoInativacaoPelaArma(final String arma){
		try {

			return manager.createQuery("select ai from ArmaInativa ai JOIN fetch ai.arma where ai.arma.numeroArma LIKE :arma")
					.setParameter("arma", "%" + arma + "%")
					.getResultList();

		} catch (Exception e) {
			log.error(e.getMessage(), e.getCause());
			return null;
		}

	}

}//end class



