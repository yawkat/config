/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.config.document;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import lombok.Getter;

/**
 * @author yawkat
 */
class BeanTypeAdapterFactory implements TypeAdapterFactory {
    @Getter
    private static final TypeAdapterFactory instance = new BeanTypeAdapterFactory();

    private BeanTypeAdapterFactory() {}

    @Override
    public TypeAdapter<?> createTypeAdapter(Type type) {
        if (type instanceof ParameterizedType) {
            type = ((ParameterizedType) type).getRawType();
        }
        if (type instanceof Class) {
            if (type != Object.class) {
                try {
                    ((Class<?>) type).getConstructor();
                    return new BeanTypeAdapter((Class<?>) type);
                } catch (NoSuchMethodException ignored) {}
            }
        }
        return null;
    }
}
