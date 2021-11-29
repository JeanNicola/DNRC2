package gov.mt.wris.utils;

import gov.mt.wris.dtos.Message;
import gov.mt.wris.exceptions.DataConflictException;
import gov.mt.wris.exceptions.DataUsedElsewhereException;
import gov.mt.wris.exceptions.HelpDeskNeededException;
import gov.mt.wris.exceptions.NotFoundException;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/*
 * GlobalExceptionHandler
 *
 * Common class for handling all thrown errors.
 * 
 * To handle an error:
 *  1. Create a local function that returns a ResponseEntity<Message> object.
 *  2. Decorate that function wiht @ExceptionHandler and the class or classes that function will handle
 *  3. In the function build the standard message object by calling buildApiMessage and passing the appropriate parameters:
 *      - the exception
 *      - a user message if the one already provided by the exceptiojn is not sufficient (optional)
 *      - a developer messsage  if the one already provided by the exceptiojn is not sufficient (optional)
 */
@ControllerAdvice
public class GlobalExceptionHandler {

	Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Message> handleSpringValidation(MethodArgumentNotValidException ex, WebRequest request) {
		String fields = "";
		List<String> fieldList = new ArrayList<String>();

		// Loop through the fields and collect the error messages to return to the
		// called
		for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
			String field = fieldError.getDefaultMessage();
			fieldList.add(field);
			LOGGER.info("API Argument Error: " + field);
		}

		fields = String.join("; ", fieldList);
		return new ResponseEntity<>(
				buildApiMessage(ex, "Missing or incorrect values were provided: "+ fields, null),
				new HttpHeaders(), HttpStatus.BAD_REQUEST);
	}

	// This handler is for developer-type errors that users may encounter that need a friendlier message
	@ExceptionHandler({ InvalidDataAccessApiUsageException.class, MethodArgumentTypeMismatchException.class,
			HttpMessageNotReadableException.class })
	public ResponseEntity<Message> handleBadRequest(Exception ex, WebRequest request) {
		return new ResponseEntity<>(buildApiMessage(ex, "Invalid data request", ex.getMessage() + "; " + ExceptionUtils.getRootCauseMessage(ex)), new HttpHeaders(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({ ValidationException.class, ConstraintViolationException.class })
	public ResponseEntity<Message> handleValidationException(Exception ex, WebRequest request) {
		return new ResponseEntity<>(buildApiMessage(ex), new HttpHeaders(), HttpStatus.BAD_REQUEST);
	}

@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<Message> handleMethodNotSupported(Exception ex, WebRequest request) {
		return new ResponseEntity<>(buildApiMessage(ex), new HttpHeaders(), HttpStatus.METHOD_NOT_ALLOWED);
	}

	@ExceptionHandler({ NotFoundException.class, EmptyResultDataAccessException.class, NoSuchElementException.class,
			NoHandlerFoundException.class })
	public ResponseEntity<Message> handleEmptyResult(Exception ex, WebRequest request) {
		return new ResponseEntity<>(buildApiMessage(ex), new HttpHeaders(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler({ DataConflictException.class, DataUsedElsewhereException.class, DataIntegrityViolationException.class})
	public ResponseEntity<Message> handleDataIntegrityViolation(Exception ex, WebRequest request) {
		return new ResponseEntity<>(buildApiMessage(ex), new HttpHeaders(), HttpStatus.CONFLICT);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<Message> handleAccessDenied(final Exception ex, final WebRequest request) {
		return new ResponseEntity<>(buildApiMessage(ex), new HttpHeaders(), HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler({AuthenticationException.class, HelpDeskNeededException.class})
	public ResponseEntity<Message> handleBadCredentials(final Exception ex, final WebRequest request) {
		return new ResponseEntity<>(buildApiMessage(ex), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Message> handleGeneric(Exception ex, WebRequest request) {
		LOGGER.error("Unexpected System Error occurred");
		return new ResponseEntity<>(buildApiMessage(ex, "An unexpected system error occurred"), new HttpHeaders(),	HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(InvalidDataAccessResourceUsageException.class)
	public ResponseEntity<Message> handleBadPermissions(InvalidDataAccessResourceUsageException ex, WebRequest request) {
		return new ResponseEntity<>(buildApiMessage(ex, "Not enough permissions"), new HttpHeaders(), HttpStatus.BAD_REQUEST);
	}

	// build Api Error Message if only exception is passed in
	private Message buildApiMessage(Exception ex) {
		return this.buildApiMessage(ex, null, null);
	}

	// build Api Error Message if only exception and a user message is passed in
	private Message buildApiMessage(Exception ex, String userMsg) {
		return this.buildApiMessage(ex, userMsg, null);
	}

	// build Api Error Message - call the static method
	private Message buildApiMessage(Exception ex, String userMsg, String developerMsg) {
		return GlobalExceptionHandler.staticBuildApiMessage(ex, userMsg, developerMsg);
	}

	// build Api Error Message - this is a public static method that can be called
	// from
	public static Message staticBuildApiMessage(Exception ex, String userMsg, String developerMsg) {
		Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

		Message message = new Message();

		LOGGER.error(ExceptionUtils.getRootCauseMessage(ex));
		LOGGER.debug(ExceptionUtils.getStackTrace(ex));

		// If userMsg is null, use the message in the exception
		message.setUserMessage(userMsg != null ? userMsg : ex.getMessage());

		// If developerMsg is null, use the root cause message in the exception
		message.setDeveloperMessage(developerMsg != null ? developerMsg : ExceptionUtils.getRootCauseMessage(ex));

		// Only return the full stack track if TRACE is enabled in the
		// application.properties file
		if (LOGGER.isTraceEnabled()) {
			message.setStackTrace(String.join(":", ExceptionUtils.getRootCauseStackTrace(ex)));
		} else {
			message.setStackTrace("Enable tracing in the logs to see the stack trace in the HTTP response");
		}

		// Include the class of the Exception
		message.setExceptionName(ClassUtils.getShortClassName(ex, null));
		return message;
	}
}