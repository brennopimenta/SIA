package br.gov.go.pm.controller;

import br.gov.go.pm.dao.ArmaDAO;
import br.gov.go.pm.modelo.Arma;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configurable
public class ImageServlet extends HttpServlet {


    @Autowired
    private ArmaDAO dao;

    public void init(ServletConfig config) throws ServletException{
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }

    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
//          response.setContentType("image/jpeg");
//        final String codigo = request.getParameter("arma");
//        final Arma a = dao.buscarPeloCodigo(Long.parseLong(codigo));
//        final OutputStream out = response.getOutputStream();
//        out.write(a.getFoto());
//        out.close();

    }

    public void destroy(){

    }

}