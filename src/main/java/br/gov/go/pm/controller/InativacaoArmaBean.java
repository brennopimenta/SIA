package br.gov.go.pm.controller;

import br.gov.go.pm.enuns.Status;
import br.gov.go.pm.modelo.Arma;
import br.gov.go.pm.modelo.ArmaInativa;
import br.gov.go.pm.service.InativacaoArmaService;
import br.gov.go.pm.service.general.AtualizaPaginaService;
import br.gov.go.pm.util.Redirect;
import br.gov.go.pm.util.exception.NegocioException;
import br.gov.go.pm.util.jsf.FacesUtil;
import br.gov.go.pm.utilitarios.MostraDataAtual;
import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
public class InativacaoArmaBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private Logger log = Logger.getLogger(getClass());

	private ArmaInativa armaInativa;
	private ArmaInativa armaInativaSelecionada;
	private ArmaInativa armaInativaSelecionadaJustificativa;
	private Arma arma;
	private boolean habilitado;
	private boolean salvaArma = true;
	public  String titulo;
    private String busca;

	private List<Status> status;
	private List<ArmaInativa> armaInativacoes = new ArrayList<>();

	@ManagedProperty(value = "#{inativacaoArmaService}")
	private InativacaoArmaService inativacaoArmaService;

	@ManagedProperty(value = "#{atualizaPaginaService}")
	private AtualizaPaginaService atualizaPaginaService;

    public void preRender() {

        final String paramInativa = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("inativaArma");
		final String paramAtiva = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("ativaArma");

		try {

			if (paramInativa != null) {
				acoesInativar(paramInativa);
			}

			if (paramAtiva != null) {
				acoesAtivar(paramAtiva);
			}
		}catch (NegocioException ne) {
			FacesUtil.addErrorMessage(ne.getMessage());
			habilitado = false;
		}
	}

	private void acoesInativar(String paramInativa) throws NegocioException {
		titulo = "Inativar/Baixar Arma";
		status = Arrays.asList(Status.BAIXADA, Status.EXTRAVIADA);
		arma = buscarArmaPeloCodigo(Long.valueOf(paramInativa));
		verificaArma(arma,"inativa");

	}

	private void acoesAtivar(String paramAtiva) throws NegocioException {
		titulo = "Ativar/Levantar Arma";
		armaInativa.setMotivo(Status.ATIVA);
		status = Arrays.asList(Status.ATIVA);
		arma = buscarArmaPeloCodigo(Long.valueOf(paramAtiva));
		verificaArma(arma, "ativa");

	}

	private void verificaArma(Arma arma, String param) throws NegocioException {

    	if (arma != null) {
			switch (param) {
				case "inativa":
					inativacaoArmaService.verificaCondicaoInativa(arma);
					break;
				case "ativa":
					inativacaoArmaService.verificaCondicaoAtiva(arma);
					break;
			}
		}else{
			throw new NegocioException("Ops! Arma não existe.");
		}
	}


	@PostConstruct
	public void inicializar() {
    	limpar();
		buscarTodos();

	}

	public void buscar(){

        if (busca.equals("")) {
            buscarTodos();
        }else {
            armaInativacoes = inativacaoArmaService.listarAtivacaoInativacaoPelaArma(busca);
        }
    }

	public Arma buscarArmaPeloCodigo(Long codigo){
	    return inativacaoArmaService.buscarArmaPeloCodigo(codigo);
    }

	public void buscarTodos() {
		armaInativacoes = inativacaoArmaService.buscarTodos();
	}

    /**utilizado para mostrar a data atual em um campo data somente leitura */
    public Date getDataDeHoje(){return MostraDataAtual.getDataDeHoje();}


	public void salvar() {
		try {
			/**a opção salvar pode ficar desabilitada caso haja algum erro no parâmetro ou a arma não seja encontrda
			 * e esse booleano garante que não haja uma possível fraude no inspect do browser.*/
			if (isHabilitado()) {

				if (armaInativaSelecionada.getCodigo() == null){
					armaInativa.setArma(arma);}

					inativacaoArmaService.salvar(armaInativa, salvaArma);
					atualizaPaginaService.AtualizaPagina();
					limpar();
					FacesUtil.addSuccessMessage("Processo concluído com sucesso!");

			} else {
				FacesUtil.addSuccessMessage("Ops! Houve violação no processo, não foi possível salvar.");
			}
		}catch (Exception e){
    		FacesUtil.addErrorMessage(e.getMessage() + ". Processo não completado");
		}
	}

	public void edicao(){
      if (armaInativaSelecionada.getMotivo().equals(Status.ATIVA))
      	status = Arrays.asList(Status.ATIVA);
      else
		  status = Arrays.asList(Status.BAIXADA, Status.EXTRAVIADA);

      armaInativa.setMotivo(armaInativaSelecionada.getMotivo());
      setArmaInativa(armaInativaSelecionada);
      salvaArma = false;

	}

	public void limpar() {
    	habilitado = true;
		armaInativa = new ArmaInativa();
        arma = new Arma();
		armaInativaSelecionada = new ArmaInativa();
		armaInativacoes = new ArrayList<>();
	}


	//getters and setters


	public ArmaInativa getArmaInativa() {
		return armaInativa;
	}

	public void setArmaInativa(ArmaInativa armaInativa) {
		this.armaInativa = armaInativa;
	}

	public ArmaInativa getArmaInativaSelecionada() {
		return armaInativaSelecionada;
	}

	public void setArmaInativaSelecionada(ArmaInativa armaInativaSelecionada) {
		this.armaInativaSelecionada = armaInativaSelecionada;
	}

	public List<ArmaInativa> getArmaInativacoes() {
		return armaInativacoes;
	}

	public void setArmaInativacoes(List<ArmaInativa> armaInativacoes) {
		this.armaInativacoes = armaInativacoes;
	}

	public void setInativacaoArmaService(InativacaoArmaService inativacaoArmaService) {
		this.inativacaoArmaService = inativacaoArmaService;
	}

	public AtualizaPaginaService getAtualizaPaginaService() {
		return atualizaPaginaService;
	}

	public void setAtualizaPaginaService(AtualizaPaginaService atualizaPaginaService) {
		this.atualizaPaginaService = atualizaPaginaService;
	}

	public List<Status> getStatus() {
		return status;
	}

	public void setStatus(List<Status> status) {
		this.status = status;
	}

    public Arma getArma() {
        return arma;
    }

    public void setArma(Arma arma) {
        this.arma = arma;
    }

	public boolean isHabilitado() {
		return habilitado;
	}

	public void setHabilitado(boolean habilitado) {
		this.habilitado = habilitado;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public boolean isSalvaArma() {
		return salvaArma;
	}

	public void setSalvaArma(boolean salvaArma) {
		this.salvaArma = salvaArma;
	}

    public String getBusca() {
        return busca;
    }

    public void setBusca(String busca) {
        this.busca = busca;
    }

	public ArmaInativa getArmaInativaSelecionadaJustificativa() {
		return armaInativaSelecionadaJustificativa;
	}

	public void setArmaInativaSelecionadaJustificativa(ArmaInativa armaInativaSelecionadaJustificativa) {
		this.armaInativaSelecionadaJustificativa = armaInativaSelecionadaJustificativa;
	}
}
