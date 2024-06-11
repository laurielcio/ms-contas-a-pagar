package br.com.laurielcio.contabil.exception;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StandardError implements Serializable{
	
	private static final long serialVersionUID = 2516188910118354151L;

	private Integer status;

	private String msg;

	private Long timeStamp;

}

