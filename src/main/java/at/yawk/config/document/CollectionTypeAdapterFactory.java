/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.config.document;

import at.yawk.config.file.TokenType;
import com.google.common.collect.ImmutableMap;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Supplier;
import lombok.Getter;

/**
 * @author yawkat
 */
public class CollectionTypeAdapterFactory implements TypeAdapterFactory {
    @Getter
    private static final TypeAdapterFactory instance = new CollectionTypeAdapterFactory();

    private static final Map<Class<?>, Supplier<Collection<Object>>> implementations =
            ImmutableMap.of(
                    List.class, ArrayList::new,
                    Set.class, LinkedHashSet::new,
                    Collection.class, ArrayList::new,
                    Queue.class, ArrayDeque::new
            );

    private CollectionTypeAdapterFactory() {}

    @Override
    public TypeAdapter<?> createTypeAdapter(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            if (pt.getRawType() instanceof Class) {
                Type contentType = pt.getActualTypeArguments()[0];
                Class<?> baseType = (Class<?>) pt.getRawType();
                Supplier<Collection<Object>> factory = implementations.get(baseType);
                if (factory != null) {
                    return new TypeAdapter<Collection>() {
                        @Override
                        public void write(WriterContext context, Collection obj) {
                            context.enterList();
                            for (Object entry : obj) {
                                context.writeObject(contentType, entry);
                            }
                            context.exitList();
                        }

                        @Override
                        public Collection read(ReaderContext context) {
                            Collection<Object> objects = factory.get();
                            context.enterList();
                            while (context.peek() != TokenType.EXIT_LIST) {
                                objects.add(context.readObject(contentType));
                            }
                            context.exitList();
                            return objects;
                        }
                    };
                }
            }
        }
        return null;
    }
}
