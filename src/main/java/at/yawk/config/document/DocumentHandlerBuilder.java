/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.config.document;

import at.yawk.config.file.ObjectReader;
import at.yawk.config.file.ObjectWriter;
import java.lang.reflect.Type;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

/**
 * @author yawkat
 */
public class DocumentHandlerBuilder {
    private final TypeAdapterRegistry registry = new TypeAdapterRegistryImpl();

    public TypeAdapterRegistry getTypeAdapterRegistry() {
        return registry;
    }

    public DocumentHandler build() {
        return new DocumentHandler() {
            @Override
            public void write(ObjectWriter target, Object o) {
                WriterContextImpl context = new WriterContextImpl(target);
                context.writeObject(o.getClass(), o);
            }

            @SuppressWarnings("unchecked")
            @Override
            public <T> T read(ObjectReader source, Class<T> type) {
                ReaderContextImpl context = new ReaderContextImpl(source);
                return (T) context.readObject(type);
            }
        };
    }

    @RequiredArgsConstructor
    private class WriterContextImpl implements WriterContext {
        @Delegate private final ObjectWriter writer;

        @SuppressWarnings("unchecked")
        @Override
        public void writeObject(Type type, Object object) {
            TypeAdapter adapter = registry.getTypeAdapter(type);
            adapter.write(this, object);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void writeObjectKey(Type type, Object object) {
            TypeAdapter adapter = registry.getTypeAdapter(type);
            adapter.writeKey(this, object);
        }
    }

    @RequiredArgsConstructor
    private class ReaderContextImpl implements ReaderContext {
        @Delegate private final ObjectReader reader;

        @Override
        public Object readObject(Type type) {
            TypeAdapter adapter = registry.getTypeAdapter(type);
            return adapter.read(this);
        }

        @Override
        public Object readObjectKey(Type type) {
            TypeAdapter adapter = registry.getTypeAdapter(type);
            return adapter.readKey(this);
        }
    }
}
