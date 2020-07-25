package br.gov.go.pm.service;

import br.gov.go.pm.dao.PaisesDAO;
import br.gov.go.pm.modelo.Paises;

import br.gov.go.pm.util.exception.NaoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PaisesService{

	@Autowired
	private PaisesDAO dao;

	@Transactional
	public void salvar(final Paises pais){
		dao.salvar(pais);
	}

	public List<Paises> listar(){
		return dao.buscarTodos();
	}
	
	public Paises buscarPorCodigo(final Long codigo) {
		return dao.buscarPorCodigo(codigo);
	}
	
	public Optional <List<Paises>> buscarPorDescricao(String descricao){
		return dao.buscarPorDescricao(descricao);
	}
	

	@Transactional
	public void excluir(final Paises pais){
		dao.excluir(pais);
	}
	
}