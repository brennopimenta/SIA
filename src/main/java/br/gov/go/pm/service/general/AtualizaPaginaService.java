package br.gov.go.pm.service.general;

import br.gov.go.pm.modelo.ArmaNumeroSigma;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

import javax.faces.application.Application;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

@Service
public class AtualizaPaginaService implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public void AtualizaPagina(){
		FacesContext context = FacesContext.getCurrentInstance();  
		Application application = context.getApplication();  
		ViewHandler viewHandler = application.getViewHandler();  
		UIViewRoot viewRoot = viewHandler.createView(context, context.getViewRoot().getViewId());  
		context.setViewRoot(viewRoot);  
		context.renderResponse();  
	}

	
}//fim
