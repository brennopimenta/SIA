package br.gov.go.pm.modelo;

import br.gov.go.pm.enuns.AcaoSistema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "usuario_auditoria")
public class UsuarioAuditoria implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long codigo;

	@Column(name="cpf_do_usuario")
	private String cpfUsuario;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_de_criacao")
	private Date dataCriacao;

	@Enumerated(EnumType.STRING)
	private AcaoSistema acao;

	@Column(length = 2000)
	private String dados;

	@PrePersist
	public void configuraDatasCriacaoAlteracao() {
		if (dataCriacao == null) {
			dataCriacao = new Date();
		}
	}

}