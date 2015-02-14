/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.config.document;

import at.yawk.config.file.TokenType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Getter;

/**
 * @author yawkat
 */
class MapTypeAdapterFactory implements TypeAdapterFactory {
    @Getter
    private static final TypeAdapterFactory instance = new MapTypeAdapterFactory();

    @Override
    public TypeAdapter<?> createTypeAdapter(Type type) {
        if (type instanceof ParameterizedType && ((ParameterizedType) type).getRawType() == Map.class) {
            Type[] ta = ((ParameterizedType) type).getActualTypeArguments();
            Type keyType = ta[0];
            Type valueType = ta[1];
            return new TypeAdapter<Map<?, ?>>() {
                @Override
                public void write(WriterContext context, Map<?, ?> obj) {
                    context.enterObject();
                    obj.forEach((k, v) -> {
                        context.writeObjectKey(keyType, k);
                        context.writeObject(valueType, v);
                    });
                    context.exitObject();
                }

                @Override
                public Map<?, ?> read(ReaderContext context) {
                    Map<Object, Object> map = new LinkedHashMap<>();
                    context.enterObject();
                    while (context.peek() != TokenType.EXIT_OBJECT) {
                        Object key = context.readObjectKey(keyType);
                        Object value = context.readObject(valueType);
                        map.put(key, value);
                    }
                    context.exitObject();
                    return map;
                }
            };
        }
        return null;
    }
}
