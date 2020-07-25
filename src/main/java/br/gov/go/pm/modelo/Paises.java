package br.gov.go.pm.modelo;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@NamedQueries({
	@NamedQuery(name="Paises.buscarTodos", query="SELECT p from Paises p"),
	@NamedQuery(name="Paises.buscarPaisesEspecificos", query="SELECT p FROM Paises p WHERE p.descricao = :pais")
	
})
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Paises implements Serializable {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long codigo;
	
	@Length(max = 3)
	private String sigla;

	@Length(max = 4)
	@Column(name="codigo_orgao")
	private String codigoOrgao;

	@NotBlank(message="Pais não pode ser nulo")
	private String descricao;




}