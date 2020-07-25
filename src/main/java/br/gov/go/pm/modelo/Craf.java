package br.gov.go.pm.modelo;

import br.gov.go.pm.enuns.Assinatura;
import br.gov.go.pm.enuns.StatusCraf;
import br.gov.go.pm.enuns.StatusEmissao;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.awt.*;
import java.io.Serializable;
import java.util.Date;


@NamedQueries({
	@NamedQuery(name="Craf.buscarTodos", query="SELECT c from Craf c"),
	//Busca os crafs por Arma usada no setar do CrafBean, para verificar se existe um craf para esta arma cujo status NÃO seja cancelado(pois traz só um item)
	@NamedQuery(name="Craf.buscarCrafPorArma", query="SELECT c FROM Craf c WHERE c.arma.numeroArma = :numeroArma and c.status <> br.gov.go.pm.enuns.StatusCraf.CANCELADO"),
	
	//utilizado no CrafDAO
	@NamedQuery(name="Craf.buscarTodosCrafAssinadosPorArmaOuCraf", query="SELECT c FROM Craf c WHERE "
			+ "c.arma.numeroArma LIKE :numeroArma and c.status = br.gov.go.pm.enuns.StatusCraf.ASSINADO or c.numeroCraf = :numeroCraf and c.status = br.gov.go.pm.enuns.StatusCraf.ASSINADO"),
	@NamedQuery(name="Craf.buscarTodosCrafAssinadosPorArmaECraf", query="SELECT c FROM Craf c WHERE "
			+ "c.arma.numeroArma LIKE :numeroArma and c.numeroCraf LIKE :numeroCraf and c.status = br.gov.go.pm.enuns.StatusCraf.ASSINADO "),
	
	/**utilizado para busca em PesquisaCrafsNaoAssinados*/
	@NamedQuery(name="Craf.buscarTodosNaoAssinados", query="SELECT c from Craf c WHERE c.status = br.gov.go.pm.enuns.StatusCraf.PENDENTE"),
	/**utilizado para busca em PesquisaCrafsAssinados*/
	@NamedQuery(name="Craf.buscarTodosAssinados", query="SELECT c from Craf c WHERE c.status = br.gov.go.pm.enuns.StatusCraf.ASSINADO"),
	/**utilizado para busca em PesquisaCrafsAssinados e nao impressos. Utilizado como inicio da lista*/
	@NamedQuery(name="Craf.buscarTodosAssinadosNaoImpressos", query="SELECT c from Craf c WHERE c.status = br.gov.go.pm.enuns.StatusCraf.ASSINADO and c.statusEmissao = br.gov.go.pm.enuns.StatusEmissao.NAO_IMPRESSO")
})


//usuarios = repository.createQuery("SELECT a FROM Usuario a WHERE a.nome LIKE :nome")
//		.setParameter("nome", "%" + nome + "%")
//		.getResultList();

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Craf implements Serializable {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long codigo;

	@NotNull(message = "Rg é obrigatória")
	private String rg;

	@NotNull(message = "C.P.F é obrigatório")
	private String cpf;

	@NotNull(message = "Nome é obrigatório")
	private String nome;

	@NotNull(message = "Orgão expedidor é obrigatório")
	@Column(name="Orgao_Expedidor")
	private String orgaoExpedidor = "PMGO";

	@NotNull(message = "Numero Craf é obrigatório")
	@Column(name="numero_craf")
	private String numeroCraf;

	@NotNull(message = "Abrangencia do Craf é obrigatória")
	@Column(name="Abrangencia_Craf")
	private String abrangenciaCraf = "NACIONAL";

	@NotNull(message = "Validade do Craf é obrigatória")
	@Column(name="Validade_Craf")
	private String validadeCraf = "INDETERMINADO";

	@NotNull(message = "B.G de publicação é obrigatório")
	private String publicacao;

	@Enumerated(EnumType.STRING)
	private StatusCraf status;

	@Column(name="Data_Expedicao")
	private Date dataExpedicao;

	@Column(name="Data_Modificacao")
	private Date dataModificacao;

	@OneToOne
	@JoinColumn(name="emissor")
	private Usuario emissor;

	@Column(name="status_emissao")
	@Enumerated(EnumType.STRING)
	private StatusEmissao statusEmissao;

	@ManyToOne
	@NotNull(message = "Arma é obrigatória")
	@JoinColumn(name = "arma_id", nullable = false)
	private Arma arma;

	@Column(name="assinatura")
	@Enumerated(EnumType.STRING)
	private Assinatura assinatura;

	@OneToOne
	@JoinColumn(name="autor_assinatura")
	private Assinaturas autorAssinatura;

	@Transient
	private Image imgAssinatura;

	/**Configuração para data de criação e modificação usando as anotações que possibilitam fazer as alterações
	 * especificadas antes de fechar o entity manager*/

	@PrePersist
	@PreUpdate
	public void configuraDatasCriacaoAlteracao() {
		this.dataModificacao = new Date();
		
		if (this.dataExpedicao == null) {
			this.dataExpedicao = new Date();
		}
	}

	
	
}
