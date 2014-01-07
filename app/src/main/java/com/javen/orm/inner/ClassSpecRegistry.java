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
package com.javen.orm.inner;

import static com.javen.orm.inner.AnnBuilder.getColumnAnn;
import static com.javen.orm.inner.AnnBuilder.getKeyAnn;
import static com.javen.orm.inner.AnnBuilder.getTableAnn;
import static com.javen.orm.inner.ReflectionUtils.buildClassHierarchy;
import static com.javen.orm.inner.ReflectionUtils.getArrayComponentType;
import static com.javen.orm.inner.ReflectionUtils.getFieldGenericArgs;
import static com.javen.orm.inner.TypeHelper.isArray;
import static com.javen.orm.inner.TypeHelper.isBoolean;
import static com.javen.orm.inner.TypeHelper.isByte;
import static com.javen.orm.inner.TypeHelper.isCharacter;
import static com.javen.orm.inner.TypeHelper.isCollection;
import static com.javen.orm.inner.TypeHelper.isDouble;
import static com.javen.orm.inner.TypeHelper.isEntity;
import static com.javen.orm.inner.TypeHelper.isFloat;
import static com.javen.orm.inner.TypeHelper.isInteger;
import static com.javen.orm.inner.TypeHelper.isLong;
import static com.javen.orm.inner.TypeHelper.isShort;
import static com.javen.orm.util.Strings.isEmpty;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import com.javen.orm.inner.ann.FieldSpec;
import com.javen.orm.inner.ann.json.KeyAnn;
import com.javen.orm.inner.ann.sql.ColumnAnn;
import com.javen.orm.inner.ann.sql.TableAnn;
import com.javen.orm.model.Entity;
import com.javen.orm.model.Model;
import com.javen.orm.util.L;

public final class ClassSpecRegistry {

	// SQL

	public static String getTableName(Class<? extends Entity> cls) {
		String name = TABLE_NAMES.get(cls);
		if (name == null) {
			TableAnn ann = getTableAnn(cls);
			if (ann != null) {
				name = ((TableAnn) ann).name;
			}
			if (isEmpty(name)) {
				name = cls.getSimpleName();
			}
			TABLE_NAMES.put(cls, name);
		}
		return name;
	}

	@SuppressWarnings("unchecked")
	public static FieldSpec<ColumnAnn>[] getTableColumnSpecs(
			Class<? extends Entity> cls) {
		FieldSpec<ColumnAnn>[] specs = COLUMN_SPECS.get(cls);
		if (specs == null) {
			ArrayList<FieldSpec<ColumnAnn>> list = new ArrayList<FieldSpec<ColumnAnn>>();
			for (Class<?> cl : buildClassHierarchy(cls)) {
				for (Field field : cl.getDeclaredFields()) {
					ColumnAnn ann = getColumnAnn(field);
					if (ann != null) {
						Class<?> componentType = getComponentType(field);
						ann.name = getColumnName(ann, field);
						list.add(new FieldSpec<ColumnAnn>(field, componentType,
								ann));
					}
				}
			}
			sanitizeSpecs(list);
			specs = list.toArray(new FieldSpec[list.size()]);
			COLUMN_SPECS.put(cls, specs);
		}
		return specs;
	}

	// JSON
	@SuppressWarnings("unchecked")
	public static FieldSpec<KeyAnn>[] getJsonKeySpecs(Class<? extends Model> cls) {
		FieldSpec<KeyAnn>[] specs = KEY_SPECS.get(cls);
		if (specs == null) {
			ArrayList<FieldSpec<KeyAnn>> list = new ArrayList<FieldSpec<KeyAnn>>();
			for (Class<?> cl : buildClassHierarchy(cls)) {
				for (Field field : cl.getDeclaredFields()) {
					KeyAnn ann = getKeyAnn(field);
					if (ann != null) {
						Class<?> componentType = getComponentType(field);
						ann.name = getKeyName(ann, field);
						list.add(new FieldSpec<KeyAnn>(field, componentType,
								ann));
					}
				}
			}
			specs = list.toArray(new FieldSpec[list.size()]);
			KEY_SPECS.put(cls, specs);
		}
		return specs;
	}

	// caches

	private static final ConcurrentHashMap<Class<? extends Entity>, String> TABLE_NAMES = new ConcurrentHashMap<Class<? extends Entity>, String>();
	private static final ConcurrentHashMap<Class<? extends Entity>, FieldSpec<ColumnAnn>[]> COLUMN_SPECS = new ConcurrentHashMap<Class<? extends Entity>, FieldSpec<ColumnAnn>[]>();

	private static final ConcurrentHashMap<Class<? extends Model>, FieldSpec<KeyAnn>[]> KEY_SPECS = new ConcurrentHashMap<Class<? extends Model>, FieldSpec<KeyAnn>[]>();

	// Utils

	private static Class<?> getComponentType(Field field) {
		Class<?> componentType = null;
		Class<?> fieldType = field.getType();
		if (isArray(fieldType)) {
			componentType = getArrayComponentType(fieldType);
		} else if (isCollection(fieldType)) {
			Class<?>[] genericArgs = getFieldGenericArgs(field);
			componentType = (genericArgs.length > 0) ? genericArgs[0]
					: Object.class;
		}
		return componentType;
	}

	// JSON

	private static String getKeyName(KeyAnn ann, Field field) {
		String name = ann.name;
		if (isEmpty(name)) {
			name = field.getName();
		}
		return name;
	}

	// SQL

	private static String getColumnName(ColumnAnn ann, Field field) {
		String name = ann.name;
		if (isEmpty(name)) {
			name = field.getName();
			if (isEntity(field.getType()) && !name.endsWith(ID_AFFIX)) {
				name += ID_AFFIX;
			}
		}
		return name;
	}

	private static final String ID_AFFIX = "_id";

	private static void sanitizeSpecs(
			ArrayList<FieldSpec<ColumnAnn>> columnSpecs) {
		for (FieldSpec<ColumnAnn> spec : columnSpecs) {
			Class<?> fieldType = spec.field.getType();
			if (spec.ann.nullable) {
				if (isBoolean(fieldType, false) || isInteger(fieldType, false)
						|| isLong(fieldType, false)
						|| isFloat(fieldType, false)
						|| isDouble(fieldType, false)
						|| isByte(fieldType, false)
						|| isShort(fieldType, false)
						|| isCharacter(fieldType, false)) {
					L.w("%s can't be null.", fieldType.getSimpleName());
					spec.ann.nullable = false;
				}
			} else if (spec.ann.eager) {
				if (!isEntity(fieldType) && !isEntity(spec.componentType)) {
					L.w("%s can't be eager.", fieldType.getSimpleName());
					spec.ann.eager = false;
				}
			}
		}
	}

}
