package br.gov.go.pm.modelo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@NamedQueries({
		@NamedQuery(name="Arma.buscarTodos", query="select a from Arma a"),
		//essa consulta busca a arma também, porém apenas o número da arma, não traz o objeto todo, para simplificar ao máximo o select.
		@NamedQuery(name="Arma.buscarNumeroArma", query="SELECT a.numeroArma FROM Arma a WHERE a.numeroArma = :arma"),
		//retorna lista de armas filtrando pelo numero da arma
		@NamedQuery(name="Arma.buscarArmaEspecifica", query="SELECT a FROM Arma a WHERE a.numeroArma = :arma"),
		//busca a armas por proprietário
		@NamedQuery(name="Arma.buscarArmaPorProprietario", query="SELECT a FROM Arma a WHERE a.cpfNovoProprietario = :cpf"),
		//@NamedQuery(name="Arma.buscarArmaPorProprietario", query="SELECT a FROM Arma a WHERE a.codigo = :codigo"),
		     @NamedQuery(name="Arma.buscarTodosProprietariosArma", query="select a FROM Arma a "),
		@NamedQuery(name="Arma.buscarTodosProprietariosArmaPorRg", query="select a FROM Arma a WHERE a.rgProprietario = :rg" ),
		@NamedQuery(name="Arma.buscarTodosProprietariosArmaPorCpf", query="select a FROM Arma a WHERE a.cpfNovoProprietario = :cpf")

})

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Arma implements Serializable {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long codigo;

	@Column(name = "numero_arma", unique = true)
	@NotNull(message="O numero da Arma é obrigatório")
	private String numeroArma;

	@ManyToOne
	@JoinColumn(name="codigo_marca_arma")
	@NotNull(message="Marca da Arma é obrigatória")
	private MarcaArma marca;

	private Integer capacidade;

	@Column(name="comprimento_do_cano")
	private String comprimentoCano;

	//Enuns
	private ArmaEnuns enunsArmas;

	//
	@Column(name="registro_e_orgao")
	private String numeroRegEOrgao;

	@ManyToOne
	@JoinColumn(name="codigo_modelo")
	@NotNull(message="Modelo da Arma é obrigatório")
	private ArmaModelo modelo;

	@Column(name="numero_de_canos")
	private Integer numeroDeCanos = 01;
	@Column(name="quantidade_de_raias")
	private Integer qtdDeRaias;
	@Column(name="nota_fiscal")
	private String notaFiscal;
	@Column(name="canos_sobressalentes")
	private String canosSobressalentes;
	private String obs;

	@OneToOne
	@JoinColumn(name="calibre")
	@NotNull(message="Calibre da Arma é obrigatório")
	private Calibre calibre;

	@ManyToOne
	@JoinColumn(name="pais_origem")
	@NotNull(message="País de origem da arma é obrigatório")
	private Paises pais;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_de_criacao")
	private Date dataCriacao;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_de_modificacao")
	private Date dataModificacao;


	@Column(name="cpf_proprietario")
	private String cpfNovoProprietario;

	@Column(name="nome_proprietario")
	private String nomeNovoProprietario;

	@NotBlank(message="Estabelecer se o proprietário da arma é militar ou civil é obrigatório")
	@Column(name="civil_militar")
	private String situacaoProprietario;

	@Column(name="rg_proprietario")
	private String rgProprietario;

	@Column(length = 200, name="numero_sigma", unique=true)
	private String numeroSigma;

	@Column(name="boletim_de_inclusao")
	private String boletimInclusao;

	public ArmaEnuns getEnunsArmas() {
		if (enunsArmas == null){
			enunsArmas = new ArmaEnuns();
		}
		return enunsArmas;
	}

	public void setEnunsArmas(ArmaEnuns enunsArmas) {
		this.enunsArmas = enunsArmas;
	}

	@OneToOne
	@JoinColumn(name="codigo_usuario")
	private Usuario usuarioSistema;

	/**Configuração para data de criação e modificação usando as anotações que possibilitam fazer as alterações
	 * especificadas antes de fechar o entity manager*/

	@PrePersist
	@PreUpdate
	public void configuraDatasCriacaoAlteracao() {
		dataModificacao = new Date();

		if (dataCriacao == null) {
			dataCriacao = new Date();
		}
	}



}


