/**
 * <p>title: McException.java</p>
 * <p>description : </p>
 * @author hy
 * @date 2020年10月21日
 *
 */
package com.chinaalert.event.common.exception;


/**
 * @author q
 *
 */
public class McException extends RuntimeException{

	/**
	 * 
	 */
	public McException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public McException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public McException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public McException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public McException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}
}
