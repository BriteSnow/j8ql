package org.j8ql.util;

public class SqlUtils {

	/**
	 * If the column as a relation prefix (e.g., contact.id) it will return "contact"."id" otherwise, just return the "id"
	 *
	 * TODO: need to support the alias with space. For example "contact.id as cid"
	 * @param columnName
	 * @return
	 */
	public static String escapeColumnName(String columnName){
		StringBuilder sb = new StringBuilder();


		// extract the alias if present
		String alias = null;
		int spaceIdx = columnName.indexOf(" ");
		if(spaceIdx > -1) {
			if (spaceIdx < columnName.length()){
				alias = columnName.substring(spaceIdx + 1);
			}
			columnName = columnName.substring(0, spaceIdx);
		}

		// process the columan name, which could have tableName.columnName notation or just columnName
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

		if (alias != null) {
			// TODO: probably should not escape alias of they contain already " or characters like ( ) @
			sb.append(" \"").append(alias).append('"');
		}

		return sb.toString();
	}
}
