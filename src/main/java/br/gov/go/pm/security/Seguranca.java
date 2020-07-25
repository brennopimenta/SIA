package br.gov.go.pm.security;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.io.Serializable;

/** */
@ManagedBean
@ViewScoped
public class Seguranca implements Serializable{

	private static final long serialVersionUID = 1L;

	private ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();

	@ManagedProperty(value = "#{segurancaDetalhe}")
	private SegurancaDetalhe segurancaDetalhe;


	/**captura o nome do usuário logado*/
	public String getNomeUsuario() {
		return segurancaDetalhe.getNomeUsuario();
	}

	/**captura o profile (perfil individual) do usuário logado*/
	public String getPefilUsuario() {
		return segurancaDetalhe.getPefilUsuario();
	}



	/**Métodos para verificação de papéis(dos grupos) e proteção de componentes segundo grupos de acesso*/

	/**********Verificação de Papéis por usuários*/

	public boolean isAdm(){
		return externalContext.isUserInRole("ADM") ;
	}
	public boolean isCmt(){
		return externalContext.isUserInRole("CMT") ;
	}
	public boolean isAutoridadeDelegada(){
		return externalContext.isUserInRole("AUTORIDADE_DELEGADA") ;
	}
	public boolean isChefe(){
		return externalContext.isUserInRole("CHEFE") ;
	}
	public boolean isGestor(){
		return externalContext.isUserInRole("GESTOR") ;
	}
	public boolean isGestorAux(){
		return externalContext.isUserInRole("GESTOR_AUXILIAR") ;
	}
	public boolean isChancela(){
		return externalContext.isUserInRole("CHANCELA") ;
	}

	public boolean isGestores(){
		return externalContext.isUserInRole("ADM") ||
				externalContext.isUserInRole("CHEFE") || externalContext.isUserInRole("CHEFE_SECAO")
				|| externalContext.isUserInRole("GESTOR")|| externalContext.isUserInRole("GESTOR_AUXILIAR");
	}

	public boolean isChefesAndGestores(){
		return externalContext.isUserInRole("ADM") || externalContext.isUserInRole("CHEFE") ||
				externalContext.isUserInRole("CHEFE_SECAO") || externalContext.isUserInRole("GESTOR");
	}

	public boolean isCmtAndAutoridade (){
		return externalContext.isUserInRole("CMT") || externalContext.isUserInRole("AUTORIDADE_DELEGADA");

	}

	/**end*/


	/**********Verificação de Papéis por funções*/
	public boolean isNaoPermitidoSalvar() {
		return externalContext.isUserInRole("CMT");
	}

	public boolean isPermitidoSalvarAssinatura(){
		return externalContext.isUserInRole("CMT") || externalContext.isUserInRole("ADM") ;
	}


	public boolean isPermitidoSalvarMarcaArma() {
		return externalContext.isUserInRole("ADM") || externalContext.isUserInRole("CHEFE_SECAO")
				|| externalContext.isUserInRole("GESTOR_AUXILIAR");
	}

	public boolean isPermitidoEditar() {
		return externalContext.isUserInRole("ADM") || externalContext.isUserInRole("CHEFE_SECAO")
		|| externalContext.isUserInRole("GESTOR_AUXILIAR");
	}

	public boolean isPermitidoExcluir() {
		return externalContext.isUserInRole("ADM") || externalContext.isUserInRole("CHEFE_SECAO")
				|| externalContext.isUserInRole("GESTOR_AUXILIAR");
	}
	/**end*/


	public void setSegurancaDetalhe(SegurancaDetalhe segurancaDetalhe) {
		this.segurancaDetalhe = segurancaDetalhe;
	}
}//end class
