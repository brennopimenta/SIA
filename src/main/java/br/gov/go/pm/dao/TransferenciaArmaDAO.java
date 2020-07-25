package br.gov.go.pm.dao;

import br.gov.go.pm.modelo.ArmaTransferencia;
import br.gov.go.pm.util.exception.InfraException;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class TransferenciaArmaDAO {

	@PersistenceContext
	private EntityManager repository;

	private Logger log = Logger.getLogger(getClass());

	public List<ArmaTransferencia> buscarTodas(){
		try{
		return repository.createQuery("from ArmaTransferencia").getResultList();
		}catch(Exception e){
			log.error(e);
			throw new InfraException(e.getMessage());
		}
	}

	public ArmaTransferencia buscarPeloCodigo(Long codigo) {
		try {
		return repository.find(ArmaTransferencia.class, codigo);
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			log.error(e);
			throw new InfraException(e);
		}
	}
	
	public void salvar(ArmaTransferencia armaTransferencia) {
		try {
			repository.merge(armaTransferencia);
		} catch (Exception e) {
			log.error(e);
			throw new InfraException(e);

		}
				
	}

	/** busca transferências por arma*/
	public List<ArmaTransferencia> buscarTransferenciasPorArma(String arma, String cpf){
		List<ArmaTransferencia> armas = null;
		try{
		if ((arma == "" && cpf != "") || (cpf == "" && arma != "")){
		armas = repository.createNamedQuery("ArmaTransferencia.buscarTransferenciasPorArmaOuProp", ArmaTransferencia.class)
				.setParameter("arma", arma)
				.setParameter("cpf", cpf)
				.getResultList();
		
		}else if (arma != "" && cpf != ""){
			armas = repository.createNamedQuery("ArmaTransferencia.buscarTransferenciasPorArmaEProp", ArmaTransferencia.class)
					.setParameter("arma", arma)
					.setParameter("cpf", cpf)
  					.getResultList();
		}

			return armas;

		} catch (Exception e) {
			log.error(e);
			throw new InfraException(e);

		}
	}
	
}//fim
