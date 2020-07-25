package br.gov.go.pm.dao;

import br.gov.go.pm.modelo.MarcaArma;
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
public class MarcaArmaDAO {


    private Logger log = Logger.getLogger(getClass());

    @PersistenceContext
    private EntityManager em;

    public Optional<MarcaArma> buscarPeloCodigo(Long codigo) {

        try{
            return Optional.of(em.find(MarcaArma.class, codigo));
        }catch(NoResultException e){
            return Optional.empty();
        }catch(Exception e){
            log.error(e);
            throw new InfraException(e);
        }
    }

    public void salvar(MarcaArma marcaArma){

        try{
            em.merge(marcaArma);
        }catch(Exception e){
            log.error(e);
            throw new InfraException(e);
        }

    }

    /*busca todos as MarcaArmas*/
    public List<MarcaArma> buscarTodos() {
        return em.createQuery("from MarcaArma").getResultList();
    }

    @Transactional
    public void excluir(final MarcaArma marcaArma) throws NegocioException {

        try{
            final MarcaArma m = em.find(MarcaArma.class, marcaArma.getCodigo());
            em.remove(m); em.flush();
        }catch(NoResultException e){
            log.debug("not found");
        }catch(Exception e){
            log.error(e);
            throw new InfraException(e);
        }

    }


}