package br.gov.go.pm.utilitarios;

import javax.servlet.ServletContext;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImagensEmArquivo implements Serializable {

	private static final long serialVersionUID = 1L;


	public ImagensEmArquivo() {

	}

	public static ServletContext folder() {
		return FileResource.criarPasta("paginas/temp");
	}

	public static String criarArquivoEmPastaTemp(ServletContext sContext, String nomeArquivo) {
//		String arquivo = sContext.getRealPath("/paginas/temp") + File.separator+ nomeArquivo + ".jpg";
		Path dir = Paths.get(sContext.getRealPath("/paginas/temp"));
		String arquivo = String.valueOf(dir)+ File.separator + nomeArquivo + ".jpg";
		return arquivo;
	}

//	public String criarArquivoEmPastaDefinida(ServletContext sContext, String nomeArquivo) {
//		String arquivo = sContext.getRealPath("/imagens/assinaturaCmt") + File.separator
//				+ nomeArquivo + ".jpg";
//		return arquivo;
//	}

	public static void criaArquivo(byte[] imagem, String arquivo) {
		FileOutputStream fos;

		try {
			fos = new FileOutputStream(arquivo);
			fos.write(imagem);

			fos.flush();
			fos.close();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

}