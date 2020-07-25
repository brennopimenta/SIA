package br.gov.go.pm.service.general;

import br.gov.go.pm.modelo.DadosSicad;
import br.gov.go.pm.util.jsf.FacesUtil;
import br.gov.go.pm.utilitarios.Utilitarios;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Type;

@Service
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConsumirServicoSicad {


	private DadosSicad ds;
	private String token;
	private JsonNode jsonNode;
	private String resLogradouro;
	private String resNumero;
	private String resQuadra = null;
	private String resLote;
	private String resBairro;
	private String resCidade;
	private String resUf;
	private String nomePai;
	private String nomeMae;


	String logradouro;
	String bairro;
	String cidade;

	public DadosSicad busca(String cpf) {
		pegaToken();
		DadosSicad retorno;
		try {
			Client c = Client.create();
			WebResource wr = c.resource(Utilitarios.urlDadosSicad() + cpf + "?token=" + token);
			String json = wr.get(String.class);
			JSONObject jsonPessoa = new JSONObject(json);
			JSONObject endereco = jsonPessoa.getJSONObject("endereco");

			/**capturando endereço pessoal*/
			resLogradouro = String.valueOf(endereco.get("logradouro"));

			if (endereco.get("numero").equals(null))
				resNumero = "";
			else resNumero = endereco.getString("numero");

			resQuadra = String.valueOf(endereco.get("quadra"));
			resBairro = String.valueOf(endereco.get("bairro"));
			resCidade = String.valueOf(endereco.get("municipio"));
			resUf = String.valueOf(endereco.get("estado"));
			/**/

			/**capturando endereço funcional*/
			JSONObject enderecoFunc = jsonPessoa.getJSONObject("enderecoUnidade");
			logradouro = String.valueOf(enderecoFunc.get("logradouro"));
			bairro = String.valueOf(enderecoFunc.get("bairro"));
			cidade = String.valueOf(enderecoFunc.get("municipio"));
			/**/

			/**capturando dados pessoais*/
			if (jsonPessoa.get("nomePai").equals(null))
				nomePai = "NÃO CONSTA";
			else nomePai = jsonPessoa.getString("nomePai");

			if (jsonPessoa.get("nomeMae").equals(null))
				nomeMae = "NÃO CONSTA";
			else nomeMae = jsonPessoa.getString("nomeMae");
			/**/

			/**SETANDO DADOS DO JSON EM DadosSicad*/
			Gson gson = new Gson();
			//return gson.fromJson(json, new TypeToken<List<DadosPessoais>>(){}.getType());
			Type dpType = new TypeToken<DadosSicad>() {
			}.getType();

			ds = gson.fromJson(json, dpType);

			/**Atributos do json que vieram com o nome diferente de Dados Sicad*/

			/**setando endereço pessoal*/
			ds.setResLogradouro(resLogradouro);
			ds.setResNumero(resNumero);
			ds.setResQuadra(resQuadra);
			ds.setResBairro(resBairro);
			ds.setResCidade(resCidade);
			ds.setResUf(resUf);

			/**setando endereço funcional*/
			ds.setLogradouro(logradouro);
			ds.setBairro(bairro);
			ds.setCidade(cidade);

			/**setando endereço funcional*/
			ds.setNomePai(nomePai);
			ds.setNomeMae(nomeMae);

			/**end*/


			retorno = ds;

	}catch (JSONException js){
			FacesUtil.addErrorMessage("Não houve retorno do para a busca!");
			return null;
	}catch (Exception e) {
			retorno = null;
			return retorno;
		}

		return  retorno;

	}

	private String pegaToken(){
		HttpSession session = ((HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true));
		return this.token = (String) session.getAttribute("token");

	}


}