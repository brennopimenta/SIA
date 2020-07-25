package br.gov.go.pm.modelo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import br.gov.go.pm.enuns.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;


@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "arma_inativacoes")
public class ArmaInativa implements Serializable {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long codigo;

	@ManyToOne
	@JoinColumn(name="codigo_arma", nullable=false)
	private Arma arma;	

	@Enumerated(EnumType.STRING)
	private Status motivo;

	@Column(length = 500)
	@NotBlank(message = "A justificativa é obrigatória")
	private String justificativa;

	@Column(length = 2000)
	private String processos;

	@Column(name="boletim", length = 50)
	@NotBlank(message = "O boletim é obrigatório")
	private String boletim = "N";

	@Temporal(TemporalType.TIMESTAMP)
	private Date data;

	@OneToOne
	@JoinColumn(name="codigo_usuario")
	private Usuario usuarioSistema;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_criacao")
	private Date dataCriacao;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_modificacao")
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

