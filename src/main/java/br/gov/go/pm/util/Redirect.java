package br.gov.go.pm.util;

import br.gov.go.pm.util.jsf.FacesUtil;
import org.apache.log4j.Logger;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.io.IOException;

import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

public class Redirect {

    private Logger log = Logger.getLogger(getClass());

    public static final void redireciona(String url) {

        FacesContext fContext = FacesContext.getCurrentInstance();
        ExternalContext extContext = fContext.getExternalContext();

        try {
            extContext.redirect(extContext.getRequestContextPath() + url);
        } catch (IOException e) {
            FacesUtil.addErrorMessage("Erro ao redirecionar a página");
        }
    }

    public static final void pushErro500(){
        FacesContext fContext = FacesContext.getCurrentInstance();
        ExternalContext extContext = fContext.getExternalContext();

        try {
            extContext.responseSendError(SC_INTERNAL_SERVER_ERROR,"Erro interno simulado");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}


