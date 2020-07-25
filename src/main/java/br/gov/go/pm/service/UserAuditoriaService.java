package br.gov.go.pm.service;

import br.gov.go.pm.dao.UserAuditoriaDAO;
import br.gov.go.pm.enuns.AcaoSistema;
import br.gov.go.pm.enuns.StatusEmissao;
import br.gov.go.pm.modelo.Usuario;
import br.gov.go.pm.modelo.UsuarioAuditoria;
import br.gov.go.pm.util.exception.NegocioException;
import br.gov.go.pm.util.jsf.FacesUtil;
import br.gov.go.pm.utilitarios.ImpressaoResource;
import com.google.gson.Gson;
import com.itextpdf.text.DocumentException;
import org.apache.log4j.Logger;
import org.primefaces.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserAuditoriaService {
	private final Logger log = Logger.getLogger(this.getClass());

	@Autowired
	private UserAuditoriaDAO dao;

	private UsuarioAuditoria usuarioAuditoria = new UsuarioAuditoria();

	public List<UsuarioAuditoria> list(){
		return dao.list();
	}


	public UsuarioAuditoria buscarPeloCodigo(final Long codigo) {
		return dao.buscarPeloCodigo(codigo);
	}

	@Transactional
	public void salvar(Usuario usuarioCliente, Usuario usuarioAtualUpdate, Usuario usuarioSistema, AcaoSistema acao) throws Exception {
		try {
			if (usuarioCliente != null && usuarioSistema !=null) {

				usuarioAuditoria.setCpfUsuario(usuarioCliente.getCpf());
				usuarioAuditoria.setAcao(acao);

				usuarioAuditoria.setDados(montaDados(usuarioCliente, usuarioAtualUpdate, usuarioSistema, acao));
				dao.salvar(usuarioAuditoria);

			} else {
				throw new NegocioException("Erro! Usuário é nulo no serviço.");

			}
		}catch (Exception e){
			throw new NegocioException("Erro ao salvar auditoria de usuário!");
		}

}

	private String montaDados(Usuario usuarioCliente, Usuario usuarioAtualUpdate, Usuario usuarioSistema, AcaoSistema acao) {

		Gson gson = new Gson();
		String dados;

		String jsonCliente = gson.toJson(usuarioCliente);
		String jsonUsuarioSistema = gson.toJson(usuarioSistema);
		String jsonAcao = gson.toJson(acao.getDescricao());

		JSONObject json = new JSONObject();

		/**se é usuário editado então acrescenta os dados antes da alteração*/
		if (usuarioAtualUpdate !=null) {
			String jsonUsuarioAtual = gson.toJson(usuarioAtualUpdate);
			json.put("usuarioAnteriorAlteracao", jsonUsuarioAtual);
		}

		json.put("acao", jsonAcao);
		json.put("cliente", jsonCliente);
		json.put("ultimoUsuarioSistema", jsonUsuarioSistema);

		String str = json.toString();
		String strFormatado = str.replaceAll("\\\\", "");
		dados = strFormatado;

		return dados;
	}

	public String geraDadosAuditoria(List<UsuarioAuditoria> usuariosAuditoria) {

		List<String> lista = new ArrayList<>();

		usuariosAuditoria.forEach(a -> {
			AcaoSistema acao = a.getAcao();
			String json = a.getDados();

			lista.add((char)13 + String.valueOf(acao).concat(json) + (char)13);
		});

		String str = lista.toString();

		return str;
	}

	/**visualiza as listas genericamentE, ou seja, nas diversas formatações*/
	public void visualizarLista(String str, String titulo, StatusEmissao ORIENTACAO, String down) {
		try{
			ImpressaoResource.geraVisualizacao(str, titulo, ORIENTACAO, down,"auditoriaUser");

		} catch(DocumentException de) {
			FacesUtil.addErrorMessage("Erro na geração do documento: " + de.getMessage());
		} catch(IOException ioe) {
			FacesUtil.addErrorMessage("Erro de IOE por favor verfique ou contate o administrador..: " + ioe.getMessage());
		} catch (Exception ex){
			FacesUtil.addErrorMessage("Erro no processo de geração e impressão do arquivo, verifique conexao e refaça o processo, se persistir contate o Administrador..: " + ex.getMessage());
		}

	}

	@Transactional(rollbackFor = Exception.class)
	public void excluir(final List<UsuarioAuditoria> auditoriasSelecionadas){
		try {
			auditoriasSelecionadas.forEach(a -> {
				UsuarioAuditoria auditoria = a;
				dao.excluir(auditoria);
			});

		}catch (Exception e){
			FacesUtil.addErrorMessage("Erro " + e.getMessage() + " ao excluir o auditoria de usuário! .");

		}
	}

	public UsuarioAuditoria getUsuarioAuditoria() {
		return usuarioAuditoria;
	}

	public void setUsuarioAuditoria(UsuarioAuditoria usuarioAuditoria) {
		this.usuarioAuditoria = usuarioAuditoria;
	}

	public List<UsuarioAuditoria> buscaAuditoriaEspecificas(String busca) {
		return dao.buscaAuditoriaEspecificas(busca);
	}
}