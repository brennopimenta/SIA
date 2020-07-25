package br.gov.go.pm.dao;

import br.gov.go.pm.enuns.Profile;
import br.gov.go.pm.modelo.Assinaturas;
import br.gov.go.pm.util.exception.NegocioException;
import br.gov.go.pm.util.exception.InfraException;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class AssinaturaDAO {

	private Logger log = Logger.getLogger(getClass());

	@PersistenceContext
	private EntityManager em;

	private List<Assinaturas> assinaturas = new ArrayList<>();

	public void salvar(Assinaturas assinatura) throws InfraException {
		try {
			em.merge(assinatura);
		} catch (Exception e) {
			log.error(e);
			throw new InfraException(e);

		}
	}

	@Transactional
	public void excluir(final Assinaturas assinatura) throws NoResultException, Exception {
		try {

			final Assinaturas as = em.find(Assinaturas.class, assinatura.getCodigo());
			em.remove(as);
			em.flush();

		} catch (NoResultException e) {
			log.debug("Não encontrado item para exclusão!");
		} catch (Exception e) {
			log.error(e);
			throw new InfraException(e);
		}
	}


	public Assinaturas buscarPeloCodigo(final Long codigo) {
		try {
			return em.find(Assinaturas.class, codigo);
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			log.error(e);
			throw new InfraException(e);
		}
	}

	/**
	 * busca todas as Assinaturas
	 */
	public List<Assinaturas> buscarTodos() {
		try {
			return em.createQuery("select a from Assinaturas a").getResultList();

		} catch (Exception e) {
			log.error(e);
			throw new InfraException(e);
		}

	}

	/**verifica se existe uma assinatura ativa para determinado usuário (mais utilizado para o ato de assinar. )
	 * @param cpf*/
	public Assinaturas verificaAssinaturaExistente(String cpf) {

		try {
			return em.createQuery("select a from Assinaturas a where a.status = 'ATIVO' and a.usuario.cpf = :cpf", Assinaturas.class)
					.setParameter("cpf", cpf)
					.getSingleResult();
		}catch(NoResultException e){
			return null;
		} catch (Exception e) {
			log.error(e.getMessage(), e.getCause());
			return null;

		}

	}

	/**verifica se existe algum comandante ou autoridade delegada ativos
	 * @param profile*/
	public Optional<Assinaturas> verificaAutoridadeExistente(Profile profile) {

		try {
			return Optional.ofNullable((Assinaturas) em.createQuery("select a from Assinaturas a where a.status = 'ATIVO' and a.usuario.profile = :profile")
					.setParameter("profile", profile)
					.getSingleResult());
		}catch(NoResultException e){
			return Optional.empty();
		} catch (Exception e) {
			log.debug("Falha na busca por usuario para login!");
			throw new InfraException(e);
		}

	}


}//fim classe



	

