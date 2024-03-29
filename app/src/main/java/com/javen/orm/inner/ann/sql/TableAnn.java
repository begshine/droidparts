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

import com.javen.orm.annotation.sql.Table;
import com.javen.orm.inner.ann.Ann;

public final class TableAnn extends Ann<Table> {

	public final String name;

	public TableAnn(Table annotation) {
		super(annotation);
		if (hackSuccess()) {
			name = (String) getElement(NAME);
			cleanup();
		} else {
			name = annotation.name();
		}
	}

}