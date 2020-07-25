package br.gov.go.pm.dao;

import br.gov.go.pm.enuns.StatusCraf;
import br.gov.go.pm.modelo.Craf;
import br.gov.go.pm.util.exception.NegocioException;
import br.gov.go.pm.util.exception.InfraException;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Repository
public class CrafDAO {

	private Logger log = Logger.getLogger(getClass());

	@PersistenceContext
	private EntityManager manager;
	
	public Craf buscarPeloCodigo(Long codigo) {
		return manager.find(Craf.class, codigo);
	}
	
	public void salvar(Craf craf) throws NegocioException {
		try {
			manager.merge(craf);

		}catch(Exception e) {
			log.error(e);
			throw new NegocioException(" problema " + e.getMessage() + " ao salvar no banco de dados.");
		}
	}

	public List<Craf> buscarTodos() {
	  return manager.createNamedQuery("Craf.buscarTodos").getResultList();
	}
	
	/**busca os Craf não assinados*/
	public List<Craf> buscarTodosNaoAssinados() {
		  try{
		  
			  return manager.createNamedQuery("Craf.buscarTodosNaoAssinados", Craf.class).getResultList();
		  
		  }catch(NoResultException e){
				return null;
			}catch(NullPointerException n){
				return null;
				
			}
		}

	/**busca os Craf não assinados*/
	public List<Craf> buscarTodosAssinados() {
		  try{
		  
			  return manager.createNamedQuery("Craf.buscarTodosAssinados", Craf.class).getResultList();
		  
		  }catch(NoResultException e){
				return null;
			}catch(NullPointerException n){
				return null;
			}
		}
	
	/**busca os Craf não assinados e NÃO Impressos*/
	public List<Craf> buscarTodosAssinadosNaoImpressos() {
		  try{
		  
			  return manager.createNamedQuery("Craf.buscarTodosAssinadosNaoImpressos", Craf.class).getResultList();
		  
		  }catch(NoResultException e){
				return null;
			}catch(NullPointerException n){
				return null;
			}
		}

	/**cancelamento de craf no banco*/
	@Transactional
	public void cancelarCraf(Craf crafSelecionado) throws NegocioException {
		Craf crafTemp = manager.find(Craf.class, crafSelecionado.getCodigo());
		crafTemp.setStatus(StatusCraf.CANCELADO);
		salvar(crafTemp);
	
	}
	
	/**utilizado em cadastroCrafBean (e outros)*/
	public Craf buscarCrafPorArma(String numeroArma){
		try{
		return manager.createNamedQuery("Craf.buscarCrafPorArma", Craf.class)  
				.setParameter("numeroArma", numeroArma)
				.getSingleResult();
		}catch(NoResultException e){
			return null;
		}catch(NullPointerException n){
			return null;
			
		}
	}

	 public Optional<Craf> verficaSeExisteCrafParaArma(String numeroArma) {

		try{
			return Optional.of(manager.createNamedQuery("Craf.buscarCrafPorArma", Craf.class)
					.setParameter("numeroArma", numeroArma)
					.getSingleResult());
		}catch(NoResultException e) {
			return Optional.empty();

		/**neste caso há dois crafs, sendo um não cancelado, para a mesma arama (provavelmente por um erro
		 * na inserção ou adulteração de dados) retorna um novo craf para que
		 * não seja incluso na carga para gerar craf e sim na de erros*/
		}catch (NonUniqueResultException nu){
			return Optional.of(new Craf());
		}catch(Exception e){
			log.error(e);
			throw new InfraException(e);
		}

	}

	/**utilizado no pesquisaCrafBean onde retorna uma lista de Craf pelos critérios*/
	public List<Craf> buscarTodosCrafPorArmaOuNumeroCraf(String busca){
		try {
			return manager.createQuery("select c from Craf c WHERE c.arma.numeroArma LIKE :busca or c.numeroCraf LIKE :busca")
					.setParameter("busca", "%" + busca + "%")
					.getResultList();
		} catch (Exception e) {
			log.error(e.getMessage(), e.getCause());
			return null;
		}
	}

/**utilizado no pesquisaCrafBean onde retorna uma lista de Craf PENDENTES pelos critérios*/
public List<Craf> buscarTodosCrafPendentesPorArmaOuNumeroCraf(String busca){
	try {
		return manager.createQuery("select c from Craf c WHERE c.arma.numeroArma LIKE :busca and c.status = br.gov.go.pm.enuns.StatusCraf.PENDENTE " +
				"or c.numeroCraf LIKE :busca and c.status = br.gov.go.pm.enuns.StatusCraf.PENDENTE")
				.setParameter("busca", "%" + busca + "%")
				.getResultList();
	} catch (Exception e) {
		log.error(e.getMessage(), e.getCause());
		return null;
	}
}

/**utilizado no pesquisaCrafBean onde retorna uma lista de Craf ASSINADOS pelos critérios*/
public List<Craf> buscarTodosCrafAssinadosPorArmaOuNumeroCraf(String busca){
	try {
		return manager.createQuery("select c from Craf c WHERE c.arma.numeroArma LIKE :busca and c.status = br.gov.go.pm.enuns.StatusCraf.ASSINADO " +
				"or c.numeroCraf LIKE :busca and c.status = br.gov.go.pm.enuns.StatusCraf.ASSINADO")
				.setParameter("busca", "%" + busca + "%")
				.getResultList();
	} catch (Exception e) {
		log.error(e.getMessage(), e.getCause());
		return null;
	}
}

public Optional<Craf> buscarCrafPorNumeroCraf(String numeroCraf) {
		try {
			return Optional.ofNullable((Craf) manager.createQuery("select c from Craf c where c.numeroCraf = :numeroCraf")
					.setParameter("numeroCraf", numeroCraf)
					.getSingleResult());
		}catch(NoResultException e){
			return Optional.empty();
		} catch (Exception e) {
			log.debug("Falha na busca por craf pelo número!");
			throw new InfraException("Falha na busca por craf pelo número!" + e);
		}
	}

public String ultimoNumeroCraf(){
	try{
		return (String) manager.createQuery("SELECT MAX(c.numeroCraf) FROM Craf c").getSingleResult();
	}catch(NoResultException e){
		log.debug(e);
		throw new InfraException("Falha na busca por craf pelo número!" + e);
	}catch(NullPointerException n){
		log.debug(n);
		throw new InfraException("Falha na busca por craf pelo número!" + n);

	}
}


}//fim



