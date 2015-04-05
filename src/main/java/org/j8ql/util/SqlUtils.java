package org.j8ql.util;

public class SqlUtils {

	/**
	 * <p>If the column as a relation prefix (e.g., contact.id) it will return "contact"."id" otherwise, just return the "id"</p>
	 * <p>Note: when the column name is a function like "to_tsvector(subject)" nothing is escaped, so make sure to escape in case of upper case column or table name.</p>
	 *
	 * @param columnName could be the column name "projectId" or with table context "ticket.projectId" or aliased "projectId pid" or function
	 * @return
	 */
	public static String escapeColumnName(String columnName){
		StringBuilder sb = new StringBuilder();

		String alias = null;

		// if we have a ")" then it is a function columnName, so, we do not escape it yet.
		int lastBracketIdx = columnName.lastIndexOf(')');
		if (lastBracketIdx != -1){
			int endOfFunIdx = lastBracketIdx + 1;
			String firstPart = columnName.substring(0,endOfFunIdx);
			// for now, we do not escape it.
			sb.append(firstPart);

			if (endOfFunIdx< columnName.length()){
				// assume that if there something after, it is the alias
				// TODO: probably need to check that it is a space
				alias = columnName.substring(endOfFunIdx);
			}
		} else{
			int spaceIdx = columnName.indexOf(" ");
			if(spaceIdx > -1) {
				if (spaceIdx < columnName.length()){
					alias = columnName.substring(spaceIdx + 1);
				}
				columnName = columnName.substring(0, spaceIdx);
			}
			// process the column name, which could have tableName.columnName notation or just columnName
			int idx = columnName.indexOf(".");
			if (idx > -1 ){
				// first is usually the table/rel name
				String relName = columnName.substring(0,idx);
				// second is usually the column name
				String colName = columnName.substring(idx + 1);
				// always quote the relName
				sb.append('"').append(relName).append("\".");

				// for the column name, does not quote it if it is "*"
				if ("*".equals(colName)) {
					sb.append(colName);
				}else{
					sb.append('"').append(colName).append('"');
				}
			}else{
				sb.append('"').append(columnName).append('"');
			}
		}

		if (alias != null) {
			// TODO: probably should not escape alias of they contain already " or characters like ( ) @
			sb.append(" \"").append(alias).append('"');
		}

		return sb.toString();
	}
}
