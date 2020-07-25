package br.gov.go.pm.dao;

import br.gov.go.pm.modelo.EmailConfig;
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
public class EmailDAO {

    @PersistenceContext
    private EntityManager repository;

    private Logger log = Logger.getLogger(getClass());

    public EmailConfig buscarPeloCodigo(final Long codigo) {
        try {
            return repository.find(EmailConfig.class, codigo);
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            log.error(e);
            throw new InfraException(e);
        }
    }

    public List<EmailConfig> list(){
        try{
            return repository.createQuery("select u from EmailConfig u ").getResultList();
        }catch(Exception e){
            System.out.println("Falha na busca por config email!");
            throw e;
        }
    }

   public void salvar(EmailConfig emailConfig) {
        try {
            repository.merge(emailConfig);
        } catch (Exception e) {
            log.error(e);
            throw new InfraException(e);

        }
    }

    @Transactional
    public void excluir(final EmailConfig EmailConfig) {
        try {

            final EmailConfig emailConfig = repository.find(EmailConfig.class, EmailConfig.getCodigo());
            repository.remove(emailConfig);
            repository.flush();

        } catch (NoResultException e) {
            log.debug("Não encontrado Configuração de email para exclusão!");
        } catch (Exception e) {
            log.error(e);
            throw new InfraException(e);
        }
    }

    /*busca apenas item pois só existe ele no banco de dados*/
    public EmailConfig buscarConfigEnvioEmail() {
        try {
            return repository.createQuery("select s from EmailConfig s", EmailConfig.class).getSingleResult();

        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            log.error(e.getMessage(), e.getCause());
            throw new InfraException(e);
        }

    }
} //end class

