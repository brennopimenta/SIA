package br.gov.go.pm.service;

import br.gov.go.pm.dao.ArmaDAO;
import br.gov.go.pm.dao.BaseNumeroSigmaDAO;
import br.gov.go.pm.enuns.StatusSigma;
import br.gov.go.pm.modelo.Arma;
import br.gov.go.pm.modelo.ArmaNumeroSigma;
import br.gov.go.pm.util.exception.NegocioException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
public class ManterNumeroSigmaService  {

	@Autowired
	private BaseNumeroSigmaDAO dao;

	@Autowired
	private ArmaDAO armaDAO;

	/**feito teste retornando um resultado ao bean para implementar ou não interações com usuário segundo este.*/
	@Transactional
	public void salvar(ArmaNumeroSigma armaNumeroSigma) throws NegocioException {

			Optional<ArmaNumeroSigma> sigmaExistente = dao.buscaPorSigmaExistente(armaNumeroSigma.getNumeroSigma());
			if (sigmaExistente.isPresent())
				throw new NegocioException(sigmaExistente.get().getNumeroSigma() + " já está cadastrado para arma "+
						sigmaExistente.get().getNumeroArma() + " .Por favor verifique.");
			else
				/*seta a data de atual*/
				armaNumeroSigma.setDataCriacao(new Date());
				dao.salvar(armaNumeroSigma);

	}

	@Transactional
	public void salvarAtribuidoNaBase(ArmaNumeroSigma armaNumeroSigma) throws NegocioException{
		armaNumeroSigma.setStatus(StatusSigma.ATRIBUIDO);
		dao.salvar(armaNumeroSigma);

	}

	public Optional<Arma> buscarSigmaExistenteEmArma(String numeroSigma) {
		return armaDAO.buscarSigmaExistenteEmArma(numeroSigma);
	}




}
