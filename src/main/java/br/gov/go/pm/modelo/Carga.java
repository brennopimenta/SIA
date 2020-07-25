package br.gov.go.pm.modelo;

import br.gov.go.pm.enuns.StatusCarga;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Carga implements Serializable {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long codigo;

//	@ManyToMany(cascade = CascadeType.ALL,  fetch=FetchType.LAZY)
//	@JoinTable(name = "carga_arma", joinColumns = @JoinColumn(name="carga_codigo"),
//			inverseJoinColumns = @JoinColumn(name = "arma_codigo"))
//	private List<Arma> armas = new ArrayList<>();

	@ManyToMany(cascade = CascadeType.ALL,  fetch=FetchType.LAZY)
	@JoinTable(name = "carga_sigma_arma", joinColumns = @JoinColumn(name="carga_codigo"),
			inverseJoinColumns = @JoinColumn(name = "sigma_codigo"))
	private List<Sigma> sigmas = new ArrayList<>();

	@Enumerated(EnumType.STRING)
	private StatusCarga status;

	@Column(name="boletim_de_inclusao")
	private String boletimInclusao;

	@Enumerated(EnumType.STRING)
	@Column(name="status_envio_boletim")
	private StatusCarga statusEnvioBoletim;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_de_criacao")
	private Date dataCriacao;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_de_modificacao")
	private Date dataModificacao;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_de_envio")
	private Date dataEnvio;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_craf")
	private Date dataGeracaoCraf;

	@OneToOne
	@JoinColumn(name="emissor")
	private Usuario emissor;

	@OneToOne
	@JoinColumn(name="emissor_craf")
	private Usuario emissorCraf;

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
