/**
 * Copyright 2013 Alex Yanchenko
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package com.javen.orm.inner.ann.sql;

import com.javen.orm.annotation.sql.Column;
import com.javen.orm.inner.ann.Ann;

public final class ColumnAnn extends Ann<Column> {

	public String name;
	public boolean nullable;
	public final boolean unique;
	public boolean eager;

	public ColumnAnn(Column annotation) {
		super(annotation);
		if (hackSuccess()) {
			name = (String) getElement(NAME);
			nullable = (Boolean) getElement(NULLABLE);
			unique = (Boolean) getElement(UNIQUE);
			eager = (Boolean) getElement(EAGER);
			cleanup();
		} else {
			name = annotation.name();
			nullable = annotation.nullable();
			unique = annotation.unique();
			eager = annotation.eager();
		}
	}

}
