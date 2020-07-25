package br.gov.go.pm.service;

import br.gov.go.pm.dao.GrupoCalibreDAO;
import br.gov.go.pm.dao.GrupoCalibreDAO;
import br.gov.go.pm.dao.UserDAO;
import br.gov.go.pm.modelo.ArmaInativa;
import br.gov.go.pm.modelo.GrupoCalibre;
import br.gov.go.pm.security.SegurancaDetalhe;
import br.gov.go.pm.util.exception.InfraException;
import br.gov.go.pm.util.exception.NegocioException;
import br.gov.go.pm.util.jsf.FacesUtil;
import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class GrupoCalibreService {

	@Autowired
	private GrupoCalibreDAO dao;

	@Autowired
	private SegurancaDetalhe segurancaDetalhe;

	/** Busca os GrupoCalibres para a lista inicial*/
	public List<GrupoCalibre> list(){
		return dao.list();
	}

	public GrupoCalibre buscarPeloCodigo(final Long codigo) {
		return dao.buscarPeloCodigo(codigo);
	}

	public List<GrupoCalibre> buscaGrupoCalibresPorParametro(String busca){
		return dao.buscaGrupoCalibresPorParametro(busca);
	}

//	public Optional<GrupoCalibre> buscaGrupoCalibreEspecifico(String nome){
//		return dao.buscaGrupoCalibreEspecifico(nome);
//	}

	@Transactional
	public void salvar(GrupoCalibre GrupoCalibre) throws Exception{

			if (GrupoCalibre != null) {

				/**insere quem criou ou alterou por último o usuário*/
				GrupoCalibre.setUsuarioAdministrador(segurancaDetalhe.getNomeUsuario());

				dao.salvar(GrupoCalibre);

			} else {
				throw new NegocioException("Erro ao salvar grupo de calibre. Verifique sua conexão ou contate o Administrador.");
			}
	}

	@Transactional
	public void excluir(final GrupoCalibre GrupoCalibre) throws Exception {
		try {
			dao.excluir(GrupoCalibre);

		}catch (InfraException i) {
			throw new Exception("Impossível a exclusão pois o grupo já está sendo utilizado por m calibre");
		}
	}


}