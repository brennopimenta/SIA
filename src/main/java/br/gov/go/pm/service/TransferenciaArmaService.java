package br.gov.go.pm.service;

import br.gov.go.pm.dao.ArmaDAO;
import br.gov.go.pm.dao.TransferenciaArmaDAO;
import br.gov.go.pm.enuns.StatusCraf;
import br.gov.go.pm.modelo.Arma;
import br.gov.go.pm.modelo.ArmaTransferencia;
import br.gov.go.pm.modelo.Craf;
import br.gov.go.pm.security.SegurancaDetalhe;
import br.gov.go.pm.util.exception.NegocioException;
import br.gov.go.pm.util.jsf.FacesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PersistenceException;

@Service
public class TransferenciaArmaService{


	@Autowired
    private TransferenciaArmaDAO dao;
	
	@Autowired
	private ArmaDAO armaDAO;

	@Autowired
	private CadastroCrafService crafService;

	@Autowired
	private SegurancaDetalhe segurancaDetalhe;
	
	/** Método salvar salva no objeto de Transferencia e método salva o novo proprietário em Arma*/
	@Transactional
	public void salvar(ArmaTransferencia armaTransferencia) throws NegocioException {
		try{
			if(armaTransferencia.getArma() == null){
				throw new NegocioException("Falta a Arma.!");
			}
			else if(armaTransferencia.getCedCpf()==null || armaTransferencia.getCedCpf().equals("0")){
				throw new NegocioException("Cpf Cedente não pode ser zero(0) e nem nulo!");

			}else if(armaTransferencia.getArma().getNumeroSigma() == null) {
				throw new NegocioException("Não é possível transferir uma arma sem sigma.");
			}else if(armaTransferencia.getArma().getCpfNovoProprietario() == null || armaTransferencia.getArma().getCpfNovoProprietario().trim().equals(0)){
				throw new NegocioException("Falta o proprietário da arma. Por favor verifique! ");

			}else {
				/**seta o usuario que fez a tranferência*/
				armaTransferencia.setUsuarioSistema(segurancaDetalhe.getUsuarioPadrao());
				dao.salvar(armaTransferencia);
				armaDAO.salvar(armaTransferencia.getArma());
				salvaDadosEmCraf(armaTransferencia.getArma());
			}

		}catch(Exception e ) {
			FacesUtil.addErrorMessage("Problema ao cadastrar a Transferencia, contate o Administrador!");
		}
	}

	public Craf buscarCrafPorArma(Arma arma){
		return crafService.buscaCrafPorArma(arma);
	}

	@Transactional
	public void salvaDadosEmCraf(Arma arma) throws NegocioException {

		try{
			Craf craf =  buscarCrafPorArma(arma);

			if(craf != null){
				craf.setStatus(StatusCraf.CANCELADO);
				crafService.salvarStatusCraf(craf);
			}else{
				FacesUtil.addAlertMessage("Não havia CRAF desta arma para cancelar, mas mão impediu a transferência");
			}



		}catch(PersistenceException e ){
			FacesUtil.addErrorMessage("Problema ao alterar craf!");

		}
	}

	/**busca arma*/
	public Arma buscaArma(Long codigo){
		return armaDAO.buscarPeloCodigo(codigo);
	}

}//fim

	

