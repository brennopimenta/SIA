package br.gov.go.pm.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import br.gov.go.pm.controller.LoginBean;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.conn.HttpHostConnectException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.gov.go.pm.utilitarios.Utilitarios;

public class SecurityFilter implements Filter {

    private String tokenValidationUrl = Utilitarios.tokenValidationUrl();
    private String urlLogin = Utilitarios.urlLogin();
    private String urlRetorno = Utilitarios.url();
    private static final String PERFIL_USO = Utilitarios.perfilUso;

    private Boolean removeParameter = false;

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        removeParameter = false;

                HttpSession session = ((HttpServletRequest) req).getSession(true);//false


                LoginBean loginBean = (session != null) ? (LoginBean) session.getAttribute("loginBean") : null;

                if (loginBean == null) {
                    loginBean = new LoginBean();
                    session = ((HttpServletRequest) req).getSession(true);
                    session.setAttribute("loginBean", loginBean);

                }

                String token = loginBean.getToken();
                if (token == null) {
                    token = request.getParameter("access_token");

                    if (token != null) {
                        removeParameter = true;
                    }
                }
                if (token == null) {
                    redirectSigu(request, response);
                    return;
                }

                Pair<Integer, JsonNode> pair = isTokenValido(token, loginBean, session);

                if (pair.getKey() == HttpServletResponse.SC_OK) {
                    req.setAttribute("jsonSigu", pair.getValue());
                    if (removeParameter) {
                        String url = ((HttpServletRequest) req).getRequestURL().toString();
                        response.sendRedirect(url);
                    } /*else {
                        chain.doFilter(req, res);
                        return;
                    }*/
                } else if (pair.getKey()== HttpServletResponse.SC_MOVED_PERMANENTLY){
                	redirecionaSemPerfil(request, response);


                } else if (pair.getKey() == HttpServletResponse.SC_FORBIDDEN) {
                    redirectSigu(request, response);
                    return;
                } else {
                    response.sendError(pair.getKey());
                }

                chain.doFilter(req, res);
                return;
    }

	    private void redirectSigu(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
	        String url = null;
	     try{
	        if (req instanceof HttpServletRequest) {
	        	url = urlRetorno;
	            //queryString = ((HttpServletRequest) req).getQueryString();
	        }
	        res.sendRedirect(urlLogin + "&redirect_uri=" + url);

	    }catch(HttpHostConnectException conn){
	    	RequestDispatcher dispatcher = req.getRequestDispatcher(Utilitarios.redirectErroDeAcesso());
	    	dispatcher.forward(req, res);

	    }
	   }

	   private void redirecionaSemPerfil(HttpServletRequest req, HttpServletResponse res){
		   if (req instanceof HttpServletRequest){

	       	RequestDispatcher dispatcher = req.getRequestDispatcher(Utilitarios.redirectUsuarioSemPerfil());
	       	try {
	       		dispatcher.forward(req, res);

			} catch (ServletException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		   }

	   }

	    /* PRIVATE */
	     private Pair<Integer, JsonNode> isTokenValido(String token, LoginBean lb, HttpSession sessao) {
	        try {

	        	System.setProperty("https.protocols", "TLSv1");

	        	String content = Request.Post(tokenValidationUrl).bodyForm(Form.form().add("token", token).build()).execute().returnContent().asString();
	            JsonNode jsonNode = mapper.readTree(content);
	            if (hasPerfil(jsonNode.get("perfis"))) {
	              lb.setJsonNode(jsonNode, sessao);
	              lb.setToken(token);

	              return ImmutablePair.of(HttpServletResponse.SC_OK, jsonNode);
	          } else {
	              // return ImmutablePair.of(HttpServletResponse.SC_FORBIDDEN, null);
	              return ImmutablePair.of(HttpServletResponse.SC_MOVED_PERMANENTLY, null);
	          }

	        } catch (HttpResponseException hre) {
	            // log.warn("Não autenticado no sigu", hre);
	            return ImmutablePair.of(HttpServletResponse.SC_FORBIDDEN, null);
	        } catch (Throwable e) {
	            e.printStackTrace();

	            return ImmutablePair.of(null, null);
	        }
	    }

	   /**verifica o perfil SIA do usuario*/
	   private boolean hasPerfil(JsonNode perfis) {
	       if (perfis != null)
	           for (final JsonNode perfil : perfis)
	               if (PERFIL_USO.toUpperCase().equals(perfil.get("sistema").get("descricao").asText().toUpperCase())) // ver se é sia, como cadastrado em PERFIL_USO

	                   return true;
	       return false;
	   }


 }