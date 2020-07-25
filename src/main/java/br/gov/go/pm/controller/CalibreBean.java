
package br.gov.go.pm.controller;

import br.gov.go.pm.modelo.Calibre;
import br.gov.go.pm.modelo.GrupoCalibre;
import br.gov.go.pm.service.CalibreService;
import br.gov.go.pm.service.general.AtualizaPaginaService;
import br.gov.go.pm.util.exception.NegocioException;
import br.gov.go.pm.util.jsf.FacesUtil;
import br.gov.go.pm.utilitarios.MostraDataAtual;
import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionSystemException;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
public class CalibreBean implements Serializable {
	private static final long serialVersionUID = 1L;

    private final Logger log = Logger.getLogger(this.getClass());
	
	@ManagedProperty(value = "#{calibreService}")
	private CalibreService calibreService;

	@ManagedProperty(value = "#{atualizaPaginaService}")
	private AtualizaPaginaService atualizaPaginaService;

	private List<Calibre> calibres;
	private List<GrupoCalibre> grupoCalibres;
	private Calibre calibre;
	private Calibre calibreSelecionado;
	private String busca;

	@PostConstruct
	public void init(){
		limpar();
		buscarTodos();
		preencherListas();
	}

	public void buscarTodos(){
        calibres = calibreService.list();
    }

	public void preencherListas(){
        grupoCalibres = calibreService.buscaGruposCalibre();
	}

	public void buscar(){

		if (busca.equals("")) {
			buscarTodos();
		}else {
			calibres = calibreService.buscaCalibresPorParametro(busca);
		}
	}

	public void salvar(){
		try {
			calibreService.salvar(calibre);

			FacesUtil.addSuccessMessage("Calibre salvo com sucesso!");
			limpar();
			atualizaPaginaService.AtualizaPagina();

		} catch (Exception e) {
			FacesUtil.addErrorMessage("Ops! " + e.getMessage());
		}
	}

	public void edicao(){
		setCalibre(calibreSelecionado);
	}

	public void excluir() throws NegocioException {
		if(calibreSelecionado != null){

            try {

                calibres.remove(calibreSelecionado);
                FacesUtil.addSuccessMessage("Calibre " + calibreSelecionado.getCalibre() + " excluído com sucesso");

                atualizaPaginaService.AtualizaPagina();
                calibreService.excluir(calibreSelecionado);

            }catch (TransactionSystemException te){
                FacesUtil.addErrorMessage("Não foi possível a exclusão, Calibre pode estar sendo utilizado. Verifique, se persistir contate o administrador! ");
                log.error("Log personalizado: " + te.getMessage(), te.getCause());
            } catch (Exception e) {
                FacesUtil.addErrorMessage(e.getMessage());
                log.error("Log personalizado: " + e.getMessage(), e.getCause());
            }

        }
	}

	public void limpar(){
		calibre = new Calibre();
		calibreSelecionado = new Calibre();
		calibres =  new ArrayList<>();
		grupoCalibres =  new ArrayList<>();

	}

//	public void pesquisaEspecifica(){
//		if (Calibre.getNome() == null || Calibre.getNome().trim().equals("")){
//			Calibres = calibreService.list();
//		}else{
//			Calibres = calibreService.buscaCalibresPorParametro(Calibre.getNome());
//
//		}
//	}

	/**utilizado para mostrar a data atual em um campo data somente leitura*/
	public Date getDataDeHoje(){
		return MostraDataAtual.getDataDeHoje();
	}

	//Getters and Setters

	public List<Calibre> getCalibres() {
		return calibres;
	}

	public void setCalibres(List<Calibre> calibres) {
		this.calibres = calibres;
	}

	public List<GrupoCalibre> getGrupoCalibres() {
		return grupoCalibres;
	}

	public void setGrupoCalibres(List<GrupoCalibre> grupoCalibres) {
		this.grupoCalibres = grupoCalibres;
	}

	public Calibre getCalibre() {
		return calibre;
	}

	public void setCalibre(Calibre calibre) {
		this.calibre = calibre;
	}

	public Calibre getCalibreSelecionado() {
		return calibreSelecionado;
	}

	public void setCalibreSelecionado(Calibre calibreSelecionado) {
		this.calibreSelecionado = calibreSelecionado;
	}

	public String getBusca() {
		return busca;
	}

	public void setBusca(String busca) {
		this.busca = busca;
	}

	public void setCalibreService(CalibreService calibreService) {
		this.calibreService = calibreService;
	}

	public void setAtualizaPaginaService(AtualizaPaginaService atualizaPaginaService) {
		this.atualizaPaginaService = atualizaPaginaService;
	}
}