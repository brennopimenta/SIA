package br.gov.go.pm.service;

import br.gov.go.pm.dao.CrafDAO;
import br.gov.go.pm.dao.EmailDAO;
import br.gov.go.pm.dao.SigmaDAO;
import br.gov.go.pm.enuns.StatusCraf;
import br.gov.go.pm.javamail.EnvioDeEmailServidorExterno;
import br.gov.go.pm.modelo.Arma;
import br.gov.go.pm.modelo.Craf;
import br.gov.go.pm.modelo.EmailConfig;
import br.gov.go.pm.modelo.Sigma;
import br.gov.go.pm.security.SegurancaDetalhe;
import br.gov.go.pm.util.exception.NegocioException;
import br.gov.go.pm.util.jsf.FacesUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.persistence.PersistenceException;
import java.util.Optional;

@Service
public class CadastroCrafService {

	@Autowired
	private CrafDAO dao;
	@Autowired
	private SigmaDAO sigmaDAO;
	@Autowired
	private SegurancaDetalhe segurancaDetalhe;
	@Autowired
	private EmailDAO emailDAO;
	@Autowired
	EnvioDeEmailServidorExterno envioDeEmailServidorExterno;


	@Transactional
	public void salvar(Craf craf) throws Exception{
		
		try{

			if (craf.getStatus() == null || craf.getStatus().equals(""))
			craf.setStatus(StatusCraf.PENDENTE);

			/**insere quem criou ou alterou por último o usuário*/
			craf.setEmissor(segurancaDetalhe.getUsuarioPadrao());
			/**acrescenta zero a esquerda no numero craf antes de salvar*/
			craf.setNumeroCraf(zerosAEsquerda(String.valueOf(craf.getNumeroCraf())));

			dao.salvar(craf);

		}catch (Exception e) {
			throw new NegocioException("Erro no serviço de cadastro do craf. " + e.getMessage());
	
		}
	}
	
	@Transactional
	public void salvarStatusImpressao(Craf craf) throws Exception {
	 try{
		dao.salvar(craf);

	  }catch (PersistenceException e) {
		FacesUtil.addErrorMessage("Problema ao salvar Status de impressao no Banco de Dados. Refaça o processo ou contate o Administrador!");

	  }
 	}

	@Transactional
 	public void salvarStatusCraf(Craf craf) throws NegocioException{
		try{
			dao.salvar(craf);

		}catch (Exception e) {
			throw new NegocioException("Falha no serviço craf " + e.getMessage());

		}
	}

 	public String ultimoCraf() throws NegocioException {
		try {
			String ultimoCraf = dao.ultimoNumeroCraf();
			if (ultimoCraf == null)
				ultimoCraf = "0";

			Integer ultimo = Integer.valueOf(ultimoCraf);
			Integer proximo = ultimo + 1;
			return zerosAEsquerda(String.valueOf(proximo)); /*converte para string novamente e acrescenta zero à esquerda */
		}catch (Exception e){
			throw new NegocioException(" - falha na busca e atribuição do último craf. " + e.getMessage());
		}
	}

	public Optional<Craf> buscaCrafExistente(Craf craf) throws NegocioException {
		return dao.buscarCrafPorNumeroCraf(zerosAEsquerda(craf.getNumeroCraf()));

	}

	public Craf buscaCrafPorArma(Arma arma){
		return dao.buscarCrafPorArma(arma.getNumeroArma());
	}

	public Optional<Craf> verficaSeExisteCrafParaArma(String numeroArma){
		return dao.verficaSeExisteCrafParaArma(numeroArma);
	}

	public String zerosAEsquerda(String numero){
		return StringUtils.leftPad(numero, 10, "0");
	}

	/**recebe um craf, seja de uma lista ou individual, e manda email*/
	public void enviarEmail(Craf craf) throws MessagingException {
		/**retorna um sigma*/
		Sigma sigma = sigmaDAO.buscarArmaSelecionada(craf.getArma().getNumeroArma());
		envioDeEmailServidorExterno.enviar(sigma.getEnderecoResidencial().getEmail());
	}

	public boolean buscarStatusEnvioEmail() {
		boolean status = emailDAO.buscarConfigEnvioEmail().getStatus();
		return status;
	}
} //end class

