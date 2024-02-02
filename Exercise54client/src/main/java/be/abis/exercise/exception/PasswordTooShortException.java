package be.abis.exercise.exception;

public class PasswordTooShortException extends Exception {


	public PasswordTooShortException(String message) {
		super(message);
	}

	public PasswordTooShortException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public PasswordTooShortException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public PasswordTooShortException(String message, Throwable cause, boolean enableSuppression,
                                     boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
