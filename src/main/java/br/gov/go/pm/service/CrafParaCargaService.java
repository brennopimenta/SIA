package br.gov.go.pm.service;

import br.gov.go.pm.dao.CargaDAO;
import br.gov.go.pm.dao.CrafDAO;
import br.gov.go.pm.dao.EmailDAO;
import br.gov.go.pm.javamail.EnvioDeEmailServidorExterno;
import br.gov.go.pm.modelo.Arma;
import br.gov.go.pm.modelo.Carga;
import br.gov.go.pm.modelo.Craf;
import br.gov.go.pm.security.SegurancaDetalhe;
import br.gov.go.pm.util.exception.NegocioException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CrafParaCargaService {
	private Logger log = Logger.getLogger(getClass());

	@Autowired
	private CargaDAO cargaDAO;
	@Autowired
	private CrafDAO crafDAO;
	@Autowired
	private CadastroCrafService cadastroCrafService;
	@Autowired
	private CargaService cargaService;
	@Autowired
	private SegurancaDetalhe segurancaDetalhe;
	@Autowired
	private EmailDAO emailDAO;
	@Autowired
	EnvioDeEmailServidorExterno envioDeEmailServidorExterno;



	/**busca o ultimo craf*/
	public String buscarUltimoCraf() throws NegocioException {
		String ultimoCraf = cadastroCrafService.ultimoCraf();
		if (ultimoCraf == null || ultimoCraf.equals(""))
			ultimoCraf = "0";

		return ultimoCraf;
	}

	public String ultimoCraf(String numCraf) {
		Integer ultimo = Integer.valueOf(numCraf);
		Integer proximo = ultimo + 1;
		return cadastroCrafService.zerosAEsquerda(String.valueOf(proximo)); /*converte para string novamente e acrescenta zero à esquerda */
	}

	/**salva a lista no banco, caso ocorra alguma exception dá um rollbak em tudo.
	 * chama o método do serviço que salva o craf individualmente(cadastro indiv. de craf)*/
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public void gerarCrafs(List<Craf> lista) throws NegocioException {
		try {
			for (Craf craf : lista)
				cadastroCrafService.salvar(craf);
		}catch (Exception e){
			log.error(e.getMessage());
			throw new NegocioException("Erro no serviço que insere conteudo do arquivo no banco de dados. " + e);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public void salvarBoletim(Carga carga) throws NegocioException {
		cargaService.salvarBoletim(carga);
	}

	/**para cada carga ele salva os itens propostos, evitando buscas ao banco de dados.*/
	public void salvarDadosEmCarga(List<Carga> listaCargasParaCraf) throws NegocioException{
		Carga carga = new Carga();
		for (Carga cg: listaCargasParaCraf) {
			cg.setEmissorCraf(segurancaDetalhe.getUsuarioPadrao());
			cg.setDataGeracaoCraf(new Date());
			carga = cg;
			cargaService.salvarCarga(carga);
		}

	}

	public Carga buscarCargaPeloCodigo(Long codigo){
		return cargaDAO.buscarCargaComArmas(codigo);
	}

	public Optional<Craf> verficaSeExisteCrafParaArma(String numeroArma){
		return crafDAO.verficaSeExisteCrafParaArma(numeroArma);
	}

	/** busca todas as cargas prontas para gerar craf, ou seja, com sigma inserido, a qual aparece na LISTA inicial */
	public List<Carga> buscarTodas() {
		return cargaDAO.buscarTodasParaCraf();
	}

	public List<Carga> buscarCargaComCraf(){
		return cargaDAO.buscarCargaComCraf();
	}

	/** busca carga pronta para craf pela arma */
	public List<Carga> listarCargaParaCrafPelaArma(String arma) throws NegocioException {
		try {
			return cargaDAO.listarCargaParaCrafPelaArma(arma);
		}catch (Exception e){
			throw new NegocioException(e.getMessage());
		}
	}

	/*   *****************Geradores de lista ********************* */

	public String gerarListaArmasVisualizacao(List<Arma> armas) {
		/**utiliza o método existente em cargaService*/
		String strFormatado = cargaService.gerarListaArmasVisualizacao(armas);

		return strFormatado;
	}


	public void enviarEmail(List<Carga> listaCargasParaCraf) throws NegocioException{
		listaCargasParaCraf.forEach(c -> c.getSigmas().forEach(a -> {
			String email = a.getEnderecoResidencial().getEmail();
			try {
				envioDeEmailServidorExterno.enviar(email);
			} catch (MessagingException e) {
				log.error(e.getMessage(), e.getCause());
			}

		}));
	}

	public boolean buscarStatusEnvioEmail() {
		boolean status = emailDAO.buscarConfigEnvioEmail().getStatus();
		return status;
	}
} //end class

