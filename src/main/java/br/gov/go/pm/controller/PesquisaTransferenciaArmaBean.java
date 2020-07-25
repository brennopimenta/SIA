package br.gov.go.pm.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import br.gov.go.pm.dao.TransferenciaArmaDAO;
import br.gov.go.pm.modelo.Arma;
import br.gov.go.pm.modelo.ArmaTransferencia;


@ManagedBean
@ViewScoped
public class PesquisaTransferenciaArmaBean implements Serializable {

	private static final long serialVersionUID = -1L;

	@ManagedProperty(value = "#{transferenciaArmaDAO}")
	private TransferenciaArmaDAO transferenciaArmaDAO;
	
	private List<ArmaTransferencia> transferencias = new ArrayList<>();
	private ArmaTransferencia armaTransferencia;
	private Arma arma;
	
	
	public Arma getArma() {
		return arma;
	}

	public void setArma(Arma arma) {
		this.arma = arma;
	}

	@PostConstruct
	public void inicializar() {
		transferencias = transferenciaArmaDAO.buscarTodas();
		limpar();
		
	}
	
	public void pesquisar(){
		if((arma.getNumeroArma() == null ||arma.getNumeroArma().trim().equals("")) && 
				(armaTransferencia.getNovoProprietarioCpf() == null ||armaTransferencia.getNovoProprietarioCpf().trim().equals(""))){
			transferencias = transferenciaArmaDAO.buscarTodas();
			
		}else{
			transferencias = this.transferenciaArmaDAO.buscarTransferenciasPorArma(arma.getNumeroArma(), armaTransferencia.getNovoProprietarioCpf());
			this.armaTransferencia = new ArmaTransferencia();
			this.arma = new Arma();
		}
	}

	public void limpar(){
		this.armaTransferencia = new ArmaTransferencia();
		this.arma = new Arma();	
	}
	
	
	
	//getters and setters
	public List<ArmaTransferencia> getTransferencias() {
		return transferencias;
	}

	public void setTransferencias(List<ArmaTransferencia> transferencias) {
		this.transferencias = transferencias;
	}

	public ArmaTransferencia getArmaTransferencia() {
		return armaTransferencia;
	}

	public void setArmaTransferencia(ArmaTransferencia armaTransferencia) {
		this.armaTransferencia = armaTransferencia;
	}

	public void setTransferenciaArmaDAO(TransferenciaArmaDAO transferenciaArmaDAO) {
		this.transferenciaArmaDAO = transferenciaArmaDAO;
	}
}
