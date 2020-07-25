package br.gov.go.pm.service;

import br.gov.go.pm.dao.BaseNumeroSigmaDAO;
import br.gov.go.pm.modelo.ArmaNumeroSigma;
import br.gov.go.pm.util.exception.NegocioException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ImportaArquivoSigmaService {

	@Autowired
	private BaseNumeroSigmaDAO dao;

	/**salva a lista no banco, caso ocorra alguma exception dá um rollbak em tudo.*/
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public void salvar(List<ArmaNumeroSigma> lista) throws NegocioException {
		excluirBase();
		try {
			for (ArmaNumeroSigma armaNumeroSigma : lista)
				dao.salvar(armaNumeroSigma);
		}catch (NegocioException e){
			throw new NegocioException("erro no serviço que insere conteudo do arquivo no banco de dados. " + e);
		}

	}

	@Transactional
	public void excluirBase() throws NegocioException{
		try {

			List<ArmaNumeroSigma> lista = dao.buscarTodos();
			if(!lista.isEmpty())
  	        	for (ArmaNumeroSigma armaNumeroSigma : lista)
					dao.excluir(armaNumeroSigma);

		}catch (NegocioException e){
			throw new NegocioException("erro de exclusão no serviço!" + e);
		} catch (Exception e) {
			throw new NegocioException("erro no geral no serviço. Por favor contate o administrador " + e);
		}

	}




} //end class
