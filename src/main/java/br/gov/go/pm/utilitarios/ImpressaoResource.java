package br.gov.go.pm.utilitarios;

import br.gov.go.pm.enuns.StatusEmissao;
import br.gov.go.pm.service.general.GeradorDeArquivos;
import com.itextpdf.text.DocumentException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImpressaoResource {

   private Logger log = Logger.getLogger(getClass());

    public ImpressaoResource() { }

    public static void geraVisualizacao(String str, String titulo, StatusEmissao ORIENTACAO, String down, String nomeArquivoEspecifico) throws DocumentException, IOException {
        String arquivo = null;
        String nomeArquivo = null;
        Path dir = null;


        /**criando a pasta interna que vai ficar o arquivo*/
        ServletContext sContext = FileResource.criarPasta("paginas/doc");

        dir = Paths.get(sContext.getRealPath("/paginas/doc"));
        nomeArquivo = nomeArquivoEspecifico + ".pdf";

        /**vai para o gerador de arquivo*/
        arquivo = String.valueOf(dir)+ File.separator + nomeArquivo;

        /**gera o conteúdo do arquivo passando os parâmetros necessarios*/
        GeradorDeArquivos geradorDeArquivos = new GeradorDeArquivos();
        geradorDeArquivos.gerarPDFfromList(arquivo,  str, titulo, 12.0f, ORIENTACAO);

        /**imprimie o arquivo*/
        visualizarEmPDF(dir, nomeArquivo, down);

    }

    public static void visualizarEmPDF(Path dir, String arquivo, String down) throws IOException {
        final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.

        String path = String.valueOf(dir);
        // Prepare.
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();

        File file = new File(path, arquivo);
        BufferedInputStream input = null;
        BufferedOutputStream output = null;

        try {
            // Open file.
            input = new BufferedInputStream(new FileInputStream(file));

            // Init servlet response.
            response.reset();
            response.setHeader("Content-Type", "application/pdf");
            response.setHeader("Content-Length", String.valueOf(file.length()));
            response.setHeader("Content-Disposition", down.concat(";filename=\"" + arquivo + "\""));
            output = new BufferedOutputStream(response.getOutputStream());

            // Escrever para o output
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            output.flush();
        } finally {
            close(output);
            close(input);
        }

        facesContext.responseComplete();
    }

    private static void close(Closeable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
