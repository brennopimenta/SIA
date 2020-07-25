package br.gov.go.pm.service;

import br.gov.go.pm.dao.MarcaArmaDAO;
import br.gov.go.pm.modelo.MarcaArma;
import br.gov.go.pm.util.exception.NaoEncontradoException;
import br.gov.go.pm.util.exception.NegocioException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class MarcaArmaService {

	@Autowired
	private MarcaArmaDAO dao;
	
	@Transactional
	public void salvar(MarcaArma marcaArma) throws NegocioException {
		
		if (marcaArma.getMarca() == null || marcaArma.getMarca().trim().equals("")){
			throw new NegocioException("A Marca da Arma é obrigatória");
			
		}
		
		dao.salvar(marcaArma);
	}

	public MarcaArma buscarPeloCodigo(final Long codigo) {
		return dao.buscarPeloCodigo(codigo).orElseThrow(() -> new NaoEncontradoException("Marca Arma "+ codigo));

	}

	public List<MarcaArma> buscaMarcaArmas() {
		List<MarcaArma> marcasDeArma = new ArrayList<MarcaArma>();
		marcasDeArma = dao.buscarTodos();
		
		return 	marcasDeArma;
	}
}


