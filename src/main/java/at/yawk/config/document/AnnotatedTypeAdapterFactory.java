/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.config.document;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yawkat
 */
@Slf4j
class AnnotatedTypeAdapterFactory implements TypeAdapterFactory {
    @Getter
    private static final TypeAdapterFactory instance = new AnnotatedTypeAdapterFactory();

    private AnnotatedTypeAdapterFactory() {}

    @Override
    public TypeAdapter<?> createTypeAdapter(Type type) {
        if (type instanceof ParameterizedType) {
            type = ((ParameterizedType) type).getRawType();
        }
        if (type instanceof Class<?>) {
            SerializedBy serializedBy = ((Class<?>) type).getAnnotation(SerializedBy.class);
            if (serializedBy != null) {
                try {
                    return serializedBy.value().newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    log.warn("Failed to instantiate annotated type adapter " + serializedBy.value());
                }
            }
        }
        return null;
    }
}
