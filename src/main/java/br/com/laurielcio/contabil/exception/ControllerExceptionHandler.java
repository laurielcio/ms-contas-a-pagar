package br.com.laurielcio.contabil.exception;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<StandardError> validation(MethodArgumentNotValidException e, HttpServletRequest request){
		ValidationError standardError = new ValidationError(HttpStatus.BAD_REQUEST.value(), "Erro de validação!", System.currentTimeMillis());
		
		for(FieldError fieldError : e.getBindingResult().getFieldErrors()) {
			standardError.addError(fieldError.getField(), fieldError.getDefaultMessage());
		}
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(standardError);
	}

	@ExceptionHandler(ContaNotFoundException.class)
	public ResponseEntity<StandardError> contaNotFoundException(ContaNotFoundException e, HttpServletRequest request){
		StandardError standardError = new StandardError(HttpStatus.NOT_FOUND.value(), e.getMessage(), System.currentTimeMillis());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(standardError);
	}

	@ExceptionHandler(ImportacaoContaException.class)
	public ResponseEntity<StandardError> importacaoContaException(ImportacaoContaException e, HttpServletRequest request){
		StandardError standardError = new StandardError(HttpStatus.BAD_REQUEST.value(), e.getMessage(), System.currentTimeMillis());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(standardError);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<StandardError> illegalArgumentException(IllegalArgumentException e, HttpServletRequest request){
		StandardError standardError = new StandardError(HttpStatus.BAD_REQUEST.value(), e.getMessage(), System.currentTimeMillis());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(standardError);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<StandardError> exception(Exception e, HttpServletRequest request){
		StandardError standardError = new StandardError(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), System.currentTimeMillis());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(standardError);
	}
	
	@ExceptionHandler(ValidationException.class)
	public ResponseEntity<StandardError> validationException(ValidationException e, HttpServletRequest request){
		StandardError standardError = new StandardError(HttpStatus.BAD_REQUEST.value(), e.getMessage(), System.currentTimeMillis());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(standardError);
	}

}

