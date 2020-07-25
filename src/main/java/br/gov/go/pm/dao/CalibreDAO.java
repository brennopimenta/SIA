package br.gov.go.pm.dao;

import br.gov.go.pm.modelo.Calibre;
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
public class CalibreDAO {

    @PersistenceContext
    private EntityManager repository;

    private Logger log = Logger.getLogger(getClass());

    public Calibre buscarPeloCodigo(final Long codigo) {
        try {
            return repository.find(Calibre.class, codigo);
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            log.error(e);
            throw new InfraException(e);
        }
    }

    public List<Calibre> list(){
        try{
            return repository.createQuery("select u from Calibre u ").getResultList();
        }catch(Exception e){
            log.error(e.getMessage(), e.getCause());
            return null;
        }
    }

   public List<Calibre> buscaCalibresPorParametro(String calibre) {

        List<Calibre> Calibres = null;
        try {
            /**busca todos por calibre*/
            if ((calibre != "")) {
                Calibres = repository.createQuery("SELECT a FROM Calibre a WHERE a.calibre LIKE :calibre")
                        .setParameter("calibre", "%" + calibre + "%")
                        .getResultList();
            }

        } catch (NoResultException e) {
            return null;
        } catch (NullPointerException n) {
            return null;

        }

        return Calibres;

    }

    public Optional<Calibre> buscaCalibreEspecifico(String calibre) {
        try {
            return Optional.ofNullable((Calibre) repository.createQuery("select g from Calibre g where g.calibre = :calibre")
                    .setParameter("calibre", calibre)
                    .getSingleResult());
        }catch(NoResultException e){
            return Optional.empty();
        } catch (Exception e) {
            log.debug("Falha na busca por Calibre específico!");
            throw new InfraException(e);
        }
    }

    public void salvar(Calibre Calibre) throws Exception {
        try {
            repository.merge(Calibre);
        } catch (Exception e) {
            log.error(e);
            throw new InfraException(e);

        }
    }

    @Transactional
    public void excluir(final Calibre Calibre) {
        try {

            final Calibre user = repository.find(Calibre.class, Calibre.getCodigo());
            repository.remove(user);
            repository.flush();

        } catch (NoResultException e) {
            log.debug("Não encontrado Calibre para exclusão!");
        } catch (Exception e) {
            log.error(e);
            throw new InfraException(e);
        }
    }



} //end class

