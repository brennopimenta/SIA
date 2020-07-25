package br.gov.go.pm.modelo;

import br.gov.go.pm.enuns.UsoArma;
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
@Table(name = "grupo_calibre")
public class GrupoCalibre implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long codigo;

	@Column(length=5, unique = true)
	@NotNull(message = "Por favor insira uma codigo identificador!")
	private String identificador;

	@Column(nullable=false, length=150)
	@NotNull(message = "Por favor insira o nome do grupo!")
	private String nome;

	@Enumerated(EnumType.STRING)
	@Column(name="grau_restricao")
	private UsoArma grauRestricao;

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