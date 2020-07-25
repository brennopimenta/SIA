package br.gov.go.pm.dao;

import br.gov.go.pm.modelo.ArmaModelo;
import br.gov.go.pm.util.exception.InfraException;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class ModeloArmaDAO {

	private Logger log = Logger.getLogger(getClass());

	@PersistenceContext
	private EntityManager manager;

	public ArmaModelo buscarPeloCodigo(Long codigo) {

		try {
			return manager.find(ArmaModelo.class, codigo);
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new InfraException(e);
		}

	}

	public void salvar(ArmaModelo modelo) {

		try {
			manager.merge(modelo);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new InfraException(e);
		}
	}

	//busca todas as ArmaModelos
	public List<ArmaModelo> buscarTodos() {
		return manager.createQuery("select m from ArmaModelo m").getResultList(); 
	}


	/**busca todas as cargas que foram geradas craf*/
	public List<ArmaModelo> buscarModeloPorFabricante(Long codigoFab) {
		try {
			return manager.createQuery("select m FROM ArmaModelo m JOIN fetch m.marca a where a.codigo = :codigoFab and m.status = br.gov.go.pm.enuns.StatusSituacao.ATIVO")
					.setParameter("codigoFab", codigoFab)
					.getResultList();

		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new InfraException(e);
		}
	}


    public List<ArmaModelo> buscarPorDescricao(String modelo) {
		try{
			return manager.createQuery("select m from ArmaModelo m where m.modelo like :modelo", ArmaModelo.class)
					.setParameter("modelo", "%" + modelo + "%")
					.getResultList();

		}catch(NoResultException e){
			return null;
		}catch(Exception e){
			log.error(e);
			throw new InfraException(e);
		}
    }
}//end class



