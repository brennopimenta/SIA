package br.gov.go.pm.controller;

import br.gov.go.pm.modelo.Usuario;
import br.gov.go.pm.service.general.AtualizaPaginaService;
import br.gov.go.pm.utilitarios.Utilitarios;
import com.fasterxml.jackson.databind.JsonNode;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Serializable;

//import javax.enterprise.context.SessionScoped;

@ManagedBean
@SessionScoped
public class LoginBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String cpfSigu;
    private Usuario usuario;
    private String token;
    private String cpf;
    private String senha;
    private JsonNode jsonNode;
    private String msgErroAcessoSistema = Utilitarios.mensagemErroAcessoPadrao;
    private HttpSession sessao;

    //4.2.3.RELEASE - VERSÃO DO STRING QUE DEU ERRADA.
    //	<p:remoteCommand name="onload" oncomplete="alert('Olá');" />

    @ManagedProperty(value = "#{atualizaPaginaService}")
    private AtualizaPaginaService atualizaPaginaService;

    @PostConstruct
    public void init() {
        this.senha = "";

    }

    /**
     * Método que pega dados do Json Retornado pela SSPJ
     *
     * @throws IOException
     * @throws ServletException
     */
    public void setJsonNode(JsonNode jsonNode, HttpSession sessaoFilter) throws ServletException, IOException {
        this.jsonNode = jsonNode;
        if (jsonNode != null)
            cpfSigu = jsonNode.get("cpf").asText().toUpperCase(); /** pega o cpf vindo do Sigu*/
        gravaDadosNaSessao(sessaoFilter, cpfSigu);

    }

    public void setAutenticarNoSpring() {
        HttpSession session = ((HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true));
        cpfSigu = (String) session.getAttribute("cpfCapturado");

        if (cpfSigu != null || !cpfSigu.equals("")) {
            cpf = cpfSigu;

        } else {
            msgErroAcessoSistema = "O cpf do Sigu veio Vazio ou sistema não conseguiu capturar.";
            redirecionaPagErro();
        }
    }

    /**
     * método da aplicação responsável por armazenar o token e o cpf na seção
     */
    public void gravaDadosNaSessao(HttpSession sessaoFilter, String cpfSigu) {
        try {
            sessao = sessaoFilter;
            sessao.setAttribute("cpfCapturado", cpfSigu);
            sessao.setAttribute("token", this.token);
        } catch (Exception e) {
            msgErroAcessoSistema = "Erro ao gravar dados para utilização na automática na sessão. Contate o Administrador!";
            redirecionaPagErro();
        }
    }

    public void redirecionarSSO() {
        limparLogin();
        ExternalContext extContext = pegaContexto();
        String url = ((HttpServletRequest) extContext.getRequest()).getRequestURL().toString();
        try {
            if (url.equals(Utilitarios.redirectLogar())) {
                url = Utilitarios.redirectAutenticado();
            } else if (url.contains("error")) {
                url = Utilitarios.redirectAutenticado();
            }
            extContext.redirect(Utilitarios.urlLogarSSP() + url);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void redirecionaPagErro() {
        ExternalContext extContext = pegaContexto();

        try {
            extContext.redirect(extContext.getRequestContextPath() + Utilitarios.redirectErroDeAcesso());
        } catch (IOException e) {
            System.out.println("Erro na ao encaminha para página de erro: " + e);
            redirecionarSSO();
        }
    }

    public void logoff() throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().redirect("j_spring_security_logout");
        limparLogin();

    }

    private ExternalContext pegaContexto() {
        FacesContext fContext = FacesContext.getCurrentInstance();
        return fContext.getExternalContext();
    }

    public void limparLogin() {

        cpfSigu = null;
        usuario = null;
        token = null;
        cpf = null;
        senha = null;
        token = null;
        jsonNode = null;

        HttpSession session = ((HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true));
        if (session != null) {
            session.removeAttribute("loginBean");
            session.invalidate();
        }
    }


    //Getters and setters
    public String getCpfSigu() {
        return cpfSigu;
    }

    public void setCpfSigu(String cpfSigu) {
        this.cpfSigu = cpfSigu;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getCpf() {

        return cpf;

    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }


    public JsonNode getJsonNode() {
        return jsonNode;
    }

    public String getMsgErroAcessoSistema() {
        return msgErroAcessoSistema;
    }

    public void setMsgErroAcessoSistema(String msgErroAcessoSistema) {
        this.msgErroAcessoSistema = msgErroAcessoSistema;
    }

    public HttpSession getSessao() {
        return sessao;
    }

    public void setSessao(HttpSession sessao) {
        this.sessao = sessao;
    }

    public void setAtualizaPaginaService(AtualizaPaginaService atualizaPaginaService) {
        this.atualizaPaginaService = atualizaPaginaService;
    }
} //e
