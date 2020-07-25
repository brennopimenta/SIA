package br.gov.go.pm.dao;

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
public class UserDAO {

    @PersistenceContext
    private EntityManager repository;

    private Logger log = Logger.getLogger(getClass());

    public Usuario buscarPeloCodigo(final Long codigo) {
        try {
            return repository.find(Usuario.class, codigo);
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            log.error(e);
            throw new InfraException(e);
        }
    }

    public List<Usuario> list(){
        try{
            return repository.createQuery("select u from Usuario u ").getResultList();
        }catch(Exception e){
            System.out.println("Falha na busca por usuários!");
            throw e;
        }
    }

    /**preenche as opções de usuários para assinarua*/
    public List<Usuario> buscaUsuarioParaAssinatura() {
        try {
            return repository.createQuery("select u from Usuario u where u.profile = 'CMT' and u.status = 'ATIVO' or u.profile = 'AUTORIDADE_DELEGADA' and u.status = 'ATIVO'").getResultList();
        } catch (Exception e) {
            log.error("Falha na busca por usuários!");
            throw new InfraException(e);
        }
    }

    /** busca lista de usuario por cpf por parâmetro */
    public List<Usuario> buscarUsuarioPorCpf(String cpf) {
        try {
            return repository.createQuery("select u from Usuario u where u.cpf = :cpf ")
                    .setParameter("cpf", cpf)
                    .getResultList();
        } catch (Exception e) {
            log.debug("Falha na busca por usuários!");
            throw new InfraException(e);
        }

    }

    public Optional<Usuario> usuarioAtivoPorCpf(String cpf) {
        try {
            return Optional.ofNullable((Usuario) repository.createQuery("select u from Usuario u where u.cpf = :cpf and u.status = 'ATIVO'")
                    .setParameter("cpf", cpf)
                    .getSingleResult());
        }catch(NoResultException e){
            return Optional.empty();
        } catch (Exception e) {
            log.debug("Falha na busca por usuario para login!");
            throw new InfraException(e);
        }
    }

    /**serve para comandante ativo*/
    public Usuario buscaCmtAtivo() {
        try {
            return repository.createQuery("select u from Usuario u where u.profile = 'CMT' and u.status = 'ATIVO'", Usuario.class)
                    .getSingleResult();
        }catch(NoResultException e){
            return null;
        } catch (Exception e) {
            log.debug("Falha na busca por Comandante Ativo!" + e.getMessage(), e);
            throw new InfraException(e);
        }
    }

    /**serve para comandante ativo*/
    public List<Usuario> buscaDadosAutoridade() {
        try {
            return repository.createQuery("select u from Usuario u where u.profile = 'CMT' and u.status = 'ATIVO' or u.profile = 'AUTORIDADE_DELEGADA' and u.status = 'ATIVO'").getResultList();
        } catch (Exception e) {
            log.error("Falha na busca por dados de autoridades!");
            throw new InfraException(e);
        }
    }


    public Optional<Usuario> usuarioPorCpf(String cpf) {
        try {
            return Optional.ofNullable((Usuario) repository.createQuery("select u from Usuario u where u.cpf = :cpf ")
                    .setParameter("cpf", cpf)
                    .getSingleResult());
        }catch(NoResultException e){
            return Optional.empty();
        } catch (Exception e) {
            log.debug("Falha na busca por usuario!");
            throw new InfraException(e);
        }
    }

    /**verifica se algum usuário usa algum grupo solicitado por userService*/
    public Optional<Long> buscaGrupoUtilizadoPorUser(Long codigo) {
        try {
            return Optional.ofNullable((Long) repository.createQuery("select g.codigo from Usuario u JOIN u.grupos g where g.codigo = :codigo")
                    .setParameter("codigo", codigo)
                    .getSingleResult());
        }catch(NoResultException e){
            return Optional.empty();
        } catch (Exception e) {
            log.debug("Falha na busca por Grupo no Usuario!");
            throw new InfraException(e);
        }
    }

    public List<Usuario> buscaUsuariosEspecificos(String nome, String cpf) {

        List<Usuario> usuarios = null;
        try {
            /**busca todos por nome*/
            if ((nome != "" && cpf == "")) {
                usuarios = repository.createQuery("SELECT a FROM Usuario a WHERE a.nome LIKE :nome")
                        .setParameter("nome", "%" + nome + "%")
                        .getResultList();
                /**busca todos por cpf*/
            } else if (nome == "" && cpf != "") {
                usuarios = repository.createQuery("SELECT a FROM Usuario a WHERE a.cpf LIKE :cpf")
                        .setParameter("cpf", "%" + cpf + "%")
                        .getResultList();
                /**busca todos por nome e cpf*/
            } else if (nome != "" && cpf != "") {
                usuarios = repository.createQuery("SELECT a FROM Usuario a WHERE a.cpf LIKE :cpf and a.nome LIKE :nome")
                        .setParameter("nome", "%" + nome + "%")
                        .setParameter("cpf", "%" + cpf + "%")
                        .getResultList();
            }

        } catch (NoResultException e) {
            return null;
        } catch (NullPointerException n) {
            return null;

        }

        return usuarios;

    }

    public void salvar(Usuario usuario) throws Exception {
        try {
            repository.merge(usuario);
        } catch (Exception e) {
            log.error(e);
            throw new InfraException(e);

        }
    }

    @Transactional
    public void excluir(final Usuario usuario) {
        try {

            final Usuario user = repository.find(Usuario.class, usuario.getCodigo());
            repository.remove(user);
            repository.flush();

        } catch (NoResultException e) {
            log.debug("Não encontrado usuário para exclusão!");
        } catch (Exception e) {
            log.error(e);
            throw new InfraException(e);
        }
    }



} //end class

