package br.com.laurielcio.contabil.exception;

public class ContaNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1327036249750493143L;

	public ContaNotFoundException(String msg) {
		super(msg);
	}

	public ContaNotFoundException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
