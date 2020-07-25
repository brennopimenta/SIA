package br.gov.go.pm.modelo;

import br.gov.go.pm.enuns.StatusSigma;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**Numero será preenchido após o retorno do processo de aprovação do exército, o qual fornece esse numero.
 * e não aparece no formulário de cadastro sendo posteriormente.*/



@NamedQueries({
	/**método usado no Atribuir Sigma em Arma*/
	@NamedQuery(name="ArmaNumeroSigma.buscarArmaSelecionada", query="SELECT ns FROM ArmaNumeroSigma ns WHERE ns.numeroArma = :arma"),
	@NamedQuery(name="ArmaNumeroSigma.buscarPorArmaOuNumeroSigma", query="SELECT ns FROM ArmaNumeroSigma ns WHERE ns.numeroArma = :arma or ns.numeroSigma =:sigma"),
	@NamedQuery(name="ArmaNumeroSigma.buscarPelaArmaENumeroSigma", query="SELECT ns FROM ArmaNumeroSigma ns WHERE ns.numeroArma = :arma and ns.numeroSigma =:sigma")
	})

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "arma_numero_sigma")
public class ArmaNumeroSigma implements Serializable {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long codigo;

	@Column(name="sigma")
	@NotNull(message = "Por favor insira o numero do sigma fornecido pelo orgão responsável")
	private String numeroSigma;

	/** este numero arma é o que vem do exército e será comparado com a arma existente no sistema,
	 * mas pode ser preenchido pela adm manualmente.*/
	@Column(name="numero_arma_exercito")
	@NotNull(message = "Por favor insira o numero da Arma")
	private String numeroArma;

	@Column(name="data_criacao")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataCriacao;

	@Column(name="data_modificacao")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataModificacao;

	@OneToOne
	@JoinColumn(name="codigo_usuario")
	private Usuario usuarioSistema;

	@Enumerated(EnumType.STRING)
	@Column(name="status")
	private StatusSigma status;

	@PrePersist
	@PreUpdate
	public void configuraDatasCriacaoAlteracao() {
		this.dataModificacao = new Date();	
		if (this.dataCriacao == null) {
			this.dataCriacao = new Date();
			
		}
	}



}
