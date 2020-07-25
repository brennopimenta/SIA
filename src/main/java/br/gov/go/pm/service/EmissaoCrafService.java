package br.gov.go.pm.service;

import br.gov.go.pm.dao.CrafDAO;
import br.gov.go.pm.dao.UserDAO;
import br.gov.go.pm.modelo.Craf;
import br.gov.go.pm.modelo.Usuario;
import br.gov.go.pm.util.exception.NegocioException;
import br.gov.go.pm.util.jsf.FacesUtil;
import br.gov.go.pm.utilitarios.FileResource;
import br.gov.go.pm.utilitarios.ImageResource;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;
import java.awt.*;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmissaoCrafService {

    private Logger log = Logger.getLogger(getClass());

    @Autowired
    private CrafDAO crafDAO;

    @Autowired
    private UserDAO userDAO;

    public List<Craf> converteAssinaturaAutoridade(List<Craf> listaCrafImprimir) throws NoResultException {
        List<Craf> novaListaCrafImprimir = new ArrayList<>();

        listaCrafImprimir.forEach(l -> {
            Craf craf;
            Image imgAssinatura = null;

            try {
                craf  = l;
                /**Se existe a imagem de assinatura, que é um array de byte, a converte em InputStream*/
                if (l.getAutorAssinatura() != null) {
                    InputStream in = FileResource.getInputStream(l.getAutorAssinatura().getAssinaturaImg());
                    imgAssinatura = ImageResource.getMakeImage(in);
                }

                craf.setImgAssinatura(imgAssinatura);
                novaListaCrafImprimir.add(craf);
            } catch (Exception e) {
                log.error(e.getMessage(), e.getCause());
                FacesUtil.addErrorMessage("Erro no serviço de tratamento de assinatura");
            }

        });
        return novaListaCrafImprimir;
    }

   public List<Usuario> buscaDadosAutoridade() throws NegocioException {
       List<Usuario> autoridades = userDAO.buscaDadosAutoridade();
        if(!autoridades.isEmpty())
            return autoridades;
        else
            throw new NegocioException("Não há dados de autoridades, por favor verifique o cadastro");
    }

    /**busca todos os craf existentes*/
    public List<Craf>buscaTodosCraf(){
        return crafDAO.buscarTodos();
    }

    /**busca todos os craf assinados*/
    public List<Craf>buscartodosCrafAssinados(){
        return crafDAO.buscarTodosAssinados();
    }

    /**busca todos os craf NÃO assinados*/
    public List<Craf>buscarTodosCrafNaoAssinados(){
        return crafDAO.buscarTodosNaoAssinados();
    }



}//end class

