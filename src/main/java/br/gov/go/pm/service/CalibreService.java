package br.gov.go.pm.service;

import br.gov.go.pm.dao.CalibreDAO;
import br.gov.go.pm.dao.GrupoCalibreDAO;
import br.gov.go.pm.dao.UserDAO;
import br.gov.go.pm.modelo.Calibre;
import br.gov.go.pm.modelo.GrupoCalibre;
import br.gov.go.pm.security.SegurancaDetalhe;
import br.gov.go.pm.util.exception.NegocioException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CalibreService {
	private final Logger log = Logger.getLogger(this.getClass());

	@Autowired
	private CalibreDAO dao;

	@Autowired
	private GrupoCalibreDAO grupoCalibreDAO;

	@Autowired
	private SegurancaDetalhe segurancaDetalhe;

	/** Busca os Calibres para a lista inicial*/
	public List<Calibre> list(){
		return dao.list();
	}

	public Calibre buscarPeloCodigo(final Long codigo) {
		return dao.buscarPeloCodigo(codigo);
	}

	public List<Calibre> buscaCalibresPorParametro(String busca) {
		return dao.buscaCalibresPorParametro(busca);
	}

	public Optional<Calibre> buscaCalibreEspecifico(String nome){
		return dao.buscaCalibreEspecifico(nome);
	}

	public List<GrupoCalibre> buscaGruposCalibre(){
		return grupoCalibreDAO.list();
	}

	@Transactional
	public void salvar(Calibre calibre) throws Exception{

			if (calibre != null) {

				/**insere quem criou ou alterou por último o usuário*/
				calibre.setUsuarioAdministrador(segurancaDetalhe.getNomeUsuario());

				dao.salvar(calibre);
				System.out.println(calibre.getCalibre() + " Grupo: " + calibre.getGrupoCalibre().getNome());

			} else {
				throw new NegocioException("Erro ao salvar o calibre. Verifique sua conexão ou contate o Administrador.");
			}
	}

	@Transactional
	public void excluir(final Calibre Calibre) throws Exception {
		try {
				dao.excluir(Calibre);

		}catch (Exception e){
			throw new NegocioException("Erro ao excluir o calibre. ");
		}
	}




}