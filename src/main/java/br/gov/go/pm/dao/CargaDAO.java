package br.gov.go.pm.dao;

import br.gov.go.pm.modelo.Carga;
import br.gov.go.pm.modelo.Sigma;
import br.gov.go.pm.util.exception.InfraException;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Repository
public class CargaDAO {

	private Logger log = Logger.getLogger(getClass());

	@PersistenceContext
	private EntityManager em;

	public void salvar(Carga carga) throws InfraException {
		try {
			em.merge(carga);
		} catch (Exception e) {
			log.error(e);
			throw new InfraException(e);

		}
	}

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public void excluir(final Carga carga) {
		try {

			final Carga as = em.find(Carga.class, carga.getCodigo());
			em.remove(as);
			em.flush();

		} catch (NoResultException e) {
			log.debug("Não encontrado carga para exclusão!");
		} catch (Exception e) {
			log.error(e);
			throw new InfraException(e);
		}
	}

	public Carga buscarCargaComArmas(final Long codigo) {
		try {
			return em.createQuery("select c FROM Carga c JOIN fetch c.sigmas a where c.codigo = :codigo", Carga.class)
					.setParameter("codigo", codigo)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			log.error(e);
			throw new InfraException(e);
		}
	}

	public Carga buscar(final String codigo) {
		try {
			return em.createQuery("select c FROM Carga c JOIN fetch c.sigmas a where a.arma.numeroSigma = :codigo", Carga.class)
					.setParameter("codigo", codigo)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			log.error(e);
			throw new InfraException(e);
		}
	}

	public Carga buscarPeloCodigo(final Long codigo) {
		try {
			return em.find(Carga.class, codigo);
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			log.error(e);
			throw new InfraException(e);
		}
	}

	/**
	 * busca todas as Carga
	 */
	public List<Carga> buscarTodos(){
		try {
			return em.createQuery("select c from Carga c").getResultList();

		} catch (Exception e) {
			log.error(e);
			throw new InfraException(e);
		}

	}


	/**busca carga pela arma e monta uma lista*/
	public List<Carga> listarCargaPelaArma(final String arma){
		try {

			return em.createQuery("select c from Carga c JOIN fetch c.sigmas s where s.arma.numeroArma = :arma")
					.setParameter("arma", arma)
					.getResultList();

		} catch (Exception e) {
			log.error(e);
			return null;
		}

	}

	/**busca cargas prontas para craf pela arma*/
	public List<Carga> listarCargaParaCrafPelaArma(final String arma){
		try {
			List<Carga> lista = new ArrayList<>();

			List<Carga> li = em.createQuery("select c from Carga c JOIN fetch c.sigmas " +
					"where c.status = br.gov.go.pm.enuns.StatusCarga.ENVIADA and c.dataGeracaoCraf = null")
					.getResultList();

			HashSet<Object> cargaHash = new HashSet<>();
			li.removeIf(e -> !cargaHash.add(e.getCodigo()));

			li.forEach(l -> l.getSigmas().forEach(sigma -> {if (sigma.getArma().getNumeroArma().equals(arma)){
					lista.add(l);}

			}));

		return lista;

		} catch (Exception e) {
			log.error(e);
			return null;
		}

	}

	/*gera listas de craf para carga*/

	/**busca todas as cargas com os sigmas cuja as armas estejam com nº de sigma e que a carga
	 * tenha sido enviada, e nãoi foi gerado craf para esta carga.*/
	public List<Carga> buscarTodasParaCraf() {
		try {
			List<Carga> novaLista = new ArrayList<>();
			List<Carga> list = em.createQuery("select c from Carga c join fetch c.sigmas s join fetch s.arma a " +
					"where c.status = br.gov.go.pm.enuns.StatusCarga.ENVIADA and c.dataGeracaoCraf = null")
					.getResultList();
			novaLista.addAll(list);

			HashSet<Object> cargaHash = new HashSet<>();
			list.removeIf(e -> !cargaHash.add(e.getCodigo()));

			trabalhaListaSigma(novaLista, list);

			HashSet<Object> carga = new HashSet<>();
			novaLista.removeIf(e -> !carga.add(e.getCodigo()));

			return novaLista;

		} catch (Exception e) {
			log.error(e);
			return null;
		}

	}

	/**busca todas as cargas que foram geradas craf*/
	public List<Carga> buscarCargaComCraf() {
		try {
			List<Carga> lista = em.createQuery("select c FROM Carga c JOIN fetch c.sigmas a where c.dataGeracaoCraf != null")
					.getResultList();

			HashSet<Object> cargasMap =new HashSet<>();
			lista.removeIf(e->!cargasMap.add(e.getCodigo()));

			return lista;
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			log.error(e);
			throw new InfraException(e);
		}
	}

    /**/
	/*gera listas de envio para boletim*/
	public List<Carga> buscarTodasParaEnvioBoletim() {
		try {
			List<Carga> listaNova = new ArrayList<>();
			/**busca todas as cargas que não foram enviadas para boletim e que não foram geradas craf*/
			List<Carga> list = em.createQuery("select c from Carga c join fetch c.sigmas s join fetch s.arma a " +
					"where c.boletimInclusao = null and c.statusEnvioBoletim  = null and c.dataGeracaoCraf = null")
					.getResultList();
			listaNova.addAll(list);

			HashSet<Object> cargaHash = new HashSet<>();
			list.removeIf(e -> !cargaHash.add(e.getCodigo()));

			trabalhaListaSigma(listaNova, list);

			HashSet<Object> carga = new HashSet<>();
			listaNova.removeIf(e -> !carga.add(e.getCodigo()));

			return listaNova;

		} catch (Exception e) {
			log.error(e);
			return null;
		}

	}

	/**busca todas as cargas que foram geradas craf*/
	public List<Carga> buscarCargasEnviadasBoletim() {
		try {
			List<Carga> listaNova = new ArrayList<>();
			/**busca todas as cargas enviadas para boletim e que não foram geradas craf*/
			List<Carga> list = em.createQuery("select c from Carga c join fetch c.sigmas s join fetch s.arma a " +
					"where c.statusEnvioBoletim  != null and c.dataGeracaoCraf = null")
					.getResultList();
			listaNova.addAll(list);

			HashSet<Object> cargaHash = new HashSet<>();
			list.removeIf(e -> !cargaHash.add(e.getCodigo()));

			trabalhaListaSigma(listaNova, list);

			HashSet<Object> carga = new HashSet<>();
			listaNova.removeIf(e -> !carga.add(e.getCodigo()));

			return listaNova;

		} catch (Exception e) {
			log.error(e);
			return null;
		}
	}

	/**busca todas as cargas que foram geradas craf*/
	public List<Carga> buscarCargasComBoletim() {
		try {
			List<Carga> listaNova = new ArrayList<>();
			/**busca todas as cargas com boletim */
			List<Carga> list = em.createQuery("select c from Carga c join fetch c.sigmas s join fetch s.arma a " +
					"where c.boletimInclusao != null")
					.getResultList();
			listaNova.addAll(list);

			HashSet<Object> cargaHash = new HashSet<>();
			list.removeIf(e -> !cargaHash.add(e.getCodigo()));

			trabalhaListaSigma(listaNova, list);

			HashSet<Object> carga = new HashSet<>();
			listaNova.removeIf(e -> !carga.add(e.getCodigo()));

			return listaNova;

		} catch (Exception e) {
			log.error(e);
			return null;
		}
	}

	/**/

	private void trabalhaListaSigma(List<Carga> listaNova, List<Carga> list) {
		for (Carga c : list) {
			List<Sigma> sigmas = c.getSigmas();
			for (Sigma s : sigmas) {
				/**remove todos os codigos repetidos*/
				if (s.getArma().getNumeroSigma() == null) {
					listaNova.removeIf(carga1 -> carga1.getCodigo().equals(c.getCodigo()));
					break;
				}
			}
		}
	}





}//fim classe




//
//	/**consulta com valores agregados*/
//	public Optional<Carga> buscarCargaPelaArma(String numeroArma) {
//		try {
//			return Optional.ofNullable((Carga) em.createQuery("select c from Carga c JOIN c.armas a where a.numeroArma = :numeroArma")
//					.setParameter("numeroArma", numeroArma)
//					.getSingleResult());
//		}catch(NoResultException e){
//			return Optional.empty();
//		} catch (Exception e) {
//			log.debug("Falha na busca por usuario para login!");
//			throw new InfraException(e);
//		}

//	}




	

