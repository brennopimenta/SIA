package br.gov.go.pm.controller;

import br.gov.go.pm.dao.MarcaArmaDAO;
import br.gov.go.pm.modelo.MarcaArma;
import br.gov.go.pm.service.MarcaArmaService;
import br.gov.go.pm.service.general.AtualizaPaginaService;
import br.gov.go.pm.util.exception.NegocioException;
import br.gov.go.pm.util.jsf.FacesUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@ViewScoped
public class MarcaArmaBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{marcaArmaService}")
    private MarcaArmaService marcaArmaService;

    @ManagedProperty(value = "#{marcaArmaDAO}")
    private MarcaArmaDAO dao;

    @ManagedProperty(value = "#{atualizaPaginaService}")
    private AtualizaPaginaService atualizaPaginaService;

    private List<MarcaArma> marcasDeArma = new ArrayList<>();
    private MarcaArma marcaSelecionada;
    private MarcaArma marcaArma;

    @PostConstruct
    public void init() {
        this.limpar();
        this.marcasDeArma = dao.buscarTodos();
    }

    public void limpar() {
        this.marcaArma = new MarcaArma();
    }

    public void atualizarForm() {
        setMarcaArma(getMarcaSelecionada());
    }

    public void salvar() {
        try {
            this.marcaArmaService.salvar(marcaArma);
            FacesUtil.addSuccessMessage("Marca de Arma Salva com Sucesso!");

            this.limpar();
            this.marcasDeArma = dao.buscarTodos();
        } catch (NegocioException e) {
            FacesUtil.addErrorMessage(e.getMessage());

        }
    }

    public void excluir() {
        try {
            dao.excluir(marcaSelecionada);
            this.marcasDeArma.remove(marcaSelecionada);
            atualizaPaginaService.AtualizaPagina();
            FacesUtil.addSuccessMessage("Fabricante " + marcaSelecionada.getMarca() + " excluído com sucesso");

            limpar();

        } catch (NegocioException e) {
            FacesUtil.addErrorMessage(e.getMessage());
        }
    }


    //Getters and Setters


    public MarcaArmaService getMarcaArmaService() {
        return marcaArmaService;
    }

    public void setMarcaArmaService(MarcaArmaService marcaArmaService) {
        this.marcaArmaService = marcaArmaService;
    }

    public MarcaArmaDAO getDao() {
        return dao;
    }

    public void setDao(MarcaArmaDAO dao) {
        this.dao = dao;
    }

    public List<MarcaArma> getMarcasDeArma() {
        return marcasDeArma;
    }

    public void setMarcasDeArma(List<MarcaArma> marcasDeArma) {
        this.marcasDeArma = marcasDeArma;
    }

    public MarcaArma getMarcaSelecionada() {
        return marcaSelecionada;
    }

    public void setMarcaSelecionada(MarcaArma marcaSelecionada) {
        this.marcaSelecionada = marcaSelecionada;
    }

    public MarcaArma getMarcaArma() {
        return marcaArma;
    }

    public void setMarcaArma(MarcaArma marcaArma) {
        this.marcaArma = marcaArma;
    }

    public void setAtualizaPaginaService(AtualizaPaginaService atualizaPaginaService) {
        this.atualizaPaginaService = atualizaPaginaService;
    }
}