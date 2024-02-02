package be.abis.exercise.advisor;

import be.abis.exercise.exception.ApiError;
import be.abis.exercise.exception.PersonAlreadyExistsException;
import be.abis.exercise.exception.PersonCanNotBeDeletedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

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

}
