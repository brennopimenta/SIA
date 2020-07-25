package br.gov.go.pm.modelo;

import br.gov.go.pm.enuns.TipoDoc;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;


@NamedQueries({
	@NamedQuery(name="Sigma.buscarTodos", query="select s from Sigma s"),
	@NamedQuery(name="Sigma.buscarArmaSelecionada", query="SELECT s FROM Sigma s WHERE s.arma.numeroArma = :armaSigma"),
	@NamedQuery(name="Sigma.buscarSigmasPorArmaOuCodigo", query="SELECT s FROM Sigma s WHERE "
				+ "s.arma.numeroArma = :numeroArma or s.codigo = :codigo "),
	@NamedQuery(name="Sigma.buscarSigmasPorArmaECodigo", query="SELECT s FROM Sigma s WHERE "
			+ "s.arma.numeroArma = :numeroArma and s.codigo = :codigo ")
	})

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Sigma implements Serializable {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long codigo;

	@NotEmpty
	@Column(length = 11)
	private String cpf;

	@Column(length = 50)
	private String rg;

	@Column(length = 200)
	private String nome;

	@Column(length = 100)
	@Enumerated(EnumType.STRING)
	private TipoDoc tipoDoc;

	@Column(length = 200)
	private String numeroDoc;

	@Embedded
	@Column(length = 50)
	private Telefone telefone;

	@Embedded
	private EnderecoResidencial enderecoResidencial;

	@Embedded
	private EnderecoFuncional enderecoFuncional;

	@Embedded
	private DadosPessoaisSigma dadosPessoaisSigma;
	private String postoGraduacao;

	@NotNull(message="Arma é Obrigatória")
	@OneToOne
	@JoinColumn(name="codigo_arma", unique=true)
	private Arma arma;

	@OneToOne
	@JoinColumn(name="codigo_usuario")
	private Usuario usuarioSistema;

	@ManyToOne
	private Carga carga;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_de_criacao")
	private Date dataCriacao;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_de_modificacao")
	private Date dataModificacao;

	@PrePersist
	@PreUpdate
	public void configuraDatasCriacaoAlteracao() {
		dataModificacao = new Date();

		if (dataCriacao == null) {
			dataCriacao = new Date();
		}
	}

	public Telefone getTelefone() {
		if (telefone == null){
			telefone = new Telefone();
		}
		return telefone;
	}
	public void setTelefone(Telefone telefone) {
		this.telefone = telefone;
	}

	public EnderecoResidencial getEnderecoResidencial() {
		if (enderecoResidencial == null){
			enderecoResidencial = new EnderecoResidencial();
		}
		return enderecoResidencial;
	}
	public void setEnderecoResidencial(EnderecoResidencial enderecoResidencial) {
		this.enderecoResidencial = enderecoResidencial;
	}

	public EnderecoFuncional getEnderecoFuncional() {
		if (enderecoFuncional == null){
			enderecoFuncional = new EnderecoFuncional();
		}
		return enderecoFuncional;
	}
	public void setEnderecoFuncional(EnderecoFuncional enderecoFuncional) {
		this.enderecoFuncional = enderecoFuncional;
	}

	public DadosPessoaisSigma getDadosPessoaisSigma() {
		if (dadosPessoaisSigma == null) {
			dadosPessoaisSigma = new DadosPessoaisSigma();
		}
		return dadosPessoaisSigma;

	}

	public void setDadosPessoaisSigma(DadosPessoaisSigma dadosPessoaisSigma) {
		this.dadosPessoaisSigma = dadosPessoaisSigma;
	}

	public Long getCodigo() {
		if(this.codigo == null){
			codigo = null;
		}
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}


}
