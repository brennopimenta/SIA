package br.gov.go.pm.dao;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import br.gov.go.pm.util.exception.InfraException;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import br.gov.go.pm.modelo.Paises;


@Repository
public class PaisesDAO{

	private Logger log = Logger.getLogger(getClass());

	@PersistenceContext
	private EntityManager manager;

	public Paises buscarPorCodigo(final Long codigo) {
		try{
			return manager.find(Paises.class, codigo);
		}catch(NoResultException e){
			return null;
		}catch(Exception e){
			log.error(e);
			throw new InfraException(e);
		}
	}
	
	public void salvar(final Paises pais) {
		try{
			manager.merge(pais);
		}catch(Exception e){
			log.error(e);
			throw new InfraException(e);	
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Paises> buscarTodos() {
		try{
			return manager.createNamedQuery("Paises.buscarTodos").getResultList();
		}catch(Exception e){
			log.error(e);
			throw new InfraException(e);	
		}
	}
	
	public Optional <List<Paises>> buscarPorDescricao(String descricao){
		try{
			return Optional.of(manager.createNamedQuery("Paises.buscarPaisesEspecificos", Paises.class)
					 .setParameter("pais", descricao)
					 .getResultList());
			
		}catch(NoResultException e){
			return Optional.empty();
		}catch(Exception e){
			log.error(e);
			throw new InfraException(e);	
		}
	}

	public Paises buscarPaisEspecifico(String pais){
		try{
			return manager.createQuery("select p FROM Paises p where p.descricao = :pais", Paises.class)
					.setParameter("pais", pais)
					.getSingleResult();

		}catch(NoResultException e){
			return null;
		}catch(Exception e){
			log.error(e);
			throw new InfraException(e);
		}
	}

	public void excluir(final Paises pais){  	
		try{
			final Paises p = manager.find(Paises.class, pais.getCodigo());
			manager.remove(p);
			manager.flush();
		}catch(NoResultException e){
			log.debug("not found");
		}catch(Exception e){
			log.error(e);
			throw new InfraException(e);	
		}
	}
	
}