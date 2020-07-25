package br.gov.go.pm.util;

import br.gov.go.pm.utilitarios.ImagensEmArquivo;
import org.primefaces.context.RequestContext;

import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.Map;

public class AppTools {

    public static void openDialogModal(boolean modal, boolean rediz, boolean min, String Height, String Width, String bottom, String url) {
        Map<String, Object> opcoes = new HashMap<>();
        opcoes.put("modal", modal);
        opcoes.put("resizable", rediz);
        opcoes.put("minimizable", min);
        opcoes.put("contentHeight", Height);
        opcoes.put("contentWidth", Width);
        opcoes.put("bottom", bottom);
        opcoes.put("responsive", true);
        opcoes.put("appendTo", "@body");

        RequestContext.getCurrentInstance().openDialog(url, opcoes, null);
    }

    public static void geraFotoArma(String ident, byte[] foto) {
        try {
            if (foto != null) {
                ServletContext sContext = ImagensEmArquivo.folder();
                String arquivo = ImagensEmArquivo.criarArquivoEmPastaTemp(sContext, ident);

                ImagensEmArquivo.criaArquivo(foto, arquivo);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}// end class
