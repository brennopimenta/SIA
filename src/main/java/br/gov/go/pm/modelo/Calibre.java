package br.gov.go.pm.modelo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "calibre")
public class Calibre implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long codigo;

	@Column(length=5)
	@NotBlank(message = "Por favor insira a descrição do calibre")
	private String calibre;

	@OneToOne
	@JoinColumn(name="grupo_calibre")
	private GrupoCalibre grupoCalibre;

	/**interessa apenas o nome do usuário do sistema*/
	@Column(length = 200, name = "usuario_adm")
	private String usuarioAdministrador;

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



}