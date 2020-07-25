package br.gov.go.pm.service;

import br.gov.go.pm.dao.AssinaturaDAO;
import br.gov.go.pm.dao.CrafDAO;
import br.gov.go.pm.enuns.Profile;
import br.gov.go.pm.enuns.StatusCraf;
import br.gov.go.pm.enuns.StatusEmissao;
import br.gov.go.pm.modelo.Assinaturas;
import br.gov.go.pm.modelo.Craf;
import br.gov.go.pm.modelo.Usuario;
import br.gov.go.pm.security.SegurancaDetalhe;
import br.gov.go.pm.security.util.GeradorSenha;
import br.gov.go.pm.util.exception.InfraException;
import br.gov.go.pm.util.exception.NegocioException;
import br.gov.go.pm.util.jsf.FacesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AssinaCrafService {

	@Autowired
	private CrafDAO crafDAO;

	@Autowired
	private AssinaturaDAO assinaturaDAO;

	@Autowired
	private SegurancaDetalhe segurancaDetalhe;

	private StatusCraf status;

	/**caso o usuário tenha perfil de chancela vai assinar(no bean pesquisaCrafNaoAssinadosBean) como comandante). Retorna então profile de CMT
	 * se for pertencente ao grupo em epígrafe*/
	public String getPerfilUsuario() throws NegocioException {
		try {
			return segurancaDetalhe.getGruposUsuarioLogado().stream().filter(g -> g.getNome().equals("CHANCELA")).count() > 0 ? String.valueOf(Profile.CMT) : segurancaDetalhe.getPefilUsuario();
		} catch (Exception e) {
			throw new NegocioException("Erro ao obter o perfil do usuário no serviço de assinatura.");
		}
	}

	public Usuario getUsuario() throws NegocioException {
		try {
			return segurancaDetalhe.getUsuarioCheck();
		} catch (Exception e) {
			throw new NegocioException("Erro ao obter o nome do usuário no serviço de assinatura");
		}
	}
	/**usa um método encoder do spring para comparar a senha e verifica se usuário possui senha cadastrada*/
	public Boolean confirmaSenha(final String senhaDigitada) throws Exception {
		Usuario usuarioLogado = segurancaDetalhe.getUsuarioCheck();
		if (usuarioLogado.getSenhaAssinatura() == null )
			throw new NegocioException("Autoridade não possui senha cadastrada para assinatura. Por favor cadastre uma senha.");

			return GeradorSenha.assertSenhaEncode(senhaDigitada, usuarioLogado.getSenhaAssinatura());
	}

	@Transactional
	public void salvarAssinatura(final Craf craf) {
		try {
			/**Verfica se status é PENDENTE ou vazio e se foi inserido a chave AUTORIDADE ou AUTORIDADE_DELEGADA no craf*/
			if ((craf.getStatus().equals(StatusCraf.PENDENTE) || craf.getStatus() == null || craf.getStatus().equals(""))) {
				craf.setStatus(StatusCraf.ASSINADO);
				craf.setStatusEmissao(StatusEmissao.NAO_IMPRESSO);
				crafDAO.salvar(craf);
			} else{
				throw new NegocioException("Por alguma razão não foi constatada a chave da autoridade, por favor contate o Administrador. ");
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro no serviço de assinatura do Craf. " + e.getMessage());
		}
	}

	public Assinaturas buscaAssinaturaAtiva(String cpf) {
		return assinaturaDAO.verificaAssinaturaExistente(cpf);
	}

	//Getters e Setters
	public StatusCraf getStatus() {
		return status;
	}

	public void setStatus(StatusCraf status) {
		this.status = status;
	}

}//end class
