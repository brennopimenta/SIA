package br.gov.go.pm.dao;

import br.gov.go.pm.modelo.ArmaNumeroSigma;
import br.gov.go.pm.util.exception.NegocioException;
import br.gov.go.pm.util.exception.InfraException;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class BaseNumeroSigmaDAO {

	private Logger log = Logger.getLogger(getClass());

	@PersistenceContext
	private EntityManager manager;

	public ArmaNumeroSigma buscarPeloCodigo(Long codigo) {
		return manager.find(ArmaNumeroSigma.class, codigo);
	}
	
	public void salvar(ArmaNumeroSigma armaNumeroSigma) throws NegocioException{
		manager.merge(armaNumeroSigma);
	}
	
	@Transactional
	public void excluir(ArmaNumeroSigma numeroSigmaSelecionado) throws Exception{
		
		ArmaNumeroSigma numeroSigmaSelecionadoTemp = manager.find(ArmaNumeroSigma.class, numeroSigmaSelecionado.getCodigo());
		manager.remove(numeroSigmaSelecionadoTemp);
		manager.flush();
			
	}

	@SuppressWarnings("unchecked")
	public List<ArmaNumeroSigma> buscarTodos() {
		return manager.createQuery("from ArmaNumeroSigma").getResultList();
		
    }

    /** retorna se existe o numero da arma cadastrado no ArmaSigmaNumero retornando este tipo, possibilitando verificar para
	 * qual sigma a arma está cadastrada*/
	public ArmaNumeroSigma buscarArmaSelecionada(String arma){
		try{
			List<ArmaNumeroSigma> lista = manager.createNamedQuery("ArmaNumeroSigma.buscarArmaSelecionada", br.gov.go.pm.modelo.ArmaNumeroSigma.class)
					.setParameter("arma", arma)
					.getResultList();
		return manager.createNamedQuery("ArmaNumeroSigma.buscarArmaSelecionada", br.gov.go.pm.modelo.ArmaNumeroSigma.class)
				.setParameter("arma", arma)
				.getSingleResult();
		}catch(NoResultException e){
			return null;
		}catch(NullPointerException n){
			return null;
			
		}
	}
	
	/**busca realizada na pesquisa*/
	
	public List<ArmaNumeroSigma> buscarPelaArma(String arma, String sigma){
		List<ArmaNumeroSigma> baseSigma = null;
		if((arma == "" && sigma != "") || (sigma == "" && arma != "")){
		baseSigma = manager.createNamedQuery("ArmaNumeroSigma.buscarPorArmaOuNumeroSigma", ArmaNumeroSigma.class)  
				.setParameter("arma", arma)
				.setParameter("sigma", sigma)
				.getResultList();
		}else if (arma != "" && sigma != ""){
			baseSigma = manager.createNamedQuery("ArmaNumeroSigma.buscarPelaArmaENumeroSigma", ArmaNumeroSigma.class)  
					.setParameter("arma", arma)
					.setParameter("sigma", sigma)
					.getResultList();
		}
		return baseSigma;
	
	}

	public Optional<ArmaNumeroSigma> buscaPorSigmaExistente(String sigma){
		try {
		return Optional.ofNullable((ArmaNumeroSigma) manager.createQuery("SELECT ns FROM ArmaNumeroSigma ns WHERE ns.numeroSigma =:sigma")
				.setParameter("sigma", sigma).getSingleResult());
		}catch(NoResultException e){
			return Optional.empty();
		} catch (Exception e) {
			log.debug("Falha na busca por usuario para login!");
			throw new InfraException(e);
		}
	}


}//FIM
