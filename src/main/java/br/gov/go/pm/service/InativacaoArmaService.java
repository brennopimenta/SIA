package br.gov.go.pm.service;


import br.gov.go.pm.dao.ArmaDAO;
import br.gov.go.pm.dao.InativacaoArmaDAO;
import br.gov.go.pm.enuns.Status;
import br.gov.go.pm.modelo.Arma;
import br.gov.go.pm.modelo.ArmaInativa;
import br.gov.go.pm.security.SegurancaDetalhe;
import br.gov.go.pm.util.exception.NegocioException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PersistenceException;
import java.util.List;

@Service
public class InativacaoArmaService {
	
	@Autowired
	private InativacaoArmaDAO dao;

	@Autowired
    private ArmaDAO armaDAO;

	@Autowired
	private SegurancaDetalhe segurancaDetalhe;



	@Transactional
	public void salvar(ArmaInativa armaInativa, boolean salvaArma) throws NegocioException {
	
		try{
			/**insere quem criou ou alterou por último o usuário*/
			armaInativa.setUsuarioSistema(segurancaDetalhe.getUsuarioPadrao());

			if (salvaArma)
			salvarArma(armaInativa);


			dao.salvar(armaInativa);


		}catch(Exception e){
			throw new NegocioException("Erro ao salvar, verifique sua conexao, refaça o processo ou contate o Administrador");
		}
	}


	@Transactional
	public void salvarArma(ArmaInativa armaInativaAtiva) throws NegocioException {
		Arma arma = armaInativaAtiva.getArma();
		try {
			if (armaInativaAtiva.getMotivo() != null && arma != null) {
				if (armaInativaAtiva.getMotivo().equals(Status.ATIVA))
					arma.getEnunsArmas().setStatusRestritivo(null);
				else
					arma.getEnunsArmas().setStatusRestritivo(armaInativaAtiva.getMotivo());
			}else{
				throw new NegocioException("Erro ao salvar em arma: o motivo ou a arma estão sem valores. Verifique sua conexão, refaça ou contate o Administrador!");
			}

			armaDAO.salvar(arma);
		}catch (Exception e){
			throw new NegocioException(e.getMessage());
		}
	}

	public void verificaCondicaoInativa(Arma arma) throws NegocioException {
		/**verifica se a arma já está inativa*/
		if (arma.getEnunsArmas().getStatusRestritivo() != null)
			throw new NegocioException("Ops! Arma já está INATIVA.");
	}

	public void verificaCondicaoAtiva(Arma arma) throws NegocioException {
		/**verifica se a arma já está inativa*/
		if (arma.getEnunsArmas().getStatusRestritivo() == null)
			throw new NegocioException("Ops! Arma já está ATIVA.");
	}

	//buscar todos
	public List<ArmaInativa> buscarTodos() {
		return dao.buscarTodos();
	}

	public ArmaInativa buscarPeloCodigo (Long codigo){
		return dao.buscarPeloCodigo(codigo);
	}


    public Arma buscarArmaPeloCodigo(Long codigo) {
        return armaDAO.buscarPeloCodigo(codigo);
	}

	public List<ArmaInativa> listarAtivacaoInativacaoPelaArma(String busca) {
		return dao.listarAtivacaoInativacaoPelaArma(busca);
	}
}
