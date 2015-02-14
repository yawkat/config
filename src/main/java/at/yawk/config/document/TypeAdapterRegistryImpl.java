/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.config.document;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yawkat
 */
class TypeAdapterRegistryImpl implements TypeAdapterRegistry {
    private final List<TypeAdapterFactory> factories = new ArrayList<>(Arrays.asList(
            // gson first because it may get detected by other factories
            GsonWrapperTypeAdapterFactory.getInstance(),

            CollectionTypeAdapterFactory.getInstance(),
            MapTypeAdapterFactory.getInstance(),
            PrimitiveTypeAdapterFactory.getInstance(),
            EnumTypeAdapterFactory.getInstance(),
            BeanTypeAdapterFactory.getInstance(),
            AnnotatedTypeAdapterFactory.getInstance()
    ));

    private final Map<Type, TypeAdapter<?>> cache = new ConcurrentHashMap<>();

    @Override
    public TypeAdapterRegistry clear() {
        factories.clear();
        cache.clear();
        return this;
    }

    @Override
    public <T> TypeAdapterRegistry registerAdapter(Class<T> type, TypeAdapter<T> adapter) {
        return registerAdapterFactory(sub -> {
            if (sub instanceof ParameterizedType) {
                sub = ((ParameterizedType) sub).getRawType();
            }
            if (sub instanceof Class && type.isAssignableFrom((Class<?>) sub)) {
                return adapter;
            }
            return null;
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public TypeAdapterRegistry registerAdapter(TypeAdapter<?> adapter) {
        for (Type gitf : adapter.getClass().getGenericInterfaces()) {
            if (gitf instanceof ParameterizedType &&
                ((ParameterizedType) gitf).getRawType() == TypeAdapter.class) {
                return registerAdapter((Class) ((ParameterizedType) gitf).getActualTypeArguments()[0], adapter);
            }
        }
        throw new IllegalArgumentException("Must declare generic type");
    }

    @Override
    public TypeAdapterRegistry registerAdapterFactory(TypeAdapterFactory factory) {
        factories.add(Objects.requireNonNull(factory));
        return this;
    }

    @Override
    public TypeAdapter<?> getTypeAdapter(Type type) {
        return cache.computeIfAbsent(Objects.requireNonNull(type), t -> {
            for (TypeAdapterFactory factory : factories) {
                TypeAdapter<?> a = factory.createTypeAdapter(t);
                if (a != null) {
                    return a;
                }
            }
            throw new UnsupportedOperationException("Unsupported type " + t.getTypeName());
        });
    }
}
