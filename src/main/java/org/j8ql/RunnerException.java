/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.j8ql;

/**
 * <p></p>
 */
public class RunnerException  extends BaseException{
	public enum RunnerError implements BaseException.Error {
		DML_BUILDER_HAS_NO_RETURNING_CANNOT_BE_STREAMED("This UpdateQuery does not have any returning and therefore cannot be streamed or listed"),
		INCOMPLETE_INSERT_BUILDER_NO_COLUMNS_OR_VALUE_OBJECT("This inserBuilder does not contain columns or a values object, not sure what to insert."),
		CANNOT_BATCH_DOES_NOT_HAVE_BATCH_VALUE("This builder cannot be batchExecuted because it does not contain a batchValue or batchValues");

		String msg;
		RunnerError(String msg) {
			this.msg = msg;
		}
		public String msg() {
			return msg;
		}
	}

	public RunnerException(RunnerError runnerError, Object... values){
		super(runnerError,values);
	}

}
