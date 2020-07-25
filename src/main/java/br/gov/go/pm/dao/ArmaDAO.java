package br.gov.go.pm.dao;

import br.gov.go.pm.modelo.Arma;
import br.gov.go.pm.modelo.Usuario;
import br.gov.go.pm.util.exception.InfraException;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Repository
public class ArmaDAO {


	private Logger log = Logger.getLogger(getClass());

	@PersistenceContext
	private EntityManager manager;

	public Arma buscarPeloCodigo(Long codigo) {

		try{
			return manager.find(Arma.class, codigo);
		}catch(NoResultException e){
			return null;
		}catch(Exception e){
			log.error(e);
			throw new InfraException(e);
		}

	}

	public void salvar(Arma arma) {

		try{
			manager.merge(arma);
		}catch(Exception e) {
			log.error(e);
			throw new InfraException(e);
//		}catch (SqlExceptionHelper ex){
//
		}
	}

	//busca todas as Armas
	@SuppressWarnings("unchecked")
	public List<Arma> buscarTodos() {
			return manager.createQuery("from Arma").getResultList();
	}

	public List<String> buscarArmaPeloNumero(String arma){
		try{

			return manager.createNamedQuery("Arma.buscarNumeroArma", String.class)
				.setParameter("arma", arma)
				.getResultList();

		}catch(NoResultException e){
			log.error(e.getMessage(), e.getCause());
			return null;
		}catch(NullPointerException n){
			log.error(n.getMessage(), n.getCause());
			return null;

		}
	}
	/** pesquisa armas pelo numero. Usada em diversas listas como pesquisa de Arma*/
	public List<Arma> buscarArmaEspecifica(String arma){
		return manager.createNamedQuery("Arma.buscarArmaEspecifica", Arma.class)
				.setParameter("arma", arma)
				.getResultList();

	}

	public List<Arma> buscarArmasPorProprietario(String cpf) {
		List<Arma> arma;
		try{

			arma =  manager.createNamedQuery("Arma.buscarArmaPorProprietario",Arma.class)
					.setParameter("cpf", cpf)
					.getResultList();
			return arma;

			}catch(NoResultException e){
				return null;
			}catch(NullPointerException n){
				return null;

			}
	}

	/* ****************************************************************/

	/** busca todos os proprietarios de Arma e agrupa-os* para a lista de proprietarios*/
	public List<Arma> buscarProprietarios() {

		try {
			List<Arma> lista = manager.createNamedQuery("Arma.buscarTodosProprietariosArma").getResultList();

			HashSet<Object> armasProp = new HashSet<>();
			lista.removeIf(e -> !armasProp.add(e.getCpfNovoProprietario()));
			return lista;

		}catch(NoResultException e){
		return null;

		}catch(NullPointerException n){
		return null;

	}

	}

	public List<Arma> buscarArmasSemProprietarios() {

		List<Arma> lista = manager.createQuery("SELECT a FROM Arma a WHERE a.cpfNovoProprietario = null or a.cpfNovoProprietario = '0'").getResultList();
		/**remove todos proprietários repetidos.*/
		HashSet<Object> armasProp =new HashSet<>();
		lista.removeIf(e->!armasProp.add(e.getCpfNovoProprietario()));
		return lista;

	}


	public List<Arma> buscarArmasComRestricao() {

		try{
		return manager.createQuery("SELECT a FROM Arma a WHERE a.enunsArmas.statusRestritivo <> null").getResultList();

		}catch(NoResultException e){
			return null;

		}catch(NullPointerException n){
			return null;

		}
	}

	/* ****************************************************************/

	/** busca todos os proprietarios de Arma por rg OU cpf e agrupa-os* para a lista de proprietarios*/
	public List<Arma> buscarProprietariosEspecificos(String rgProprietario, String cpfNovoProprietario) {
		List<Arma> armas = null;
		HashSet<Object> armasProp =new HashSet<>();
		try{

			if ((rgProprietario != "" && cpfNovoProprietario == "")){
				armas = manager.createNamedQuery("Arma.buscarTodosProprietariosArmaPorRg", Arma.class)
						.setParameter("rg", rgProprietario)
						.getResultList();

				armas.removeIf(e->!armasProp.add(e.getRgProprietario()));


			}else if (rgProprietario == "" && cpfNovoProprietario != ""){
				armas = manager.createNamedQuery("Arma.buscarTodosProprietariosArmaPorCpf", Arma.class)
						.setParameter("cpf", cpfNovoProprietario)
						.getResultList();
			}else if (rgProprietario != "" && cpfNovoProprietario != ""){
				armas = manager.createNamedQuery("Arma.buscarTodosProprietariosArmaPorRg", Arma.class)
						.setParameter("rg", rgProprietario)
						.getResultList();
				armas.removeIf(e->!armasProp.add(e.getCpfNovoProprietario()));
			}

			}catch(NoResultException e){
				return null;
			}catch(NullPointerException n){
				return null;

			}
		return armas;
	}

	public List<Arma> buscarArmasSemSigma(){
		List<Arma> lista = manager.createQuery("select a from Arma a where a.numeroSigma = null and (a.cpfNovoProprietario <> null or a.cpfNovoProprietario <> '0')").getResultList();
		return lista;
	}

	public Optional<Arma> buscarSigmaExistenteEmArma(String numeroSigma) {
		try {
			return Optional.ofNullable((Arma) manager.createQuery("select a from Arma a where a.numeroSigma = :numeroSigma")
					.setParameter("numeroSigma", numeroSigma)
					.getSingleResult());
		}catch(NoResultException e){
			return Optional.empty();
		} catch (Exception e) {
			log.debug("Falha na busca da arma pelo sigma!");
			throw new InfraException(e);
		}
	}


}//fim


/*Exemplo para chave composta:
	yourList.removeIf(e -> !seen.add(Arrays.asList(e.getFirstKeyPart(), e.getSecondKeyPart()));*/


//exemplo com java 8, removendo da lista os que têm sigma
//		HashSet<Object> buscarArmasSemSigma =new HashSet<>();
//		lista.removeIf(e-> e.getNumeroSigma() != null);


	/*@SuppressWarnings("unchecked")
	public List<Arma> buscarComPaginacao(int first, int pageSize) {
		return manager.createNamedQuery("Arma.buscarTodas")
				.setFirstResult(first)
				.setMaxResults(pageSize)
				.getResultList();

	}
*/
	/*public Optional<Arma> buscarPeloCodigo(Long codigo) {

		try{
			return Optional.of(manager.find(Arma.class, codigo));
		}catch(NoResultException e){
			return Optional.empty();
		}catch(Exception e){
			log.error(e);
			throw new InfraException(e);
		}

	}*/



