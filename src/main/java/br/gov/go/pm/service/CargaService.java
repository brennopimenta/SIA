package br.gov.go.pm.service;

import br.gov.go.pm.dao.*;
import br.gov.go.pm.enuns.*;
import br.gov.go.pm.modelo.Arma;
import br.gov.go.pm.modelo.Carga;
import br.gov.go.pm.modelo.Cidade;
import br.gov.go.pm.modelo.Sigma;
import br.gov.go.pm.security.SegurancaDetalhe;
import br.gov.go.pm.util.FormataData;
import br.gov.go.pm.util.exception.InfraException;
import br.gov.go.pm.util.exception.NegocioException;
import br.gov.go.pm.util.jsf.FacesUtil;
import br.gov.go.pm.utilitarios.ImpressaoResource;
import br.gov.go.pm.utilitarios.MostraDataAtual;
import com.itextpdf.text.DocumentException;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class CargaService {
	private Logger log = Logger.getLogger(getClass());

	@Autowired
	private CargaDAO dao;

	@Autowired
	private SigmaDAO sigmaDAO;

	@Autowired
	private SegurancaDetalhe segurancaDetalhe;

	@Autowired
	private ArmaDAO armaDAO;

	@Autowired
	private CidadeDAO cidadeDAO;


	@Transactional
	public void salvar(Carga carga, final List<Sigma> sigmas) throws NegocioException {
		try {
			final List<Arma> listaArmas = gerarListaArma(sigmas);
			/**verifica se tem armas para gerar uma carga*/
			if (!listaArmas.isEmpty()) {
				/**salva a lista de processos de sigmas e por consequecia a listas de arma*/
				carga.setSigmas(sigmas);
				carga.setEmissor(segurancaDetalhe.getUsuarioPadrao());
				carga.setStatus(StatusCarga.NAO_ENVIADA);

				dao.salvar(carga);

				/**salva status de GERADA_CARGA em arma*/
				salvaEmArma(listaArmas);
			}else{
				throw new NegocioException("Não há armas para gerar uma carga");
			}

		}catch (Exception e){
			throw new NegocioException(e.getMessage());
		}

	}

	@Transactional(rollbackFor = Exception.class)
	public void salvarEdit(final Carga carga, final List<Sigma> sigmas) throws NegocioException, InfraException {
		try {
			/**verifica se tem armas para gerar uma carga*/
			if (carga != null) {
				/**salva a lista de processos de sigmas e por consequencia a listas de arma*/
				carga.setSigmas(sigmas);
				carga.setEmissor(segurancaDetalhe.getUsuarioPadrao());

				dao.salvar(carga);

			}else{
				throw new NegocioException("Não há cargas para salvar, favor verifique sua conexão, refaça o processo!");
			}

		}catch (Exception e){
			throw new NegocioException(e.getMessage());
		}

	}

	/**salva carga de forma geral. Método usado também por outras classes.*/
	@Transactional(rollbackFor = Exception.class)
	public void salvarCarga(final Carga carga) throws NegocioException, InfraException {
		try {
			if (carga != null)
				dao.salvar(carga);
			else
				throw new NegocioException("Falha no serviço de salvar carga. Verifique sua conexão, refaça o processo ou contate o Administrador!");

		}catch (Exception e){
			throw new NegocioException(e.getMessage());
		}

	}

	@Transactional(rollbackFor = Exception.class)
	public void salvarBoletim(final Carga carga) throws NegocioException {
		try {
			if (carga.getBoletimInclusao().isEmpty() || carga.getBoletimInclusao() == null) {
				throw new NegocioException("Erro, não há boletim a ser incluído, por favor verifique");
			}else {
				dao.salvar(carga);
			}

		}catch (Exception e){
			throw new NegocioException(e.getMessage());
		}

	}

	@Transactional(rollbackFor = Exception.class)
	public void salvarStatusEnviada(Carga carga) throws NegocioException {
		try {
			carga.setStatus(StatusCarga.ENVIADA);
			carga.setDataEnvio(new Date());
			dao.salvar(carga);

		}catch (Exception e){
			throw new NegocioException(e.getMessage());
		}

	}

	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public void salvaEmArma(final List<Arma> lista) throws Exception {
		try {
			for (Arma arma : lista) {
				arma.getEnunsArmas().setStatusCarga(StatusCarga.GERADA_CARGA);
				armaDAO.salvar(arma);
			}
		}catch (Exception e){
			throw new Exception(e.getMessage());
		}
	}

	/**usado toda vez que precisar salvar uma arma, por qualquer método externo.*/
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public void salvaArma(final Arma arma) throws Exception {
		try {
				armaDAO.salvar(arma);

		}catch (Exception e){
			throw new Exception(e.getMessage());
		}
	}

	/*rollbackFor está no DAO*/
	public void excluir(final Carga carga) throws NegocioException {
		try {
			dao.excluir(carga);
		}catch (Exception e){
			throw new NegocioException(e.getMessage());
		}

	}

	public List<Arma> gerarListaArma(final List<Sigma> sigmasArmas) {
		List<Arma> listaArmas = new ArrayList<>();
		for (Sigma sigma : sigmasArmas) {
			listaArmas.add(sigma.getArma());
		}
		return listaArmas;
	}


	/* Buscas em Carga*/

	public List<Carga> buscarTodas() {
		return dao.buscarTodos();
	}

	public List<Carga> buscarCargasComBoletim(){
		return dao.buscarCargasComBoletim();
	}

	public Carga buscarPeloCodigo(final Long codigo) {
		return dao.buscarPeloCodigo(codigo);
	}

	/**busca a carga com as armas (Lazy)*/
	public Carga buscarCargaComArmas(final Long codigo){
		return dao.buscarCargaComArmas(codigo);
	}

	public Carga buscar(final String codigo){
		return dao.buscar(codigo);
	}

	/**busca de sigmas(com as armas) para montar a carga*/
	public List<Sigma> buscaSigmasComArmasSemNumeroSigma(){
		List<Sigma> sigmasBase = sigmaDAO.buscaSigmasComArmasSemNumeroSigma();
		List<Sigma> sigmas = new ArrayList<>();
		int cont = 0;
		int tamanho = sigmasBase.size();

		if (tamanho < 100){
			return sigmasBase;
		} else {
			/** arma que sejam apenas 100 armas relacionadas ao processo de sigma */
			for (int i = 0; i < 100; i++) {
				sigmas.add(sigmasBase.get(i));
			}
			return sigmas;
		}
	}

	/**busca carga pela arma - utilizada também na consulta de carga do form carga*/
	public List<Carga> listarCargaPelaArma(String arma) throws NegocioException{
		return dao.listarCargaPelaArma(arma);

	}

	/*   *****************Geradores de lista ********************* */

	public String gerarListaArmasVisualizacao(List<Arma> armas) {
		List<Arma>listaArmas = armas;
		List<String> lista = new ArrayList<>();

		for (Arma arma : listaArmas) {
			lista.add(arma.getNumeroArma());
		}

		/**str e list vão para o gerador de arquivo*/
		String str = lista.toString();
		String strFormatado = str.substring(1, str.length() - 1).trim();         //pronto

		return strFormatado;
	}


	/**gera a lista de string para ser baixada(baixarCarga())
	 * @param carga*/
	public String gerarListaCarga(Carga carga){

		List<String>lista = new ArrayList<>();

		String codigoNoExercito = DadosFixos.EXERCITO.getDescricao();
		String sentidoRaia = null;
		String quantidadeRaias = null;
		String dataExpedicaoRG=null;
		String codigoUf = null;
		String cidadeFuncional = null;
		String cidadeRes = null;

		final String DELIM_INI = "[";
		final String DELIM_FIM = "]";

		lista.add("[REMOTO]".concat("[".concat(FormataData.dataAbreviadaAnoCompletoComHoras(carga.getDataCriacao()))).concat("][")
				.concat(String.valueOf(carga.getSigmas().size())).concat("]"));

		/**verifica criterios se a alma for ou nao raiada*/
		for (Sigma sigma: carga.getSigmas()) {
			if(sigma.getArma().getEnunsArmas().getTipoAlma().equals(TipoAlma.RAIADA)) {
				sentidoRaia = sigma.getArma().getEnunsArmas().getSentidoRaia().getDescricao();
				quantidadeRaias = String.valueOf(sigma.getArma().getQtdDeRaias());
			}else{
				sentidoRaia = "";
				quantidadeRaias = "";
			}

			/**verifica se existe a data expedicao da rg */
			if(sigma.getDadosPessoaisSigma().getRgDataExpedicao() != null)
				dataExpedicaoRG = FormataData.dataAbreviada(sigma.getDadosPessoaisSigma().getRgDataExpedicao());
			else dataExpedicaoRG = String.valueOf(FormataData.dataAbreviada(MostraDataAtual.getDataDeHoje()));

			/**verifica o codigo do estado*/
			codigoUf = codigoUf(sigma);

			/**verifica o codigo das cidades*/
			cidadeFuncional = buscaCidade(sigma.getEnderecoFuncional().getCidade());
			cidadeRes = buscaCidade(sigma.getEnderecoResidencial().getCidadeRes());

					lista.add((char)13 + codigoNoExercito + DELIM_INI.concat(String.valueOf(sigma.getArma().getCodigo())).concat(DELIM_FIM).concat(" ")
									.concat(DELIM_INI).concat(sigma.getArma().getNumeroArma()).concat(DELIM_FIM).concat(" ")
									.concat(DELIM_INI).concat(sigma.getArma().getMarca().getCodigoOrgao()).concat(DELIM_FIM).concat(" ")
									.concat(DELIM_INI).concat(String.valueOf(sigma.getArma().getEnunsArmas().getEspecie().ordinal())).concat(DELIM_FIM).concat(" ")
									.concat(DELIM_INI).concat(sigma.getArma().getModelo().getModelo()).concat(DELIM_FIM).concat(" ")
									.concat(DELIM_INI).concat(sigma.getArma().getCalibre().getCalibre()).concat(DELIM_FIM).concat(" ")
									.concat(DELIM_INI).concat(sigma.getArma().getCalibre().getGrupoCalibre().getIdentificador()).concat(DELIM_FIM).concat(" ")
									.concat(DELIM_INI).concat(String.valueOf(sigma.getArma().getCapacidade())).concat(DELIM_FIM).concat(" ")
									.concat(DELIM_INI).concat(String.valueOf(sigma.getArma().getEnunsArmas().getFuncionamento().ordinal())).concat(DELIM_FIM).concat(" ")
									.concat(DELIM_INI).concat(String.valueOf(sigma.getArma().getNumeroDeCanos())).concat(DELIM_FIM).concat(" ")
									.concat(DELIM_INI).concat(String.valueOf(sigma.getArma().getComprimentoCano())).concat(DELIM_FIM).concat(" ")
									.concat(DELIM_INI).concat(String.valueOf(sigma.getArma().getEnunsArmas().getUnMedComprimentoDoCano())).concat(DELIM_FIM).concat(" ")
									.concat(DELIM_INI).concat(sigma.getArma().getEnunsArmas().getTipoAlma().getDescricao()).concat(DELIM_FIM).concat(" ")
									.concat(DELIM_INI).concat(quantidadeRaias).concat(DELIM_FIM).concat(" ")
									.concat(DELIM_INI).concat(sentidoRaia).concat(DELIM_FIM).concat(" ")
									.concat(DELIM_INI).concat(sigma.getArma().getEnunsArmas().getAcabamento().getAcabamento()).concat(DELIM_FIM).concat(" ")
									.concat(DELIM_INI).concat(sigma.getArma().getPais().getCodigoOrgao()).concat(DELIM_FIM).concat(" ")
									.concat(DELIM_INI).concat(DadosFixos.TIPOPUBLICACAO.getDescricao()).concat(DELIM_FIM).concat(" ")
									/*numero e data da carga segundo convenção com a DECAE*/
									.concat(DELIM_INI).concat(String.valueOf(carga.getCodigo())).concat(DELIM_FIM).concat(" ")
									.concat(DELIM_INI).concat(String.valueOf(FormataData.dataAbreviada(sigma.getDataCriacao()))).concat(DELIM_FIM).concat(" ")
									.concat(DadosFixos.EXERCITO.getDescricao()).concat(" ")
									.concat(DELIM_INI).concat(sigma.getArma().getCpfNovoProprietario()).concat(DELIM_FIM).concat(" ")
									.concat(DELIM_INI).concat(sigma.getArma().getNomeNovoProprietario()).concat(DELIM_FIM).concat(" ")
									.concat(DELIM_INI).concat(FormataData.dataAbreviada(sigma.getDadosPessoaisSigma().getDataNascimento())).concat(DELIM_FIM).concat(" ")
									.concat(DELIM_INI).concat(sigma.getRg()).concat(DELIM_FIM).concat(" ")
									.concat(DELIM_INI).concat(dataExpedicaoRG).concat(DELIM_FIM).concat(" ")
									.concat(DELIM_INI).concat(sigma.getDadosPessoaisSigma().getRgOrgaoExpedidor()).concat(DELIM_FIM).concat(" ")
									//CONFERIR COM O EXÉRCITO(ADEQUAR NOSSO ENUM COM O BD DELES)
									.concat(DELIM_INI).concat(codigoUf).concat(DELIM_FIM).concat(" ")
									.concat(DELIM_INI).concat(sigma.getDadosPessoaisSigma().getPai()).concat(DELIM_FIM).concat(" ")
									.concat(DELIM_INI).concat(sigma.getDadosPessoaisSigma().getMae()).concat(DELIM_FIM).concat(" ")
                                    .concat(DELIM_INI).concat(DadosFixos.PROFISSAO.getDescricao()).concat(DELIM_FIM).concat(" ")
                                    .concat(DELIM_INI).concat(sigma.getEnderecoFuncional().getLogradouro()).concat(DELIM_FIM).concat(" ")
                                    .concat(DELIM_INI).concat(sigma.getEnderecoFuncional().getBairro()).concat(DELIM_FIM).concat(" ")
                                    //substituído do web service pelo esses dados no BD que está conforme tabela do exercito
                                    .concat(DELIM_INI).concat(cidadeFuncional).concat(DELIM_FIM).concat(" ")
                                    .concat(DELIM_INI).concat(sigma.getEnderecoResidencial().getLogradouroRes()).concat(DELIM_FIM).concat(" ")
                                    .concat(DELIM_INI).concat(sigma.getEnderecoResidencial().getBairroRes()).concat(DELIM_FIM).concat(" ")
									//substituído do web service pelo esses dados no BD que está conforme tabela do exercito
                                    .concat(DELIM_INI).concat(cidadeRes).concat(DELIM_FIM).concat(" ")
                                    //CONFERIR COM O EXÉRCITO
                                    .concat(DELIM_INI).concat(DadosFixos.TIPO_PROPRIETARIO.getDescricao()).concat(DELIM_FIM).concat(" ")
					);
		}

		String str = lista.toString();
		String strCarga = str.substring(1, str.length() - 1).trim().replaceAll(",", "");

		return strCarga;
	}

	public String codigoUf(Sigma sigma) {
		String uf = sigma.getDadosPessoaisSigma().getRgUfExpedidor();
		List<UnidadeFederacao> unidadeFederacaoList = Arrays.asList(UnidadeFederacao.values());
		String codigo = null;
		for (UnidadeFederacao sigla : unidadeFederacaoList) {
			if (!uf.isEmpty() && uf.equals(sigla.sigla())) {
				codigo = sigla.codigo();
			}
		}
		return codigo;
	}

	/**caso falhe a busca sera retornado o codigo de Goiania*/
	public String buscaCidade(String cidade){

		if (cidade.isEmpty()){
			return "197";
		}

		Cidade cidadeResult = cidadeDAO.buscarCidadeEspecifica(cidade);
		if (cidadeResult == null)
			return "197"; /**197 é o código de Goiânia*/
		else
			return cidadeResult.getCodigoOrgao();
    }

    /*compartilhados*/

    public void processoVisualizaArmas(Carga cargaSelecionada) {
        Carga carga = buscarCargaComArmas(cargaSelecionada.getCodigo());

            if (carga != null) {
                /**preenche a lista de armas*/
                List<Arma>listaArmas = gerarListaArma(carga.getSigmas());
                try {
                    if (!carga.getSigmas().isEmpty()) {
                        String strFormatado = gerarListaArmasVisualizacao(listaArmas);
                        String titulo = "Lista de Armas  -  Carga " + carga.getCodigo();
                        visualizarLista(strFormatado, titulo, StatusEmissao.RETRATO, "inline");

                    } else {
                        FacesUtil.addErrorMessage("Não existem armas na carga. Favor verifique.");
                    }

                } catch (Exception e) {
                    FacesUtil.addErrorMessage(e.getMessage());
                }
            } else {
                FacesUtil.addErrorMessage("A carga  " + cargaSelecionada.getCodigo() + " está vazia.");
            }
        }

    /**visualiza as listas genericamentE, ou seja, nas diversas formatações*/
    public void visualizarLista(String str, String titulo, StatusEmissao ORIENTACAO, String down) {
        try{
            ImpressaoResource.geraVisualizacao(str, titulo, ORIENTACAO, down,"carga");

        } catch(DocumentException de) {
            FacesUtil.addErrorMessage("Erro na geração do documento: " + de.getMessage());
        } catch(IOException ioe) {
            FacesUtil.addErrorMessage("Erro de IOE por favor verfique ou contate o administrador..: " + ioe.getMessage());
        } catch (Exception ex){
            FacesUtil.addErrorMessage("Erro no processo de geração e impressão do arquivo, verifique conexao e refaça o processo, se persistir contate o Administrador..: " + ex.getMessage());
        }

    }

	public StreamedContent processoBaixarArquivoCarga(Carga cargaSelecionada, String cargaView, String nomeArquivo) {
        String numeroCarga;
        String dataCarga;
        if (cargaSelecionada == null) {
			numeroCarga = "";
			dataCarga = String.valueOf(FormataData.dataAbreviada(MostraDataAtual.getDataDeHoje()));
		}else {
			numeroCarga = String.valueOf(cargaSelecionada.getCodigo());
			dataCarga = String.valueOf(FormataData.dataAbreviada(cargaSelecionada.getDataCriacao()));
		}

		final InputStream stream = new ByteArrayInputStream(cargaView.getBytes());
		StreamedContent file = new DefaultStreamedContent(stream, "application/txt", nomeArquivo + "-" + dataCarga + "- carga"+ numeroCarga + ".txt");
		RequestContext.getCurrentInstance().execute("executaBotaoDownload();");

		return file;

	}
}//end class
