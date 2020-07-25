package br.gov.go.pm.service.general;

import br.gov.go.pm.enuns.StatusEmissao;
import br.gov.go.pm.util.exception.NegocioException;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.log4j.Logger;
import org.jfree.ui.Align;
import org.primefaces.model.StreamedContent;
import org.springframework.stereotype.Service;

import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class GeradorDeArquivos {

	private Logger log = Logger.getLogger(getClass());

	private Map<String, Object> parametros;
	private String nomeInicialRelatorio;
	private String nomeFinalRelatorio;
	private StreamedContent file = null;
	private File tempFile = null;


	/**Este método é utilizado como método principal*/
	public void imprimirPDF(List<?> objetos, String nomeRelatorio, final InputStream in) throws Exception {

		//aqui usamos a interface JrDataSource, para abstrair-mos de usar apenas banco de dados, para tanto usamos a sua implementação JRBeanCollectionDataSource
		final JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(objetos);

		try{
			final JasperPrint print = JasperFillManager.fillReport(in , parametros,  (objetos == null ? new JREmptyDataSource() : jrds));
			final HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
			response.reset();
			response.setContentType("application/pdf");
			response.setHeader("Content-disposition", "inline; filename=".concat(nomeRelatorio).concat(".pdf"));
			final ServletOutputStream servletOutputStream = response.getOutputStream();
			JasperExportManager.exportReportToPdfStream(print, servletOutputStream);
			servletOutputStream.flush();
			servletOutputStream.close();
			FacesContext.getCurrentInstance().renderResponse();
			FacesContext.getCurrentInstance().responseComplete();
		} catch (JRException e) {
			throw new NegocioException("Erro JRE ao gerar o PDF" + e.getMessage());
		} catch (Exception erro) {
			throw new NegocioException("Erro " + erro.getMessage());
		} finally {

		}

	}

	/**método importante que manda as imagens necessárias ao craf (ex: fundo e assinatura*/
	public void adicionaParametroRelatorio(String chave, Object valor) {
		getParametros().put(chave, valor);
	}


	public Map<String, Object> getParametros() {
		if (parametros == null) {
			parametros = new HashMap<String, Object>();
		}
		return parametros;
	}

	/**gera conteúdo simples em PDF para visualização de listas (de String, etc)*/
	public void gerarPDFfromList(String arquivo, String str, String titulo, float fntSize, StatusEmissao ORIENTACAO)  throws DocumentException, IOException{

		Rectangle ROTATE = PageSize.A4_LANDSCAPE.rotate();

		if (ORIENTACAO.equals(StatusEmissao.PAISAGEM))
			ROTATE = PageSize.LETTER.rotate();
	
		Document document = new Document(ROTATE);
		try{

			PdfWriter.getInstance(document, new FileOutputStream(arquivo));

			document.open(); // adicionando um parágrafo ao documento
			if (str != null) {
				Paragraph header = new Paragraph();
				header.setAlignment(Element.ALIGN_CENTER);
				header.add(titulo);
				document.add(new Paragraph(header));
				document.add(new Paragraph(new Phrase(str, FontFactory.getFont(FontFactory.TIMES_ROMAN, fntSize))));

			}else{
				document.add(new Paragraph("Nenhum conteúdo diponível"));}

			document.close();

		} catch(DocumentException de) {
			log.error(de.getMessage());
		} catch(IOException ioe) {
			log.error(ioe.getMessage());
		}

	}



	public void setParametros(Map<String, Object> parametros) {
		this.parametros = parametros;
	}

	public String getNomeInicialRelatorio() {
		return nomeInicialRelatorio;
	}

	public void setNomeInicialRelatorio(String nomeInicialRelatorio) {
		this.nomeInicialRelatorio = nomeInicialRelatorio;
	}

	public String getNomeFinalRelatorio() {
		return nomeFinalRelatorio;
	}

	public void setNomeFinalRelatorio(String nomeFinalRelatorio) {
		this.nomeFinalRelatorio = nomeFinalRelatorio;
	}


}








/*
	public void imprimirDOC(String nomeArquivoJasper, List<?> objetos, String nomeRelatorio) throws NegocioException {
		JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(objetos);
		File arquivoIReport = new File(ReportLoader.class.getResource("").getPath() + nomeArquivoJasper + ".jasper");
		JasperReport jasperReport = null;
		JasperPrint printer = null;
		try {
			jasperReport = (JasperReport) JRLoader.loadObject(arquivoIReport);
			printer = JasperFillManager.fillReport(jasperReport, getParametros(),
					(objetos == null ? new JREmptyDataSource() : jrds));
			setNomeInicialRelatorio(Long.toString(new Date().getTime()) + ".rtf");
			File docFile = new File(ReportLoader.class.getResource("").getPath() + getNomeInicialRelatorio());
			setNomeFinalRelatorio(nomeRelatorio);
			if (docFile.exists()) {
				try {
					docFile.delete();
				} catch (Exception e) {
					throw new NegocioException("Erro ao exportar para DOC");
				}
			}
			JRRtfExporter jrRtfExporter = new JRRtfExporter();
			jrRtfExporter.setParameter(JRExporterParameter.JASPER_PRINT, printer);
			jrRtfExporter.setParameter(JRExporterParameter.OUTPUT_FILE, docFile);
			jrRtfExporter.exportReport();
		} catch (JRException e) {
			throw new NegocioException("Erro ao gerar o relatorio" + e.getMessage());
		} catch (Exception erro) {
			throw new NegocioException("Erro ao gerar o relatorio" + erro.getMessage());
		}
	}

	@SuppressWarnings("unused")
	public void imprimirXLS(String nomeArquivoJasper, List<?> objetos, String nomeRelatorio) throws NegocioException {
		JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(objetos);
		File arquivoIReport = new File(ReportLoader.class.getResource("").getPath() + nomeArquivoJasper + ".jasper");
		JasperReport jasperReport = null;
		JasperPrint printer = null;
		try {
			jasperReport = (JasperReport) JRLoader.loadObject(arquivoIReport);
			printer = JasperFillManager.fillReport(jasperReport, getParametros(),
					(objetos == null ? new JREmptyDataSource() : jrds));
			setNomeInicialRelatorio(Long.toString(new Date().getTime()) + ".xls");
			File excelFile = new File(ReportLoader.class.getResource("").getPath() + getNomeInicialRelatorio());
			setNomeFinalRelatorio(nomeRelatorio);
			if (excelFile.exists()) {
				try {
					excelFile.delete();
				} catch (Exception e) {
					throw new NegocioException("Erro ao exportar para Excel");
				}
			}
			JRXlsExporter jrxlsExporter = new JRXlsExporter();
			jrxlsExporter.setParameter(JRExporterParameter.JASPER_PRINT,
					this.gerarRelatorioJasperPrintObjeto(getParametros(), objetos, nomeArquivoJasper));
			jrxlsExporter.setParameter(JRExporterParameter.OUTPUT_FILE, excelFile);
			jrxlsExporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
			jrxlsExporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS, Boolean.TRUE);
			jrxlsExporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
			jrxlsExporter.exportReport();
		} catch (JRException e) {
			throw new NegocioException("Erro ao gerar o relatorio" + e.getMessage());
		} catch (Exception erro) {
			throw new NegocioException("Erro ao gerar o relatorio" + erro.getMessage());
		}
	}

		public StreamedContent getFileRTF() {
		StreamedContent file;
		InputStream stream = null;
		try {
			stream = new FileInputStream(ReportLoader.class.getResource("").getPath() + getNomeInicialRelatorio());
		} catch (FileNotFoundException e) {
			FacesUtil.addErrorMessage("Arquivo não encontrado " + getNomeFinalRelatorio());
		} // Caminho onde está salvo o arquivo.
		file = new DefaultStreamedContent(stream, "application/octet-stream", getNomeFinalRelatorio() + ".rtf");
		return file;
	}

	public StreamedContent getFileXLS() {
		StreamedContent file;
		InputStream stream = null;
		try {
			stream = new FileInputStream(ReportLoader.class.getResource("").getPath() + getNomeInicialRelatorio());
		} catch (FileNotFoundException e) {
			FacesUtil.addErrorMessage("Arquivo não encontrado " + getNomeFinalRelatorio());
		} // Caminho onde está salvo o arquivo.
		file = new DefaultStreamedContent(stream, "application/vnd.ms-excel", getNomeFinalRelatorio() + ".xls");
		return file;
	}


	*/


/*ver
	public JasperPrint gerarRelatorioJasperPrintObjeto(Map<String, Object> parametros, List<?> listaObjetos,
			String nomeArquivoJasper) throws Exception {
		try {
			JRDataSource jr = new JRBeanArrayDataSource((listaObjetos != null ? listaObjetos.toArray() : null));
			File arquivoIReport = new File(
					ReportLoader.class.getResource("").getPath() + nomeArquivoJasper + ".jasper");
			JasperReport jasperReport = (JasperReport) JRLoader.loadObject(arquivoIReport);
			JasperPrint print = JasperFillManager.fillReport(jasperReport, parametros,
					(listaObjetos != null ? jr : new JREmptyDataSource()));
			jr = null;
			arquivoIReport = null;
			jasperReport = null;
			return print;
		} catch (Exception e) {
			throw e;
		}
	}*/
