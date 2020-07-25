package br.gov.go.pm.dao;

import br.gov.go.pm.modelo.Grupo;
import br.gov.go.pm.modelo.Usuario;
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
public class GrupoDAO {

    @PersistenceContext
    private EntityManager repository;

    private Logger log = Logger.getLogger(getClass());

    public Grupo buscarPeloCodigo(final Long codigo) {
        try {
            return repository.find(Grupo.class, codigo);
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            log.error(e);
            throw new InfraException(e);
        }
    }

    public List<Grupo> list(){
        try{
            return repository.createQuery("select u from Grupo u ").getResultList();
        }catch(Exception e){
            System.out.println("Falha na busca por usuários!");
            throw e;
        }
    }

   public List<Grupo> buscaGruposPorParametro(String nome) {

        List<Grupo> Grupos = null;
        try {
            /**busca todos por nome*/
            if ((nome != "")) {
                Grupos = repository.createQuery("SELECT a FROM Grupo a WHERE a.nome LIKE :nome")
                        .setParameter("nome", "%" + nome + "%")
                        .getResultList();
            }

        } catch (NoResultException e) {
            return null;
        } catch (NullPointerException n) {
            return null;

        }

        return Grupos;

    }

    public Optional<Grupo> buscaGrupoEspecifico(String nome) {
        try {
            return Optional.ofNullable((Grupo) repository.createQuery("select g from Grupo g where g.nome = :nome")
                    .setParameter("nome", nome)
                    .getSingleResult());
        }catch(NoResultException e){
            return Optional.empty();
        } catch (Exception e) {
            log.debug("Falha na busca por Grupo específico!");
            throw new InfraException(e);
        }
    }

    public void salvar(Grupo grupo) throws Exception {
        try {
            repository.merge(grupo);
        } catch (Exception e) {
            log.error(e);
            throw new InfraException(e);

        }
    }

    @Transactional
    public void excluir(final Grupo grupo) {
        try {

            final Grupo user = repository.find(Grupo.class, grupo.getCodigo());
            repository.remove(user);
            repository.flush();

        } catch (NoResultException e) {
            log.debug("Não encontrado grupo para exclusão!");
        } catch (Exception e) {
            log.error(e);
            throw new InfraException(e);
        }
    }



} //end class

