package br.gov.go.pm.utilitarios;

import br.gov.go.pm.enuns.StatusEmissao;
import br.gov.go.pm.service.general.GeradorDeArquivos;
import br.gov.go.pm.util.jsf.FacesUtil;
import com.itextpdf.text.DocumentException;
import org.apache.log4j.Logger;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class VisualizarListaResource {

   private Logger log = Logger.getLogger(getClass());

    public VisualizarListaResource() { }


    /**visualiza as listas genericamentE, ou seja, nas diversas formatações*/
    public static void visualizarLista(String str, String titulo, StatusEmissao ORIENTACAO, String down) {
        try {
            ImpressaoResource.geraVisualizacao(str, titulo, ORIENTACAO, down, "listaDeArmas");

        } catch (DocumentException de) {
            FacesUtil.addErrorMessage("Erro na geração do documento: " + de.getMessage());
        } catch (IOException ioe) {
            FacesUtil.addErrorMessage("Erro de IOE por favor verfique ou contate o administrador..: " + ioe.getMessage());
        } catch (Exception ex) {
            FacesUtil.addErrorMessage("Erro no processo de geração e impressão do arquivo, verifique conexao e refaça o processo, se persistir contate o Administrador..: " + ex.getMessage());
        }

    }
}
