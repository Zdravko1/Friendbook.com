package com.friendbook.exceptions;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
	public class ExcHandler extends ResponseEntityExceptionHandler {
	 
	    @ExceptionHandler(value = { Exception.class})
	    protected String errorHandler(Exception ex) {
	        return "error";
	    }
	}