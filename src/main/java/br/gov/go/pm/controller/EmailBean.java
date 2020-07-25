package br.gov.go.pm.controller;

import br.gov.go.pm.modelo.EmailConfig;
import br.gov.go.pm.service.EmailService;
import br.gov.go.pm.service.general.AtualizaPaginaService;
import br.gov.go.pm.util.exception.NegocioException;
import br.gov.go.pm.util.jsf.FacesUtil;
import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@ViewScoped
public class EmailBean implements Serializable{

    private static final long serialVersionUID = 1L;

    private Logger log = Logger.getLogger(getClass());

    private List<EmailConfig> config = new ArrayList<>();
    private EmailConfig email = new EmailConfig();
    private boolean ligado = true;

    @ManagedProperty(value = "#{emailService}")
    private EmailService emailService;

    @ManagedProperty(value = "#{atualizaPaginaService}")
    private AtualizaPaginaService atualizaPaginaService;

    @PostConstruct
    public void init(){
        limpar();
        buscarTodos();
        setaDados();
    }

    public void buscarTodos(){
        config = emailService.list() ;
    }

    public void setaDados(){
        config.forEach(c -> {
            email.setCodigo(c.getCodigo());
            email.setSmtp(c.getSmtp());
            email.setPorta(c.getPorta());
            email.setEmail(c.getEmail());
            email.setSenha(c.getSenha());
            email.setStatus(c.getStatus());
            email.setUsuarioSistema(c.getUsuarioSistema());
            email.setDataCriacao(c.getDataCriacao());
            email.setDataModificacao(c.getDataModificacao());
            email.setMensagem(c.getMensagem());

        });
    }

    public void limpar() {
        config = new ArrayList<>();
    }

    public void salvar() {
        try {
            emailService.salvar(email);
            FacesUtil.addSuccessMessage("Configuração Salva com Sucesso!");
            atualizaPaginaService.AtualizaPagina();
        } catch (NegocioException e) {
            FacesUtil.addErrorMessage("Erro ao salvar. Por favor verique sua conexão ou contate o administrador. Erro: " + e.getMessage());
        }
    }

    public void statusEnvio(){


    }

    //getters and setters
    public List<EmailConfig> getConfig() {
        return config;
    }

    public void setConfig(List<EmailConfig> config) {
        this.config = config;
    }

    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    public void setAtualizaPaginaService(AtualizaPaginaService atualizaPaginaService) {
        this.atualizaPaginaService = atualizaPaginaService;
    }

    public EmailConfig getEmail() {
        return email;
    }

    public void setEmail(EmailConfig email) {
        this.email = email;
    }

    public boolean isLigado() {
        return ligado;
    }

    public void setLigado(boolean ligado) {
        this.ligado = ligado;
    }
}//fim
