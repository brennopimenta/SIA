package br.gov.go.pm.utilitarios;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ArquivoResource implements Serializable {

	private static final long serialVersionUID = 1L;


	public ArquivoResource() {

	}

	public static ServletContext createFolder(String pathPasta) {
		ServletContext sContext = (ServletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();

		File folder = new File(sContext.getRealPath(pathPasta));

		if (!folder.exists())
			folder.mkdirs();
		return sContext;
	}



}