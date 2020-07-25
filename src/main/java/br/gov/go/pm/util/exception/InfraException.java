
package br.gov.go.pm.util.exception;
public class InfraException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InfraException(final String mensagem) {
		super(mensagem);
	}

	public InfraException(final String mensagem, final Throwable origem) {
		super(mensagem, origem);
	}

	public InfraException(final Throwable origem) {
		super(origem);
	}
	
}