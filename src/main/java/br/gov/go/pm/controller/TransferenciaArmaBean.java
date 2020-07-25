package br.gov.go.pm.controller;

import br.gov.go.pm.enuns.Status;
import br.gov.go.pm.modelo.Arma;
import br.gov.go.pm.modelo.ArmaTransferencia;
import br.gov.go.pm.modelo.DadosSicad;
import br.gov.go.pm.service.TransferenciaArmaService;
import br.gov.go.pm.service.general.ConsumirServicoSicad;
import br.gov.go.pm.util.exception.NegocioException;
import br.gov.go.pm.util.jsf.FacesUtil;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;

@ManagedBean
@ViewScoped
public class TransferenciaArmaBean implements Serializable {

 private static final long serialVersionUID = 1L;

    private Logger log = Logger.getLogger(getClass());

	@ManagedProperty(value = "#{transferenciaArmaService}")
	private TransferenciaArmaService transferenciaArmaService;

	@ManagedProperty(value = "#{consumirServicoSicad}")
	private ConsumirServicoSicad consumirServicoSicad;

	private Arma arma;
	private ArmaTransferencia armaTransferencia;
	private ArmaTransferencia cedente;
	private ArmaTransferencia recebedor;
	private Arma proprietarioExistente;
	private DadosSicad ds;

	private Boolean camposReadyOnlyCedente = true;
	private Boolean tranferenciaCivil = false;
	private String labelAlterar = "Liberar Campos Endereço";
	private String labeltituloPagina;

	public void preRender() throws Exception {

		 String param = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("arma");
		 String paramCivil = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("armaCivil");

		/**    chama serviço da ssp para atribuir dados atuais do cedente(prop. atual) passando o cpf verificado em arma*/
		if(param != null) {
            buscaArma(param);
            atribuiDadosCedente();
			arma.setSituacaoProprietario("Militar");

            labeltituloPagina = "Transferir Arma de Militar para Militar";

		}else if(paramCivil !=null){
			buscaArma(paramCivil);
			atribuiDadosCedente();
			setaDadosArmaCivil();

			tranferenciaCivil = true;
			labeltituloPagina = "Tranferir Arma para SINARM";
		}
	}
	
	@PostConstruct
	public void init() {
		limpar();

	}

	public void buscaArma(String param){
		Arma armaResult = transferenciaArmaService.buscaArma(Long.valueOf(param));
		try {
			if (armaResult.getCpfNovoProprietario() == null || armaResult.getCpfNovoProprietario().equals("0")) {
				limpar();
				FacesUtil.addErrorMessage("Não é possivel transferir esta arma pois está sem proprietário!");

            }else if(armaResult.getNumeroSigma() == null){
				limpar();
				FacesUtil.addErrorMessage("Não é possivel transferir esta arma pois está sem sigma!");

			/**verifica se a arma não está baixada ou extraviada(algum impedimento)*/
			} else if(armaResult.getEnunsArmas().getStatusRestritivo() != null){
				limpar();
				FacesUtil.addErrorMessage("Não é possivel transferir esta arma pois ela está " + armaResult.getEnunsArmas().getStatusRestritivo().getDescricao());

			}else {
				setArma(armaResult);
			}
		}catch (Exception e ){
			FacesUtil.addErrorMessage("Erro " + e.getMessage() + " " + "ao buscar arma, refaça o processo ou contate o Administrador!");
		}
	}

	private void setaDadosArmaCivil(){
		arma.getEnunsArmas().setStatusRestritivo(Status.TRANSFERIDA_SINARM);
		arma.setSituacaoProprietario("Civil");
	}

	private void setaAntesDeSalvar() throws NegocioException {
		/** Seta no objeto arma para arma de transferencia*/
		armaTransferencia.setArma(arma);
		armaTransferencia.getArma().setCpfNovoProprietario(recebedor.getNovoProprietarioCpf());
		armaTransferencia.getArma().setNomeNovoProprietario(recebedor.getNovoPropNome());
		armaTransferencia.setCraf(transferenciaArmaService.buscarCrafPorArma(arma));

		/** Seta na transferencia o cedente da arma.*/
		armaTransferencia.setCedCpf(cedente.getCedCpf());
		armaTransferencia.setCedNome(cedente.getCedNome());
		armaTransferencia.setCedSituacao(cedente.getCedSituacao());
		armaTransferencia.setCedRg(cedente.getCedRg());
		armaTransferencia.setCedCategoriaFuncional(cedente.getCedCategoriaFuncional());
		armaTransferencia.setCedCelular(cedente.getCedCelular());
		armaTransferencia.setCedLogradouro(cedente.getCedLogradouro());
		armaTransferencia.setCedBairro(cedente.getCedBairro());
		armaTransferencia.setCedCidade(cedente.getCedCidade());
		armaTransferencia.setCedUf(cedente.getCedUf());
		armaTransferencia.setCedEmail(cedente.getCedEmail());

		/** Seta em transferencia o recebedor(novo proprietário) da arma*/
		armaTransferencia.setNovoProprietarioCpf(recebedor.getNovoProprietarioCpf());
		armaTransferencia.setNovoPropNome(recebedor.getNovoPropNome());
		armaTransferencia.setNovoPropSituacao(recebedor.getNovoPropSituacao());
		armaTransferencia.setNovoPropRg(recebedor.getNovoPropRg());
		armaTransferencia.setNovoPropCategoriaFuncional(recebedor.getNovoPropCategoriaFuncional());
		armaTransferencia.setNovoPropCelular(recebedor.getNovoPropCelular());
		armaTransferencia.setNovoPropLogradouro(recebedor.getNovoPropLogradouro());
		armaTransferencia.setNovoPropBairro(recebedor.getNovoPropBairro());
		armaTransferencia.setNovoPropCidade(recebedor.getNovoPropCidade());
		armaTransferencia.setNovoPropUf(recebedor.getNovoPropUf());
		armaTransferencia.setNovoPropEmail(recebedor.getNovoPropEmail());

	}
		
	public void salvar() {
		try {
		        setaAntesDeSalvar();
				transferenciaArmaService.salvar(armaTransferencia);

				FacesUtil.addSuccessMessage("Transferência realizada com sucesso!");
				limpar();
		} catch (NegocioException ne) {
		    log.error(ne.getMessage());
			FacesUtil.addErrorMessage("Transferencia não realizada, refaça o processo ou contate o Administrador. Erro:  " + ne.getMessage());

		} catch (Exception e) {
			log.error(e.getMessage());
			FacesUtil.addErrorMessage("Erro, refaça o processo. Caso persista o erro contatar o administrador");

		}
	
	}
	
	/**atribui o dados ao cedente.*/
	public void atribuiDadosCedente() {
		
		try{
		/** Se houver um proprietário da arma e for militar atribui este ao objeto cedente bem como os seus dados atuais no campo cedente.*/
		if (arma.getSituacaoProprietario().equals("Militar")){

		    cedente.setCedCpf(arma.getCpfNovoProprietario());
		    cedente.setCedNome(arma.getNomeNovoProprietario());

		    /**busca no servidor da ssp, na ficha, os dados*/
			ds = consumirServicoSicad.busca(cedente.getCedCpf());
            if (ds != null) {
                cedente.setCedLogradouro(ds.getResLogradouro());
                cedente.setCedBairro(ds.getResBairro());
                cedente.setCedCidade(ds.getResCidade());
                cedente.setCedUf(ds.getResUf());
                cedente.setCedSituacao(ds.getStatus());
                cedente.setCedCategoriaFuncional(ds.getPostoSiglaGrad());
                cedente.setCedRg(ds.getRgMilitar());
                cedente.setCedEmail(ds.getEmail());
            }else{
                FacesUtil.addErrorMessage("Não foi possível obter dados de endereço do cedente, por favor verifique sua conexão com a intranet ou contate o administrador. Campos liberados.");
                /**livera os campos de endereco do cedente*/
                setCamposReadyOnlyCedente(false);
            }

		} else{
		    RequestContext.getCurrentInstance().execute("limpaCamposCedente();");
		    limpar();
			FacesUtil.addErrorMessage("Arma não é de militar. Por favor vá a pesquisa de armas e verifique.");

			}
		}catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao atribuir dados da ficha no Cedente: " + e.getMessage());
			RequestContext.getCurrentInstance().execute("limpaCamposCedente();"); 
		}
	}

    /**Metodo localizarProprietarioExistente tem o objetivo de buscar situação da arma no momento de salvar
     * Não permite que seja duplicado proprietário na arma.
     * Também não deixa o novo proprietário ser o mesmo de cedente*/
    public void localizarProprietarioExistente(){

        try{
            /**busca pela arma que já é alvo da transferência*/
            Arma armaBusca = transferenciaArmaService.buscaArma(arma.getCodigo());

            if (recebedor.getNovoProprietarioCpf().equals(armaBusca.getCpfNovoProprietario())) {
                FacesUtil.addErrorMessage("Arma já pertence ao proprietário de cpf " + recebedor.getNovoProprietarioCpf() + " !");
                RequestContext.getCurrentInstance().execute("limpaCpfProp();");
            }else if(recebedor.getNovoProprietarioCpf().equals(cedente.getCedCpf())){
                FacesUtil.addErrorMessage("Ops! Cedente e Novo Proprietário são os mesmos! ");
                RequestContext.getCurrentInstance().execute("limpaCpfProp();");

            }else  {
                atribuiDadosNovoProprietario();
            }

        }catch(Exception e){
            FacesUtil.addErrorMessage("Erro durante a transfência. Contato o administrador! - " + e.getMessage());
        }

    }
	
	public void atribuiDadosNovoProprietario() {
		try {
		    /**busca endereço atual do novo proprietário quando o usuário digitar o cpf*/
			ds = consumirServicoSicad.busca(recebedor.getNovoProprietarioCpf());

			if (ds != null) {

				recebedor.setNovoPropNome(ds.getNome());
                recebedor.setNovoPropCategoriaFuncional(ds.getPostoSiglaGrad());
                recebedor.setNovoPropRg(ds.getRgMilitar());
                recebedor.setNovoPropSituacao(ds.getStatus());
                //endereço
                recebedor.setNovoPropLogradouro(ds.getResLogradouro());
                recebedor.setNovoPropBairro(ds.getResBairro());
                recebedor.setNovoPropCidade(ds.getResCidade());
                recebedor.setNovoPropUf(ds.getResUf());
                recebedor.setNovoPropEmail(ds.getEmail());

			} else {
				FacesUtil.addErrorMessage("Não foi possível encontrar o CPF no cadastro de Policiais Militares!");
				recebedor = new ArmaTransferencia();

			}
		}catch (Exception e){
			FacesUtil.addErrorMessage("Erro ao buscar e atribuir novo proprietário: " + e.getMessage());
		}
	}

	public void ativaTransferenciaCivil(){
    	 setTranferenciaCivil(true);
	}

	
	public void liberaCamposCedente(){
	    /**zera os dados de Cedente, exeto cpf, nome e sistuação*/
		RequestContext.getCurrentInstance().execute("limpaCamposCedente();");
		setCamposReadyOnlyCedente(false);
		labelAlterar = "Campos liberados";
	}

	public void limpar() {
		arma = new Arma();
		armaTransferencia = new ArmaTransferencia();
		cedente = new ArmaTransferencia();
        recebedor = new ArmaTransferencia();
		ds = new DadosSicad();
		setCamposReadyOnlyCedente(true);
	}

	//getters and setters
	public Arma getArma() {
		if (arma == null){
			arma = new Arma();
		}
		return arma;
	}

	public void setArma(Arma arma) {
		this.arma = arma;
	}

	public ArmaTransferencia getArmaTransfencia() {
		if(armaTransferencia == null){
			armaTransferencia = new ArmaTransferencia();
		}
		return armaTransferencia;
	}

	public void setArmaTransfencia(ArmaTransferencia armaTransfencia) {
		this.armaTransferencia = armaTransfencia;
	}

	public Arma getProprietarioExistente() {
		return proprietarioExistente;
	}

	public void setProprietarioExistente(Arma proprietarioExistente) {
		this.proprietarioExistente = proprietarioExistente;
	}
	
	public Boolean getCamposReadyOnlyCedente() {
		return camposReadyOnlyCedente;
	}

	public void setCamposReadyOnlyCedente(Boolean camposReadyOnlyCedente) {
		this.camposReadyOnlyCedente = camposReadyOnlyCedente;
	}

	public String getLabelAlterar() {
		return labelAlterar;
	}

	public void setLabelAlterar(String labelAlterar) {
		this.labelAlterar = labelAlterar;
	}

	public DadosSicad getDs() {
		return ds;
	}

	public void setDs(DadosSicad ds) {
		this.ds = ds;
	}

    public void setTransferenciaArmaService(TransferenciaArmaService transferenciaArmaService) {
		this.transferenciaArmaService = transferenciaArmaService;
	}

	public void setConsumirServicoSicad(ConsumirServicoSicad consumirServicoSicad) {
		this.consumirServicoSicad = consumirServicoSicad;
	}

	public Boolean getTranferenciaCivil() {
		return tranferenciaCivil;
	}

	public void setTranferenciaCivil(Boolean tranferenciaCivil) {
		this.tranferenciaCivil = tranferenciaCivil;
	}

	public ArmaTransferencia getCedente() {
        return cedente;
    }

    public void setCedente(ArmaTransferencia cedente) {
        this.cedente = cedente;
    }

    public ArmaTransferencia getRecebedor() {
        return recebedor;
    }

    public void setRecebedor(ArmaTransferencia recebedor) {
        this.recebedor = recebedor;
    }

	public String getLabeltituloPagina() {
		return labeltituloPagina;
	}

	public void setLabeltituloPagina(String labeltituloPagina) {
		this.labeltituloPagina = labeltituloPagina;
	}
}
