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
package com.javen.orm.model;

import com.javen.orm.annotation.sql.Column;
import com.javen.orm.contract.DB;

public abstract class Entity extends Model {
	private static final long serialVersionUID = 1L;

	public Entity() {
		// Subclasses must have a no-argument constructor
		// and should override hashCode() and equals().
	}

	@Column(name = DB.Column.ID)
	public long id;

	@Override
	public int hashCode() {
		return getClass().hashCode() + (int) id;
	}

}
