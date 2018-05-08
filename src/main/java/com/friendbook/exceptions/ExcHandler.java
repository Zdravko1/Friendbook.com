package com.friendbook.exceptions;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
	public class ExcHandler extends ResponseEntityExceptionHandler {
	
		public static String ERROR_MSG; 
	
		@ExceptionHandler(WrongCredentialsException.class)
	    protected String errorHandler(WrongCredentialsException ex, Model model) {
			ERROR_MSG = ex.getMessage();
			model.addAttribute("error", ERROR_MSG);
	        return "error";
	    }
		
		@ExceptionHandler(IllegalArgumentException.class)
	    protected String errorHandler(IllegalArgumentException ex, Model model) {
			ERROR_MSG = ex.getMessage();
			model.addAttribute("error", ERROR_MSG);
	        return "error";
	    }
	 
	    @ExceptionHandler(value = { Exception.class})
	    protected String errorHandler(Exception ex, Model model) {
	    	ERROR_MSG = "The page you are looking for has been removed,had its name changed or temporarily unavailable";
	    	model.addAttribute("error", ERROR_MSG);
	        return "error";
	    }
	}