package br.gov.go.pm.service;

import br.gov.go.pm.dao.CidadeDAO;
import br.gov.go.pm.dao.CidadeDAO;
import br.gov.go.pm.modelo.Cidade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CidadeService {

	@Autowired
	private CidadeDAO dao;

	@Transactional
	public void salvar(final Cidade cidade){
		dao.salvar(cidade);
	}

	public List<Cidade> listar(){
		return dao.buscarTodos();
	}
	
	public Cidade buscarPorCodigo(final Long codigo) {
		return dao.buscarPorCodigo(codigo);
	}
	
	public List<Cidade> buscarPorDescricao(String descricao){
		return dao.buscarPorDescricao(descricao);
	}

	@Transactional
	public void excluir(final Cidade cidade){
		dao.excluir(cidade);
	}


}