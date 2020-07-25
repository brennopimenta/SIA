package br.gov.go.pm.util.jsf;

import org.primefaces.event.FileUploadEvent;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class FacesUtil {

	public static void addSuccessMessage(String message) {
		FacesContext.getCurrentInstance().addMessage(null, 
				new FacesMessage(FacesMessage.SEVERITY_INFO,
						message, message)); 
	}
	
	public static void addErrorMessage(String message) {
		FacesContext.getCurrentInstance().addMessage(null, 
				new FacesMessage(FacesMessage.SEVERITY_ERROR,
						message, message)); 
	}

	public static void addAlertMessage(String message) {
		FacesContext.getCurrentInstance().addMessage(null, 
				new FacesMessage(FacesMessage.SEVERITY_WARN,
						message, message)); 
	}

	/**metodo recebe o envento com o nome do arquivo e alerta que já inclui arquivo com sucesso.*/
	public static void addMsgAnexo(FileUploadEvent event) {

		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage( "Arquivo (" + event.getFile().getFileName() + ") anexado com sucesso!"));

	}
	
}