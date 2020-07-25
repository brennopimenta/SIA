package br.gov.go.pm.enuns;

public enum Acabamento {

		/**para o exército é enviado a descriação*/
		OXIDADO("OXIDADO"), INOXFOSCO("INOX FOSCO"), INOXAUTOBRILHO("INOX AUTO BRILHO"),
		CAMUFLADO("CAMUFLADO"); 

			String acabamento;

			Acabamento(String acabamento) {
				this.acabamento = acabamento;
			}

			public String getAcabamento() {
				return acabamento;
			}
			
			
			
}
