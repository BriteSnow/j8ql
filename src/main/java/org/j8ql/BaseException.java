/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.j8ql;

import java.util.IllegalFormatException;

/**
 * <p></p>
 */
public class BaseException extends RuntimeException {

	private Error error;
	private Object[] values;

	public BaseException(Error error, Object[] values) {
		this(null, error, values);
	}

	public BaseException(Throwable cause, Error error, Object[] values) {
		super(cause);
		this.error = error;
		this.values = values;
	}

	public Error getError(){
		return error;
	}

	@Override
	public String getMessage() {
		return error.toString() + ": " + error.formatMessage(values);
	}

	public interface Error {

		public String msg();

		default public String formatMessage(Object... values){
			try {
				if (values == null){
					return msg();
				}else {
					return String.format(msg(), values);
				}
			}catch(IllegalFormatException e){
				// if wrong format, just show the message without format.
				return msg();
			}
		}
	}
}
