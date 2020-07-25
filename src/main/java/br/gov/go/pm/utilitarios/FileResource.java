package br.gov.go.pm.utilitarios;

import org.apache.commons.io.IOUtils;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

public class FileResource {

    /**pega a imagem imputStream do contexto*/
    public static InputStream getStream(final String path) throws Exception {
        return FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream(path);
    }

    /**Transforma uma imagem de array de byte em um imputStream*/
    public static InputStream getInputStream(final byte[] imgEmByte) throws Exception {
        ByteArrayInputStream imgInputStream = new ByteArrayInputStream(imgEmByte);
        return IOUtils.toBufferedInputStream(imgInputStream);
    }

    /**criador de pastas*/
    public static ServletContext criarPasta(String caminho) {
        ServletContext sContext = (ServletContext) FacesContext
                .getCurrentInstance().getExternalContext().getContext();

        File folder = new File(sContext.getRealPath(caminho));

        if (!folder.exists())
            folder.mkdirs();
        return sContext;
    }

}
