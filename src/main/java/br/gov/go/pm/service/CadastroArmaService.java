package br.gov.go.pm.service;

import br.gov.go.pm.dao.ArmaDAO;
import br.gov.go.pm.dao.CalibreDAO;
import br.gov.go.pm.dao.ModeloArmaDAO;
import br.gov.go.pm.enuns.Label;
import br.gov.go.pm.enuns.TipoAlma;
import br.gov.go.pm.modelo.*;
import br.gov.go.pm.security.SegurancaDetalhe;
import br.gov.go.pm.util.exception.NegocioException;
import br.gov.go.pm.util.jsf.FacesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CadastroArmaService {
	
	@Autowired
	private ArmaDAO dao;

	@Autowired
	private ModeloArmaDAO modeloArmaDAO;

	@Autowired
	private CalibreDAO calibreDAO;

	@Autowired
	private SegurancaDetalhe segurancaDetalhe;



	@Transactional
	public void salvar(Arma arma) throws NegocioException {
	
		try{

			/**insere quem criou ou alterou por último o usuário*/
			arma.setUsuarioSistema(segurancaDetalhe.getUsuarioPadrao());

			if (arma.getEnunsArmas().getTipoAlma().equals(TipoAlma.RAIADA) && arma.getEnunsArmas().getSentidoRaia() == null){
					throw new NegocioException("O sentido da Raia é o obrigatório pois alma da arma é RAIADA");
			}
			if (arma.getEnunsArmas().getTipoAlma().equals(TipoAlma.RAIADA) && arma.getQtdDeRaias() == 0){
				throw new NegocioException("A arma é raiada portanto a QUANTIDADE de RAIAS deve ser maior que zero");
			}

			dao.salvar(arma);

		}catch(PersistenceException e ){
			throw new NegocioException(e.getMessage());

		}
	}

	@Transactional
	public void salvarSigmaEmArma(Arma arma) throws NegocioException {
		try{
			dao.salvar(arma);

		}catch(PersistenceException e ){
			FacesUtil.addErrorMessage("Problema ao salvar nº do sigma em arma.");

		}
	}

	/**busca no DAO do modelo os modelos por Marca(Fabricante)*/
	public List<ArmaModelo> buscarModeloPorFabricante(Long codigoFab) {
		return modeloArmaDAO.buscarModeloPorFabricante(codigoFab);
	}

    public List<Calibre> buscarCalibres() {
		return calibreDAO.list();
    }


}
