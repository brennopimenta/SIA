package br.gov.go.pm.modelo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

import br.gov.go.pm.enuns.*;

@Embeddable
public class ArmaEnuns implements Serializable {

	private static final long serialVersionUID = 1L;

	private UsoArma uso;

	@NotNull(message="Alma da arma é obrigatória")
	@Enumerated(EnumType.STRING)
	@Column(name="alma")
	private TipoAlma tipoAlma;
	private Acabamento acabamento;
	private Funcionamento funcionamento;
	private EspecieArma especie;

	@Column(name="sentido_da_raia")
	private SentidoRaia sentidoRaia;
	@Column(name="unidade_medida_do_cano")
	private UnidadeDeMedida unMedComprimentoDoCano = UnidadeDeMedida.MM;
	@Column(name="status_restritivo")
	private Status statusRestritivo;
	@Column(name="status_carga")
	private StatusCarga statusCarga;

	public ArmaEnuns(UsoArma uso, TipoAlma tipoAlma, Acabamento acabamento, Funcionamento funcionamento,
                     EspecieArma especie, SentidoRaia sentidoRaia, UnidadeDeMedida unMedComprimentoDoCano,
                     Status statusRestritivo, StatusCarga statusCarga) {
		super();
		this.uso = uso;
		this.tipoAlma = tipoAlma;
		this.acabamento = acabamento;
		this.funcionamento = funcionamento;
		this.especie = especie;
		this.sentidoRaia = sentidoRaia;
		this.unMedComprimentoDoCano = unMedComprimentoDoCano;
		this.statusRestritivo = statusRestritivo;
		this.statusCarga = statusCarga;
	}

	public ArmaEnuns() {

	}

	//Getters and Setters


	public TipoAlma getTipoAlma() {
		return tipoAlma;
	}
	public void setTipoAlma(TipoAlma tipoAlma) {
		this.tipoAlma = tipoAlma;
	}

	@Enumerated(EnumType.STRING)
	public Acabamento getAcabamento() {
		return acabamento;
	}
	public void setAcabamento(Acabamento acabamento) {
		this.acabamento = acabamento;
	}


	@Enumerated(EnumType.STRING)
	public Funcionamento getFuncionamento() {
		return funcionamento;
	}
	public void setFuncionamento(Funcionamento funcionamento) {
		this.funcionamento = funcionamento;
	}

	@NotNull(message="Espécie da arma é obrigatória")
	@Enumerated(EnumType.STRING)
	public EspecieArma getEspecie() {
		return especie;
	}
	public void setEspecie(EspecieArma especie) {
		this.especie = especie;
	}

	@Enumerated(EnumType.STRING)
	public SentidoRaia getSentidoRaia() {
		return sentidoRaia;
	}
	public void setSentidoRaia(SentidoRaia sentidoRaia) {
		this.sentidoRaia = sentidoRaia;
	}

	@Enumerated(EnumType.STRING)
	public UnidadeDeMedida getUnMedComprimentoDoCano() {
		return unMedComprimentoDoCano;
	}
	public void setUnMedComprimentoDoCano(UnidadeDeMedida unMedComprimentoDoCano) {
		this.unMedComprimentoDoCano = unMedComprimentoDoCano;
	}

	@Enumerated(EnumType.STRING)
	public UsoArma getUso() {
		return uso;
	}

	public void setUso(UsoArma uso) {
		this.uso = uso;
	}

	@Enumerated(EnumType.STRING)
	public Status getStatusRestritivo() {
		return statusRestritivo;
	}
	public void setStatusRestritivo(Status statusRestritivo) {
		this.statusRestritivo = statusRestritivo;
	}

	@Enumerated(EnumType.STRING)
	public StatusCarga getStatusCarga() {
		return statusCarga;
	}

	public void setStatusCarga(StatusCarga statusCarga) {
		this.statusCarga = statusCarga;
	}
}