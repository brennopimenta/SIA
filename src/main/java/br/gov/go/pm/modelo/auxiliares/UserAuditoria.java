//package br.gov.go.pm.modelo.auxiliares;
//
//import br.gov.go.pm.modelo.Grupo;
//import br.gov.go.pm.modelo.Usuario;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import javax.persistence.*;
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//@Entity
//@Data
//@Builder
//@AllArgsConstructor
//@NoArgsConstructor
//@Table(name = "auditoria_usuario")
//public class UserAuditoria implements Serializable {
//
//	private static final long serialVersionUID = 1L;
//
//	@Id
//	@GeneratedValue(strategy=GenerationType.IDENTITY)
//	private Long codigo;
//
//	@ManyToOne
//	@JoinColumn(name="codigo_auditoria")
//	private Usuario usuario;
//
//	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//	@JoinTable(name = "usuario_grupo", joinColumns = @JoinColumn(name="usuario_codigo"),
//			inverseJoinColumns = @JoinColumn(name = "grupo_codigo"))
//	private List<Grupo> grupos = new ArrayList<>();
//
//
//	@Temporal(TemporalType.TIMESTAMP)
//	@Column(name="data_de_criacao")
//	private Date dataCriacao;
//
//	@Temporal(TemporalType.TIMESTAMP)
//	@Column(name="data_de_modificacao")
//	private Date dataModificacao;
//
//	@PrePersist
//	@PreUpdate
//	public void configuraDatasCriacaoAlteracao() {
//		dataModificacao = new Date();
//
//		if (dataCriacao == null) {
//			dataCriacao = new Date();
//		}
//	}
//
//}