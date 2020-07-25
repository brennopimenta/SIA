package br.gov.go.pm.dao;

import br.gov.go.pm.modelo.Sigma;
import br.gov.go.pm.util.exception.InfraException;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class SigmaDAO {

	private Logger log = Logger.getLogger(getClass());

	@PersistenceContext
	private EntityManager manager;
	
	public Sigma buscarPeloCodigo(Long codigo){
		return manager.find(Sigma.class, codigo);
	}
	
	@SuppressWarnings("unchecked")
	public List<Sigma> buscarTodos() {
			return manager.createQuery("select s from Sigma s").getResultList();
	}
	
	public void salvar(Sigma sigma) {

		manager.merge(sigma);
		
	}
	
	public Sigma buscarArmaSelecionada(String armaSigma){
		Sigma retorno;
		try{

		retorno = manager.createNamedQuery("Sigma.buscarArmaSelecionada", Sigma.class)
				.setParameter("armaSigma", armaSigma)
				.getSingleResult();
		return retorno;
		}catch(NoResultException e){
			log.error(e);
			return null;
		}catch(NullPointerException n){
			log.error(n);
			return null;
			
		}
	}

	public List<Sigma> buscarProcessosSigma(String numeroArma, Long codigo) {
		List<Sigma> sigmas = null;
		
		if ((numeroArma == "" && (codigo != null || codigo !=0)) || ((codigo == null || codigo ==0) && numeroArma != "")){
			sigmas = manager.createNamedQuery("Sigma.buscarSigmasPorArmaOuCodigo", Sigma.class)  
				.setParameter("numeroArma", numeroArma)
				.setParameter("codigo", codigo)
				.getResultList();
		
		}else if (numeroArma != "" && (codigo != null || codigo !=0)){
			sigmas = manager.createNamedQuery("Sigma.buscarSigmasPorArmaECodigo", Sigma.class)  
					.setParameter("numeroArma", numeroArma)
					.setParameter("codigo", codigo)
					.getResultList();
			
			
		}
		return sigmas;
	}

	/**busca todos processos de sigma, com suas armas, cuja arma não tenha numero de sigma
	 * e não tenha sido gerada uma carga para ela.*/
	public List<Sigma> buscaSigmasComArmasSemNumeroSigma() {
		try {
			 return manager.createQuery("select s from Sigma s JOIN s.arma a where a.numeroSigma = null and a.enunsArmas.statusCarga = null")
					.getResultList();
		}catch(NoResultException e){
			return null;
		} catch (Exception e) {
			log.debug("Falha na busca por sigmas cuja armas estão sem nº sigma!");
			throw new InfraException(e);
		}
	}

}
