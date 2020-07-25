package br.gov.go.pm.enuns;

public enum UnidadeDeMedida {
   MM("MM"), CM("CM"), MT("MT");

   private String descricao;

   UnidadeDeMedida(String descricao) {
      this.descricao = descricao;
   }

   public String getDescricao() {
      return descricao;
   }

   }
