package br.gov.go.pm.modelo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "grupo")
public class Grupo implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long codigo;

	@Column(nullable=false, length=40)
	@NotNull(message = "Por favor insira o nome!")
	private String nome;

	@Column(nullable=false, length=80)
	@NotNull(message = "Por favor insira uma descrição!")
	private String descricao;

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