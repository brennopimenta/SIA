
package br.gov.go.pm.controller;

import br.gov.go.pm.enuns.UsoArma;
import br.gov.go.pm.modelo.GrupoCalibre;
import br.gov.go.pm.service.GrupoCalibreService;
import br.gov.go.pm.service.general.AtualizaPaginaService;
import br.gov.go.pm.util.exception.NegocioException;
import br.gov.go.pm.util.jsf.FacesUtil;
import br.gov.go.pm.utilitarios.MostraDataAtual;
import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.transaction.TransactionSystemException;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
public class GrupoCalibreBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private final Logger log = Logger.getLogger(this.getClass());
	
	@ManagedProperty(value = "#{grupoCalibreService}")
	private GrupoCalibreService grupoCalibreService;

	@ManagedProperty(value = "#{atualizaPaginaService}")
	private AtualizaPaginaService atualizaPaginaService;

	private List<GrupoCalibre> grupoCalibres = new ArrayList<>();
	private List<UsoArma> grausRestricao;
	private GrupoCalibre grupoCalibre;
	private GrupoCalibre grupoCalibreSelecionado;
	private String busca;

	@PostConstruct
	public void init(){
		limpar();
		buscarTodos();
		preencherListas();
	}

	public void buscarTodos(){
        grupoCalibres = grupoCalibreService.list();
    }

	public void preencherListas(){
        grausRestricao = Arrays.asList(UsoArma.values());
	}

	public void buscar(){

		if (busca.equals("")) {
			buscarTodos();
		}else {
			grupoCalibres = grupoCalibreService.buscaGrupoCalibresPorParametro(busca);
		}

	}

	public void salvar(){
		try {
			grupoCalibreService.salvar(grupoCalibre);

			FacesUtil.addSuccessMessage("Grupo Calibre salvo com sucesso!");
			limpar();
			atualizaPaginaService.AtualizaPagina();
;
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Ops! " + e.getMessage());
		}
	}

	public void edicao(){
		setGrupoCalibre(grupoCalibreSelecionado);
	}

	public void excluir() {

			if (grupoCalibreSelecionado != null) {

				//grupoCalibreService.verificaGrupoCalibreUtilizadoPorUser(GrupoCalibreSelecionado.getCodigo());

				try {
					grupoCalibreService.excluir(grupoCalibreSelecionado);
					grupoCalibres.remove(grupoCalibreSelecionado);

					FacesUtil.addSuccessMessage("GrupoCalibre " + grupoCalibreSelecionado.getNome() + " excluído com sucesso");
					atualizaPaginaService.AtualizaPagina();
				}catch (TransactionSystemException te) {
					FacesUtil.addErrorMessage("Não foi possível a exclusão, grupo pode estar sendo utilizado. Verifique, se persistir contate o administrador!");
					log.error("Log personalizado: " + te.getMessage(), te.getCause());
				} catch (Exception e) {
					FacesUtil.addErrorMessage(e.getMessage());
					log.error("Log personalizado: " + e.getMessage(), e.getCause());
				}
			}

	}

	public void limpar(){
		grupoCalibre = new GrupoCalibre();
		grupoCalibreSelecionado = new GrupoCalibre();

	}

//	public void pesquisaEspecifica(){
//		if (GrupoCalibre.getNome() == null || GrupoCalibre.getNome().trim().equals("")){
//			GrupoCalibres = grupoCalibreService.list();
//		}else{
//			GrupoCalibres = grupoCalibreService.buscaGrupoCalibresPorParametro(GrupoCalibre.getNome());
//
//		}
//	}

	/**utilizado para mostrar a data atual em um campo data somente leitura*/
	public Date getDataDeHoje(){
		return MostraDataAtual.getDataDeHoje();
	}

	//Getters and Setters


	public List<GrupoCalibre> getGrupoCalibres() {
		return grupoCalibres;
	}

	public void setGrupoCalibres(List<GrupoCalibre> grupoCalibres) {
		this.grupoCalibres = grupoCalibres;
	}

	public GrupoCalibre getGrupoCalibre() {
		return grupoCalibre;
	}

	public void setGrupoCalibre(GrupoCalibre grupoCalibre) {
		this.grupoCalibre = grupoCalibre;
	}

	public GrupoCalibre getGrupoCalibreSelecionado() {
		return grupoCalibreSelecionado;
	}

	public void setGrupoCalibreSelecionado(GrupoCalibre grupoCalibreSelecionado) {
		this.grupoCalibreSelecionado = grupoCalibreSelecionado;
	}

	public void setAtualizaPaginaService(AtualizaPaginaService atualizaPaginaService) {
		this.atualizaPaginaService = atualizaPaginaService;
	}

	public List<UsoArma> getGrausRestricao() {
		return grausRestricao;
	}

	public void setGrausRestricao(List<UsoArma> grausRestricao) {
		this.grausRestricao = grausRestricao;
	}

	public String getBusca() {
		return busca;
	}

	public void setBusca(String busca) {
		this.busca = busca;
	}

	public void setGrupoCalibreService(GrupoCalibreService grupoCalibreService) {
		this.grupoCalibreService = grupoCalibreService;
	}

}