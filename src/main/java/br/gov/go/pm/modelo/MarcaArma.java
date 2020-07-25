package br.gov.go.pm.modelo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "marca_arma")
public class MarcaArma implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long codigo;

	@NotBlank(message="Por favor insira a marca da arma.")
	private String marca;

	private String descricao;

	@Length(max = 4)
	@Column(name="codigo_orgao")
	private String codigoOrgao;


}
