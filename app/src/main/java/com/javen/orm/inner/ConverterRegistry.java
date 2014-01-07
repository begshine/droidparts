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

import java.util.HashMap;
import java.util.LinkedHashSet;

import com.javen.orm.inner.converter.ArrayCollectionConverter;
import com.javen.orm.inner.converter.BitmapConverter;
import com.javen.orm.inner.converter.BooleanConverter;
import com.javen.orm.inner.converter.ByteArrayConverter;
import com.javen.orm.inner.converter.ByteConverter;
import com.javen.orm.inner.converter.CharacterConverter;
import com.javen.orm.inner.converter.Converter;
import com.javen.orm.inner.converter.DateConverter;
import com.javen.orm.inner.converter.DoubleConverter;
import com.javen.orm.inner.converter.EntityConverter;
import com.javen.orm.inner.converter.EnumConverter;
import com.javen.orm.inner.converter.FloatConverter;
import com.javen.orm.inner.converter.IntegerConverter;
import com.javen.orm.inner.converter.JSONArrayConverter;
import com.javen.orm.inner.converter.JSONObjectConverter;
import com.javen.orm.inner.converter.LongConverter;
import com.javen.orm.inner.converter.ModelConverter;
import com.javen.orm.inner.converter.ShortConverter;
import com.javen.orm.inner.converter.StringConverter;
import com.javen.orm.inner.converter.UUIDConverter;
import com.javen.orm.inner.converter.UriConverter;

public class ConverterRegistry {

	private static final LinkedHashSet<Converter<?>> converters = new LinkedHashSet<Converter<?>>();

	private static final HashMap<Class<?>, Converter<?>> map = new HashMap<Class<?>, Converter<?>>();

	static {
		converters.add(new BooleanConverter());
		converters.add(new ByteConverter());
		converters.add(new CharacterConverter());
		converters.add(new DoubleConverter());
		converters.add(new FloatConverter());
		converters.add(new IntegerConverter());
		converters.add(new LongConverter());
		converters.add(new ShortConverter());
		converters.add(new StringConverter());
		converters.add(new EnumConverter());
		converters.add(new DateConverter());
		converters.add(new UUIDConverter());
		converters.add(new UriConverter());
		converters.add(new ByteArrayConverter());
		converters.add(new JSONObjectConverter());
		converters.add(new JSONArrayConverter());
		converters.add(new BitmapConverter());
		converters.add(new ModelConverter());
		converters.add(new EntityConverter());
		converters.add(new ArrayCollectionConverter());
	}

	@SuppressWarnings("unchecked")
	public static <T> Converter<T> getConverter(Class<T> cls)
			throws IllegalArgumentException {
		Converter<?> converter = map.get(cls);
		if (converter == null) {
			for (Converter<?> h : converters) {
				if (h.canHandle(cls)) {
					converter = h;
					map.put(cls, converter);
					break;
				}
			}
		}
		if (converter != null) {
			return (Converter<T>) converter;
		} else {
			throw new IllegalArgumentException("No converter for '"
					+ cls.getName() + "'.");
		}
	}

	public static void registerConverter(Converter<?> converter) {
		converters.add(converter);
	}

	private ConverterRegistry() {
	}

}
