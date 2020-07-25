package br.gov.go.pm.dao;

import br.gov.go.pm.modelo.Cidade;
import br.gov.go.pm.util.exception.InfraException;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;


@Repository
public class CidadeDAO {

	private Logger log = Logger.getLogger(getClass());

	@PersistenceContext
	private EntityManager manager;

	public Cidade buscarPorCodigo(final Long codigo) {
		try{
			return manager.find(Cidade.class, codigo);
		}catch(NoResultException e){
			return null;
		}catch(Exception e){
			log.error(e);
			throw new InfraException(e);
		}
	}

	public void salvar(final Cidade cidade) {
		try{
			manager.merge(cidade);
		}catch(Exception e){
			log.error(e);
			throw new InfraException(e);
		}
	}

	public List<Cidade> buscarTodos(){
		try {
			return manager.createQuery("select c from Cidade c").getResultList();

		} catch (Exception e) {
			log.error(e);
			throw new InfraException(e);
		}

	}

	public List<Cidade> buscarPorDescricao(String descricao){
		try{
			return manager.createQuery("select c from Cidade c where c.descricao like :descricao", Cidade.class)
					 .setParameter("descricao", "%" + descricao + "%")
					 .getResultList();

		}catch(NoResultException e){
			return null;
		}catch(Exception e){
			log.error(e);
			throw new InfraException(e);
		}
	}

	public Cidade buscarCidadeEspecifica(String cidade){
		try{
			return manager.createQuery("select c FROM Cidade c where c.descricao = :cidade", Cidade.class)
					.setParameter("cidade", cidade)
					.getSingleResult();

		}catch(NoResultException e){
			return null;
		}catch(Exception e){
			log.error(e);
			throw new InfraException(e);
		}
	}

	public void excluir(final Cidade cidade){
		try{
			final Cidade p = manager.find(Cidade.class, cidade.getCodigo());
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