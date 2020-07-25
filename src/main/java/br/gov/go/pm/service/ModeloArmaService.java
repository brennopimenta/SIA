package br.gov.go.pm.service;

import br.gov.go.pm.dao.CalibreDAO;
import br.gov.go.pm.dao.ModeloArmaDAO;
import br.gov.go.pm.enuns.StatusSituacao;
import br.gov.go.pm.modelo.ArmaModelo;
import br.gov.go.pm.modelo.Calibre;
import br.gov.go.pm.modelo.Cidade;
import br.gov.go.pm.modelo.Usuario;
import br.gov.go.pm.security.SegurancaDetalhe;
import br.gov.go.pm.util.exception.NegocioException;
import br.gov.go.pm.util.jsf.FacesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PersistenceException;
import java.util.List;

@Service
public class ModeloArmaService {
	
	@Autowired
	private ModeloArmaDAO dao;

	@Autowired
	private CalibreDAO calibreDAO;

	@Autowired
	private SegurancaDetalhe segurancaDetalhe;



	@Transactional
	public void salvar(ArmaModelo modelo) throws NegocioException {
	
		try{
			/**insere quem criou ou alterou por último o usuário*/
			modelo.setUsuarioSistema(segurancaDetalhe.getUsuarioPadrao());

			dao.salvar(modelo);

		}catch(PersistenceException e ){
			throw new NegocioException(e.getMessage());
		}
	}


	@Transactional
	public void inativar(ArmaModelo modelo) throws NegocioException {
		try {
			modelo.setStatus(StatusSituacao.INATIVO);
			salvar(modelo);
		}catch (Exception e){
			throw new NegocioException("Erro ao inativar o modelo! .");
		}
	}

	@Transactional
	public void ativar(ArmaModelo modelo) throws NegocioException {
		try {
			modelo.setStatus(StatusSituacao.ATIVO);
			salvar(modelo);
		}catch (Exception e){
			throw new NegocioException("Erro ao ativar o modelo! .");
		}
	}

	//buscar todos
	public List<ArmaModelo> buscarTodos() {
		return dao.buscarTodos();
	}

	public ArmaModelo buscarPeloCodigo (Long codigo){
		return dao.buscarPeloCodigo(codigo);
	}

	public List<ArmaModelo> buscarPorDescricao(String descricao){
		return dao.buscarPorDescricao(descricao);
	}

    public List<Calibre> buscarCalibres() {
		return calibreDAO.list();
    }
}
