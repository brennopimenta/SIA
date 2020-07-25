package br.gov.go.pm.util.exception;

public class NaoEncontradoException extends RuntimeException{

	public NaoEncontradoException(final String mensagem, final Throwable causa) {
		super(mensagem, causa);
	}

	public NaoEncontradoException(String mensagem) {
		super(mensagem);
	}

	public NaoEncontradoException(Throwable causa) {
		super(causa);
	}
	
}