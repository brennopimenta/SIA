package br.gov.go.pm.dao;

import br.gov.go.pm.modelo.GrupoCalibre;
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
public class GrupoCalibreDAO {

    @PersistenceContext
    private EntityManager repository;

    private Logger log = Logger.getLogger(getClass());

    public GrupoCalibre buscarPeloCodigo(final Long codigo) {
        try {
            return repository.find(GrupoCalibre.class, codigo);
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            log.error(e);
            throw new InfraException(e);
        }
    }

    public List<GrupoCalibre> list(){
        try{
            return repository.createQuery("select u from GrupoCalibre u ").getResultList();
        }catch(Exception e){
         log.error(e.getMessage(), e.getCause());
           return null;
        }
    }

   public List<GrupoCalibre> buscaGrupoCalibresPorParametro(String nome) {

        List<GrupoCalibre> GrupoCalibres = null;
        try {
            /**busca todos por nome*/
            if ((nome != "")) {
                GrupoCalibres = repository.createQuery("SELECT a FROM GrupoCalibre a WHERE a.nome LIKE :nome")
                        .setParameter("nome", "%" + nome + "%")
                        .getResultList();
            }

        } catch (NoResultException e) {
            return null;
        } catch (NullPointerException n) {
            return null;

        }

        return GrupoCalibres;

    }

    public Optional<GrupoCalibre> buscaGrupoCalibreEspecifico(String nome) {
        try {
            return Optional.ofNullable((GrupoCalibre) repository.createQuery("select g from GrupoCalibre g where g.nome = :nome")
                    .setParameter("nome", nome)
                    .getSingleResult());
        }catch(NoResultException e){
            return Optional.empty();
        } catch (Exception e) {
            log.debug("Falha na busca por GrupoCalibre específico!");
            throw new InfraException(e);
        }
    }

    public void salvar(GrupoCalibre GrupoCalibre) throws Exception {
        try {
            repository.merge(GrupoCalibre);
        } catch (Exception e) {
            log.error(e);
            throw new InfraException(e);

        }
    }

    @Transactional
    public void excluir(final GrupoCalibre GrupoCalibre) throws InfraException {
        try {

            final GrupoCalibre user = repository.find(GrupoCalibre.class, GrupoCalibre.getCodigo());
            repository.remove(user);
            repository.flush();

        } catch (NoResultException e) {
            log.debug("Não encontrado GrupoCalibre para exclusão!");
        } catch (Exception e) {
            log.error(e);
            throw new InfraException(e);
        }
    }



} //end class

