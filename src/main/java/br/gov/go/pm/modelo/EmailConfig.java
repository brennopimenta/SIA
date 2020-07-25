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
@Table(name = "email_config")
public class EmailConfig implements Serializable {

	private static final long serialVersionUID = 1L;
	/* SERVIDOR_SMTP = "smtp.gmail.com";
        PORTA_SERVIDOR = "465";
        EMAIL = "bancodedadosdtic@gmail.com";
        SENHA = "pmgo2018";
        MENSAGEM = "Atenção, seu Craf está pronto! Por favor compareça à DECAE na CALTI";
        */


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long codigo;

	@Column(nullable = false, name = "servidor_smtp", length = 200)
	@NotNull(message = "Deve ser inserido um Servidor smtp!")
	private String smtp;

	@Column(nullable = false, name = "porta_servidor", length = 50)
	@NotNull(message = "Deve ser inserido uma porta!")
	private String porta;

	@Column(nullable = false, name = "email", length = 200)
	@NotNull(message = "Deve ser inserido um email!")
	private String email;

	@Column(nullable = false, name = "senha", length = 200)
	@NotNull(message = "Deve ser inserido uma senha!")
	private String senha;

	@Column(nullable = false, name = "mensagem", length = 300)
	@NotNull(message = "Deve ser inserido uma mensagem ao usuário!")
	private String mensagem;

	private Boolean status;

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