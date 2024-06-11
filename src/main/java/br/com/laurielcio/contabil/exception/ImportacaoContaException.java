package br.com.laurielcio.contabil.exception;

public class ImportacaoContaException extends RuntimeException {

        private static final long serialVersionUID = 1327036249750493143L;

	public ImportacaoContaException(String msg) {
            super(msg);
        }

	public ImportacaoContaException(String msg, Throwable cause) {
            super(msg, cause);
        }
}
