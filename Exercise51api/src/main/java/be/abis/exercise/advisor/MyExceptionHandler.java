package be.abis.exercise.advisor;

import be.abis.exercise.exception.*;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@RestControllerAdvice
public class MyExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(value = PersonAlreadyExistsException.class)
	protected ResponseEntity<? extends Object> handlePersonAlreadyExists(PersonAlreadyExistsException pnfe,
			WebRequest request) {
		HttpStatus status = HttpStatus.CONFLICT;
		ApiError err = new ApiError("person exception", status.value(), pnfe.getMessage());
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("content-type", MediaType.APPLICATION_PROBLEM_JSON_VALUE);
		return new ResponseEntity<ApiError>(err, responseHeaders, status);
	}

	@ExceptionHandler(value = PersonCanNotBeDeletedException.class)
	protected ResponseEntity<? extends Object> handlePersonCannotBeDeleted(PersonCanNotBeDeletedException pcde,
			WebRequest request) {
		HttpStatus status = HttpStatus.CONFLICT;
		ApiError err = new ApiError("person exception", status.value(), pcde.getMessage());
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("content-type", MediaType.APPLICATION_PROBLEM_JSON_VALUE);
		return new ResponseEntity<ApiError>(err, responseHeaders, status);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
																  HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		ApiError err = new ApiError("invalid arguments", status.value(), ex.getMessage());

		List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
		List<ValidationError> validationErrorList = err.getInvalidParams();
		for (FieldError fe : fieldErrors) {

			System.out.println(fe.getField() + " " + fe.getDefaultMessage());
			ValidationError validationError = new ValidationError();
			validationError.setName(fe.getField());
			validationError.setReason(fe.getDefaultMessage());
			validationErrorList.add(validationError);
		}

		return new ResponseEntity<Object>(err, headers, status);
	}

	@ExceptionHandler(value = ApiKeyNotCorrectException.class)
	protected ResponseEntity<Object> handleGuestNotFound(ApiKeyNotCorrectException ance, WebRequest request) {
		//System.out.println("apikey not correct found...");
		HttpStatus status = HttpStatus.UNAUTHORIZED;
		ApiError err = new ApiError("unauthorized", status.value(), ance.getMessage());
		System.out.println("err: " + err.getDescription());
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("content-type", MediaType.APPLICATION_PROBLEM_JSON_VALUE);
		return new ResponseEntity<Object>(err, responseHeaders, status);
	}

}
