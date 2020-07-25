package br.gov.go.pm.modelo;

import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;


@NamedQueries({
	//retorna lista de armas filtrando por proprietario e pelo numero da arma
	@NamedQuery(name="ArmaTransferencia.buscarTransferenciasPorArmaEProp", query="SELECT at FROM ArmaTransferencia at WHERE "
			+ "at.arma.numeroArma = :arma and at.novoProprietarioCpf = :cpf "),
	//retorna lista de armas filtrando por proprietario ou pelo numero da arma
		@NamedQuery(name="ArmaTransferencia.buscarTransferenciasPorArmaOuProp", query="SELECT at FROM ArmaTransferencia at WHERE "
				+ "at.arma.numeroArma = :arma or at.novoProprietarioCpf = :cpf ")

})

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "arma_transferencia")
public class ArmaTransferencia {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long codigo;

	@ManyToOne
	@JoinColumn(name="codigo_arma", nullable=false)
	private Arma arma;

	@Column(name="boletim", length = 50)
	private String boletim;

	@OneToOne
	@JoinColumn(name = "ultimo_craf")
	private Craf craf;

	/** dados do Cedente(prorprietário atual) */

	@Column(name="situacao_cedente")
	private String cedSituacao;

	@Column(name="cpf_cedente")
	@NotBlank(message="CPF do Cedente é obrigatório")
	private String cedCpf;

	@Column(name="nome_cedente")
	@NotBlank(message="Nome do Cedente é obrigatório")
	private String cedNome;

	@Column(name="rg_cedente")
	@NotBlank(message="Rg do Cedente é obrigatório")
	private String cedRg;

	@Column(name="email_cedente")
	private String cedEmail;

	@Column(name="logradouro_cedente")
	private String cedLogradouro;

	@Column(name="bairro_cedente")
	private String cedBairro;

	@Column(name="cidade_cedente")
	private String cedCidade;

	@Column(name="uf_cedente")
	private String cedUf;

	@Column(name="celular_cedente")
	@NotNull(message="Telefone do Cedente é obrigatório")
	private String cedCelular;

	@Column(name="categoriaFuncional_cedente")
	private String cedCategoriaFuncional;
	
	/**dados do novo proprietário*/

	@Column(name="situacao_proprietario")
	private String novoPropSituacao;

	@NotNull(message="Por favor insira um proprietário!")
	@Column(name="cpf_novo_proprietario")
	private String novoProprietarioCpf;

	@NotBlank(message="Nome novo proprietário não pode ser vazio.")
	@Column(name="nome_novo_proprietario")
	private String novoPropNome;

	@NotNull(message="Por favor insira uma rg para o proprietário!")
	@Column(name="rg_novo_proprietario")
	private String novoPropRg;

	@Column(name="email_novo_proprietario")
	private String novoPropEmail;

	@Column(name="logradouro_novo_proprietario")
	private String novoPropLogradouro;

	@Column(name="bairro_novo_proprietario")
	private String novoPropBairro;

	@Column(name="cidade_novo_proprietario")
	private String novoPropCidade;

	@Column(name="uf_novo_proprietario")
	private String novoPropUf;

	@Column(name="celular_novo_proprietario")
	@NotNull(message="Telefone do Proprietário é obrigatório")
	private String novoPropCelular;

	@Column(name="categoria_funcional_novo_proprietario")
	private String novoPropCategoriaFuncional;
	/**/

	@OneToOne
	@JoinColumn(name="codigo_usuario")
	private Usuario usuarioSistema;

	@Temporal(TemporalType.TIMESTAMP)
	private Date dataCriacao;

	@Temporal(TemporalType.TIMESTAMP)
	private Date dataModificacao;
	
	/**Configuração para data de criação e modificação usando as anotações que possibilitam fazer as alterações
	 * especificadas antes de fechar o entity manager*/

	@PrePersist
	@PreUpdate
	public void configuraDatasCriacaoAlteracao() {
		this.dataModificacao = new Date();
		
		if (this.dataCriacao == null) {
			this.dataCriacao = new Date();
		}
	}


}//end class

