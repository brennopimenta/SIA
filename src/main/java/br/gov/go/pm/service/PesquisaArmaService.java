package br.gov.go.pm.service;

import br.gov.go.pm.dao.ArmaDAO;
import br.gov.go.pm.modelo.Arma;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PesquisaArmaService {

	@Autowired
	private ArmaDAO dao;

	public List<Arma> buscarProprietarios() {
		return dao.buscarProprietarios();
	}

	public List<Arma> buscarTodasAsArmas(){
		return dao.buscarTodos();
	}

	public List<Arma> buscarArmasPorProprietario(String cpf) {
    	return dao.buscarArmasPorProprietario(cpf);
	}

	public List<Arma> buscarProprietariosEspecificos(String rgProprietario, String cpfNovoProprietario) {
		return dao.buscarProprietariosEspecificos(rgProprietario, cpfNovoProprietario);
	}

	public List<Arma> buscarArmasSemSigma() {
		return dao.buscarArmasSemSigma();
	}

	public List<Arma> buscarArmaEspecifica(String numeroArma) {
		return dao.buscarArmaEspecifica(numeroArma);
	}

	public List<Arma> buscarArmasSemProprietarios() {
		return dao.buscarArmasSemProprietarios();
	}

	public List<Arma> buscarArmasComRestricao() {
		return dao.buscarArmasComRestricao();
	}

	/**gera lista do item a ser exibido*/
	public String gerarListaDeProprietariosDeArmasParaImprimir(List<Arma>listaDeArmas ) {
		List<String>lista = new ArrayList<>(); //para o resultado final
		List<Arma>list; //para os proprietários

		final String INDICADOR = "- ";

		list = dao.buscarProprietarios();
		list.removeIf(p -> p.getCpfNovoProprietario().equals("0"));

		lista.add("Total de Proprietários: ".concat(String.valueOf(list.size())));
		list.forEach(l -> {
			    long qtdArmas = (listaDeArmas.isEmpty() ? 0 : listaDeArmas.stream().filter(p -> p.getCpfNovoProprietario().equals(l.getCpfNovoProprietario())).count());

				lista.add((char) 13 + INDICADOR.concat(l.getNomeNovoProprietario()).concat(",  ")
						.concat((l.getCpfNovoProprietario()).concat(",  ")
						.concat((l.getNumeroSigma() == null ? "Sem Sigma" : l.getNumeroSigma())).concat(",  ")
						.concat(l.getRgProprietario()).concat(",  ")
						.concat("Possui ".concat(String.valueOf(qtdArmas)).concat(" arma(s)"))));
		});





		String str = lista.toString();
		String strFormatado = str.substring(1, str.length() - 1);

		return strFormatado;
	}

	/**gera lista do item a ser exibido*/
	public String gerarListaArmasParaImprimir(List<Arma>list ) {
		List<String>lista = new ArrayList<>();
		final String INDICADOR = "- ";

		lista.add("Total de Armas: ".concat(String.valueOf(list.size())));
		list.forEach(l -> {
			lista.add((char)13 + INDICADOR.concat(l.getModelo().getModelo()).concat(",  ")
					.concat((l.getNumeroArma() == null ? "Não numerada":l.getNumeroArma())).concat(",  ")
					.concat((l.getNumeroSigma() == null ? "Sem Sigma" : l.getNumeroSigma())).concat(",  ")
					.concat((l.getCpfNovoProprietario() == null || l.getCpfNovoProprietario().equals("0") ? "Sem CPF" : l.getCpfNovoProprietario()).concat(",  ")
							.concat(l.getNomeNovoProprietario() == null ? "Sem Nome proprietário" : l.getNomeNovoProprietario()).concat(",  ")
							.concat(l.getEnunsArmas().getStatusRestritivo() == null ? "Sem Restrição" : l.getEnunsArmas().getStatusRestritivo().getDescricao().concat(" "))));
		});

		String str = lista.toString();
		String strFormatado = str.substring(1, str.length() - 1);

		return strFormatado;
	}


}
