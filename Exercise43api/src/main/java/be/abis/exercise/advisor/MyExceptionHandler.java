package be.abis.exercise.advisor;

import be.abis.exercise.exception.ApiError;
import be.abis.exercise.exception.PersonAlreadyExistsException;
import be.abis.exercise.exception.PersonCanNotBeDeletedException;
import be.abis.exercise.exception.ValidationError;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<?> constraintViolationException(ConstraintViolationException ex, WebRequest request) {

		List<String> errors = new ArrayList<>();

		ex.getConstraintViolations().forEach(cv -> errors.add(cv.getMessage()));

		Map<String, List<String>> result = new HashMap<>();

		result.put("errors", errors);

		return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
																  HttpHeaders headers, HttpStatusCode status, WebRequest request) {

		ApiError err = new ApiError("invalid arguments", status.value(), ex.getMessage());

		List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
		List<ValidationError> validationErrorList = err.getInvalidParams();
		for (FieldError fe : fieldErrors) {

			//System.out.println(fe.getField() + " " + fe.getDefaultMessage());
			ValidationError validationError = new ValidationError();
			validationError.setName(fe.getField());
			validationError.setReason(fe.getDefaultMessage());
			validationErrorList.add(validationError);
		}

		return new ResponseEntity<Object>(err, headers, status);
	}

	@ExceptionHandler({ MethodArgumentTypeMismatchException.class })
	public ResponseEntity<Object> handleMethodArgumentTypeMismatch(
			MethodArgumentTypeMismatchException ex, WebRequest request) {
		String error =
				ex.getName() + " should be of type " + ex.getRequiredType().getName();

		ApiError apiError =
				new ApiError("wrong type entered",HttpStatus.BAD_REQUEST.value(),error);
		return new ResponseEntity<Object>(
				apiError, new HttpHeaders(), apiError.getStatus());
	}

}
