package br.gov.go.pm.service;

import br.gov.go.pm.dao.CargaDAO;
import br.gov.go.pm.dao.CrafDAO;
import br.gov.go.pm.enuns.*;
import br.gov.go.pm.enuns.Label;
import br.gov.go.pm.modelo.Arma;
import br.gov.go.pm.modelo.Carga;
import br.gov.go.pm.modelo.Craf;
import br.gov.go.pm.modelo.Sigma;
import br.gov.go.pm.security.SegurancaDetalhe;
import br.gov.go.pm.util.FormataData;
import br.gov.go.pm.util.exception.NegocioException;
import org.apache.log4j.Logger;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.util.*;
import java.util.List;

import static javafx.scene.text.Font.font;

@Service
public class ItemBoletimService {

	private Logger log = Logger.getLogger(getClass());

	@Autowired
	private CargaDAO cargaDAO;
	@Autowired
	private CargaService cargaService;

	public String gerarItemCargasEscolhidas(List<Carga> listaCargasSelecionadas) throws NegocioException {
		List<String>lista = new ArrayList<>();

		try {
			for (Carga cg : listaCargasSelecionadas) {
				lista.add((char)13 + gerarListaItem(cg) + (char)13);
				salvaDadosEmCarga(cg);
			}

			String str = lista.toString();
		    String item = str.substring(1, str.length() - 1).trim().replaceAll(",", "");
			return item;

		} catch (NegocioException e) {
			throw new NegocioException("Erro ao gerar item das Cargas selecionadas. Verifique a conexão, refaça o processo ou contate o Administrador." + e.getMessage());
		}
	}


	/**gera lista do item a ser exibido*/
	public String gerarListaItem(Carga carga) {
		List<String>lista = new ArrayList<>();
		final String DOISP = ": ";
		final String PVIR = "; ";

		for (Sigma sigma: carga.getSigmas()) {

			lista.add((char)13 +
							 Label.SIGMA.getDescricao().concat(DOISP).concat(sigma.getArma().getNumeroSigma()).concat(PVIR).concat(" ")
							.concat(Label.CPF.getDescricao()).concat(DOISP).concat(sigma.getArma().getNomeNovoProprietario()).concat(PVIR).concat("")
							.concat(Label.RG.getDescricao()).concat(DOISP).concat(sigma.getArma().getRgProprietario()).concat(PVIR).concat("")
							.concat(Label.TIPO.getDescricao()).concat(DOISP).concat(sigma.getArma().getEnunsArmas().getEspecie().name()).concat(PVIR).concat("")
							.concat(Label.MARCA.getDescricao()).concat(DOISP).concat(sigma.getArma().getMarca().getMarca()).concat(PVIR).concat("")
							.concat(Label.CALIBRE.getDescricao()).concat(DOISP).concat(sigma.getArma().getCalibre().getCalibre()).concat(PVIR).concat("")
							.concat(Label.MODELO.getDescricao()).concat(DOISP).concat(sigma.getArma().getModelo().getModelo()).concat(PVIR).concat("")
							.concat(Label.CAPACIDADE.getDescricao()).concat(DOISP).concat(String.valueOf(sigma.getArma().getCapacidade())).concat(PVIR).concat("")
							.concat(Label.NUMERO_ARMA.getDescricao()).concat(DOISP).concat(sigma.getArma().getNumeroArma()).concat(PVIR).concat("")
							.concat(Label.ACABAMENTO.getDescricao()).concat(DOISP).concat(sigma.getArma().getEnunsArmas().getAcabamento().getAcabamento()).concat(PVIR).concat("")
							.concat(Label.FUNCIONAMENTO.getDescricao()).concat(DOISP).concat(String.valueOf(sigma.getArma().getEnunsArmas().getFuncionamento())).concat(PVIR).concat("")
							.concat(Label.COMPRIMENTO_CANO.getDescricao()).concat(DOISP).concat(sigma.getArma().getComprimentoCano()).concat(" ")
							.concat(String.valueOf(sigma.getArma().getEnunsArmas().getUnMedComprimentoDoCano())).concat(PVIR).concat("")
							.concat(Label.TIPO_ALMA.getDescricao()).concat(DOISP).concat(String.valueOf(sigma.getArma().getEnunsArmas().getTipoAlma())).concat(PVIR).concat("")
							.concat(Label.RAIAS.getDescricao()).concat(DOISP).concat(String.valueOf(sigma.getArma().getQtdDeRaias())).concat(".") + (char)13
							// ultimo não recebe .concat(PVIR) e recebe o ponto final

			);

		}

		String str = lista.toString();
		String strFormatado = str.substring(1, str.length() - 1).trim().replaceAll(",", "");

		return strFormatado;
	}


	public void salvaDadosEmCarga(Carga carga) throws NegocioException{
		try {
				carga.setStatusEnvioBoletim(StatusCarga.GERADO_ITEM_BOLETIM);
				cargaService.salvarCarga(carga);
		}catch (Exception e){
			throw new NegocioException(e.getMessage());
		}

	}

	/** busca todas as cargas Disponíveis a enviar para boletim */
	public List<Carga> buscarTodasParaEnvioBoletim() {
		return cargaDAO.buscarTodasParaEnvioBoletim();
	}

	public List<Carga> buscarCargasEnviadasParaBoletim(){
		return cargaDAO.buscarCargasEnviadasBoletim();
	}

	public List<Carga> buscarCargasComBoletim(){
		return cargaService.buscarCargasComBoletim();
	}

	public List<Carga> buscarCargaPelaArma(String busca) throws NegocioException {
		return cargaService.listarCargaPelaArma(busca);
	}


	/*   *****************Geradores de lista ********************* */

	/**utiliza método pronto em cargaService para gerar uma lista de armas*/
	public void processoVisualizaArmas(Carga cargaSelecionada) {
		cargaService.processoVisualizaArmas(cargaSelecionada);
	}

	public void visualizarLista(String str, String titulo, StatusEmissao orientacao, String down) {
		cargaService.visualizarLista(str, titulo, orientacao, down);
	}


	public StreamedContent processoBaixarArquivoCarga(Carga cargaSelecionada, String cargaView, String nomeArquivo) {
		return cargaService.processoBaixarArquivoCarga(cargaSelecionada, cargaView, nomeArquivo);
	}
} //end class

