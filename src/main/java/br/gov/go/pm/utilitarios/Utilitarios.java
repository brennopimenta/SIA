package br.gov.go.pm.utilitarios;

import java.io.Serializable;

public class Utilitarios implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final String perfilUso = "mportal";
	public static String mensagemErroAcessoPadrao = "Desculpe, mas você não tem perfil para utilizar esse sistema!";

	public Utilitarios() {

	}


	/* *************************************************** PÁGINAS DE REDIRECIONAMENTO****************************************************** */

	/*DESENVOLVIMENTO*/

	public static String url(){
		return "http://localhost:8080/sia/paginas/login/autenticado.jsf";
	}

	public static String redirectLogar(){
		return "http://localhost:8080/sia/paginas/login/logar.jsf";
	}

	public static String redirectAutenticado(){
		return "http://localhost:8080/sia/paginas/login/autenticado.jsf";
	}

	public static String redirectErroDeAcesso(){return "/paginas/error/erro-acesso.jsf";}

	public static String redirectUsuarioSemPerfil(){
		return "/paginas/error/usuario-sem-perfil";
	}


	/*HOMOLOGAÇÃO SEGPLAN*/

//	public static String url(){
//		return "http://pmsiahomolog.intra.goias.gov.br/sia/paginas/login/autenticado.jsf";
//	}
//
//	public static String redirectLogar(){
//		return "http://pmsiahomolog.intra.goias.gov.br/sia/paginas/login/logar.jsf";
//	}
//
//	public static String redirectAutenticado(){
//		return "http://pmsiahomolog.intra.goias.gov.br/sia/paginas/login/autenticado.jsf";
//	}
//
//	public static String redirectErroDeAcesso(){return "/paginas/error/erro-acesso.jsf";}
//
//	public static String redirectUsuarioSemPerfil(){
//		return "/paginas/error/usuario-sem-perfil";
//	}


	/*PRODUÇÃO SEGPLAN */

//	public static String url(){
//		return "http://pm.intra.goias.gov.br/sia/paginas/login/autenticado.jsf";
//	}
//
//	public static String redirectLogar(){
//		return "http://pm.intra.goias.gov.br/sia/paginas/login/logar.jsf";
//	}
//
//	public static String redirectAutenticado(){
//		return "http://pm.intra.goias.gov.br/sia/paginas/login/autenticado.jsf";
//	}
//
//	public static String redirectErroDeAcesso(){return "/paginas/error/erro-acesso.jsf";}
//
//	public static String redirectUsuarioSemPerfil(){
//		return "/paginas/error/usuario-sem-perfil";
//	}


/* *************************************************** URL SSPJ ******************************************************************* */

	/*HOMOLOGAÇÃO*/

//	public static String tokenValidationUrl(){
//		String url = "https://ssows-h.ssp.go.gov.br/validate";
//		return url;
//	}
//	/*URL SSP*/
//	public static String urlLogin(){
//		String url = "https://sso-homo.ssp.go.gov.br/#/?response_type=token&client_id=mportal";
//		return url;
//	}
//
//	public static String urlLogarSSP(){
//		return "https://sso-homo.ssp.go.gov.br/#/?response_type=token&client_id=mportal&redirect_uri=";
//	}

	/*PRODUÇÃO*/

	public static String tokenValidationUrl(){
		String url = "https://ssows.ssp.go.gov.br/validate";
		return url;
	}
	/*URL SSP*/
	public static String urlLogin(){
		String url = "https://sso.ssp.go.gov.br/#/?response_type=token_only&client_id=mportal";
		return url;
	}

	public static String urlLogarSSP(){
		return "https://sso.ssp.go.gov.br/#/?response_type=token_only&client_id=mportal&redirect_uri=";
	}



	/* URL de Serviços*/

	public static String urlDadosSicad() {
		return "https://legadows.ssp.go.gov.br/dadosServidorPmPorCpfOuRgMilitar/";
		//"https://sicadws.ssp.go.gov.br/dadosServidorPorCpfOuRgMilitar/"
	}

	/* end****/












	/* URLs PARA COMPLETAS PARA LOGAR EM HOMOLOG*/

	public static String paginaLogar(){
		String urlLogar = urlLogarSSP() + redirectLogar();
		return urlLogar;
	}




}
