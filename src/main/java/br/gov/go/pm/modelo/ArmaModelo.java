package br.gov.go.pm.modelo;

import br.gov.go.pm.enuns.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "arma_modelo")
public class ArmaModelo implements Serializable {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long codigo;

	@Column(name = "modelo_arma")
	@NotNull(message="O modelo da Arma é obrigatório")
	private String modelo;

	@Column(name="numero_de_canos")
	private Integer numeroDeCanos = 01;

	private Integer capacidade;

	@Column(name="comprimento_do_cano")
	private String comprimentoCano;

	/*Banco de Dados*/
	@ManyToOne
	@JoinColumn(name="pais_origem")
	@NotNull(message="País de origem é obrigatório")
	private Paises pais;

	@ManyToOne
	@JoinColumn(name="codigo_marca_arma")
	@NotNull(message="Marca da Arma é obrigatória")
	private MarcaArma marca;
	/**/

    /*Enuns*/
	@NotNull(message="Espécie da arma é obrigatória")
	@Enumerated(EnumType.STRING)
	private EspecieArma especie;

	@Enumerated(EnumType.STRING)
	@Column(name="unidade_medida_do_cano")
	private UnidadeDeMedida unMedComprimentoDoCano;

	@Enumerated(EnumType.STRING)
	private Acabamento acabamento;

//	@Enumerated(EnumType.STRING)
//	private Funcionamento funcionamento;

	@OneToOne
	@JoinColumn(name="calibre")
	@NotNull(message="Calibre da Arma é obrigatório")
	private Calibre calibre;

	/**/

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_de_criacao")
	private Date dataCriacao;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_de_modificacao")
	private Date dataModificacao;

	@OneToOne
	@JoinColumn(name="codigo_usuario")
	private Usuario usuarioSistema;

	@Enumerated(EnumType.STRING)
	@Column(name="status")
	private StatusSituacao status;

	private byte[] foto;

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


