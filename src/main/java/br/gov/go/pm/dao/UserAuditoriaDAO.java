package br.gov.go.pm.dao;

import br.gov.go.pm.modelo.Usuario;
import br.gov.go.pm.modelo.UsuarioAuditoria;
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
public class UserAuditoriaDAO {

    @PersistenceContext
    private EntityManager repository;

    private Logger log = Logger.getLogger(getClass());

    public UsuarioAuditoria buscarPeloCodigo(final Long codigo) {
        try {
            return repository.find(UsuarioAuditoria.class, codigo);
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            log.error(e);
            throw new InfraException(e);
        }
    }

    public List<UsuarioAuditoria> list(){
        try{
            return repository.createQuery("select u from UsuarioAuditoria u ").getResultList();
        }catch(Exception e){
            System.out.println("Falha na busca por usuários!");
            throw e;
        }
    }



    public void salvar(UsuarioAuditoria usuarioAuditoria) throws Exception {
        try {
            repository.merge(usuarioAuditoria);
        } catch (Exception e) {
            log.error(e);
            throw new InfraException(e);

        }
    }

    @Transactional
    public void excluir(final UsuarioAuditoria UsuarioAuditoria) {
        try {

            final UsuarioAuditoria user = repository.find(UsuarioAuditoria.class, UsuarioAuditoria.getCodigo());
            repository.remove(user);
            repository.flush();

        } catch (NoResultException e) {
            log.debug("Não encontrado usuário para exclusão!");
        } catch (Exception e) {
            log.error(e);
            throw new InfraException(e);
        }
    }


    public List<UsuarioAuditoria> buscaAuditoriaEspecificas(String busca) {

        List<UsuarioAuditoria> auditorias = null;
        try {
                /**busca todos por cpf*/

               return repository.createQuery("SELECT a FROM UsuarioAuditoria a WHERE a.cpfUsuario LIKE :busca")
                        .setParameter("busca", "%" + busca + "%")
                        .getResultList();

        } catch (NoResultException e) {
            return null;
        } catch (NullPointerException n) {
            return null;

        }

    }

} //end class

