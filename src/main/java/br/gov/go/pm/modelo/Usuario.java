package br.gov.go.pm.modelo;

import br.gov.go.pm.enuns.Profile;
import br.gov.go.pm.enuns.StatusSituacao;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "usuario")
public class Usuario implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long codigo;

	@Column(nullable = false, length = 20)
	@NotNull(message = "Deve ser inserido um C.p.f!")
	private String cpf;

	@Column(length = 200)
	@NotNull(message = "Nome de ser inserido!")
	private String nome;

	@Column(length = 50)
	@NotNull(message = "Posto ou Graduação deve ser inserida!")
	private String graduacao;

	@Enumerated(EnumType.STRING)
	private Profile profile;

	@Column(length = 250)
	private String token;

	@Column(length = 50)
	private String senha;

	@Column(length = 350,name = "senha_assinatura")
	private String senhaAssinatura;

	@Enumerated(EnumType.STRING)
	private StatusSituacao status;

	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "usuario_grupo", joinColumns = @JoinColumn(name="usuario_codigo"),
			inverseJoinColumns = @JoinColumn(name = "grupo_codigo"))
	private List<Grupo> grupos = new ArrayList<>();

	@OneToOne
	@JoinColumn(name="usuario_sistema")
	private Usuario usuarioSistema;

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