package br.gov.go.pm.controller;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import br.gov.go.pm.dao.SigmaDAO;
import br.gov.go.pm.modelo.Arma;
import br.gov.go.pm.modelo.Sigma;
import org.apache.log4j.Logger;

@ManagedBean
@ViewScoped
public class PesquisaSigmaBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private Logger log = Logger.getLogger(getClass());

	@ManagedProperty(value = "#{sigmaDAO}")
	private SigmaDAO sigmaDAO;
	
	private List<Sigma> listaSigmas;
	
	private Sigma sigma;
	private Arma arma;
		
	@PostConstruct
	public void inicializar() {
	  listaSigmas = sigmaDAO.buscarTodos();
	  limpar();

	}

	public void limpar(){
		sigma = new Sigma();
		arma = new Arma();
	}
	
	public void pesquisar(){
		if((arma.getNumeroArma() == null ||arma.getNumeroArma().trim().equals("")) && 
				(sigma.getCodigo() == null || sigma.getCodigo() == 0 )){
			listaSigmas = sigmaDAO.buscarTodos();

		}else{
			listaSigmas = sigmaDAO.buscarProcessosSigma(arma.getNumeroArma(), sigma.getCodigo());
			
		}
	}
	
	
	//Getters and Setters
	public List<Sigma> getListaSigmas() {
		return listaSigmas;
	}


	public void setListaSigmas(List<Sigma> listaSigmas) {
		this.listaSigmas = listaSigmas;
	}


	public void setSigmaDAO(SigmaDAO sigmaDAO) {
		this.sigmaDAO = sigmaDAO;
	}


	public Sigma getSigma() {
		return sigma;
	}


	public void setSigma(Sigma sigma) {
		this.sigma = sigma;
	}

	public Arma getArma() {
		return arma;
	}

	public void setArma(Arma arma) {
		this.arma = arma;
	}
	
	
}
