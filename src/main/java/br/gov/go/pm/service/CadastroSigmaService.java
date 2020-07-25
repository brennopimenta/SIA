package br.gov.go.pm.service;

import br.gov.go.pm.dao.ArmaDAO;
import br.gov.go.pm.dao.SigmaDAO;
import br.gov.go.pm.modelo.Arma;
import br.gov.go.pm.modelo.Sigma;
import br.gov.go.pm.security.SegurancaDetalhe;
import br.gov.go.pm.util.exception.NegocioException;
import br.gov.go.pm.util.jsf.FacesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CadastroSigmaService {

	@Autowired
	private SigmaDAO sigmaDAO;

	@Autowired
	private ArmaDAO armaDAO;

	@Autowired
	private SegurancaDetalhe segurancaDetalhe;

	@Transactional
	public void salvarProprietarioArma(Arma arma){
		try{
			if(arma.getCpfNovoProprietario() == null || arma.getCpfNovoProprietario().trim().equals("")){
				FacesUtil.addErrorMessage("Proprietário da Arma está vazio, insira o cpf! ");
			}

			armaDAO.salvar(arma);

		}catch (Exception e) {
				FacesUtil.addErrorMessage("Erro " + e.getMessage() +  " .Não atribuído proprietário ao Sigma. Por favor Edite o sigma "
						+ "e inclua uma arma válida ou contate o Administrador.");
			    }
	}
	
	@Transactional
	public void salvar(Sigma sigma) throws NegocioException {

		/**insere quem criou ou alterou por último o usuário*/
		sigma.setUsuarioSistema(segurancaDetalhe.getUsuarioPadrao());

		sigmaDAO.salvar(sigma);

	}

	
}
