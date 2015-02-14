/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.config.document;

import java.lang.reflect.Type;
import lombok.Getter;

/**
 * @author yawkat
 */
class PrimitiveTypeAdapterFactory implements TypeAdapterFactory {
    @Getter
    private static final TypeAdapterFactory instance = new PrimitiveTypeAdapterFactory();

    private static final TypeAdapter<String> STRING_TYPE_ADAPTER = new TypeAdapter<String>() {
        @Override
        public void write(WriterContext context, String obj) {
            context.item(obj);
        }

        @Override
        public String read(ReaderContext context) {
            return context.stringValue();
        }

        @Override
        public void writeKey(WriterContext context, String obj) {
            context.key(obj);
        }

        @Override
        public String readKey(ReaderContext context) {
            return context.key();
        }
    };
    private static final TypeAdapter<Integer> INT_TYPE_ADAPTER = new TypeAdapter<Integer>() {
        @Override
        public void write(WriterContext context, Integer obj) {
            context.item(obj);
        }

        @Override
        public Integer read(ReaderContext context) {
            return context.intValue();
        }

        @Override
        public void writeKey(WriterContext context, Integer obj) {
            context.key(String.valueOf(obj));
        }

        @Override
        public Integer readKey(ReaderContext context) {
            return Integer.valueOf(context.key());
        }
    };
    private static final TypeAdapter<Long> LONG_TYPE_ADAPTER = new TypeAdapter<Long>() {
        @Override
        public void write(WriterContext context, Long obj) {
            context.item(obj);
        }

        @Override
        public Long read(ReaderContext context) {
            return context.longValue();
        }

        @Override
        public void writeKey(WriterContext context, Long obj) {
            context.key(String.valueOf(obj));
        }

        @Override
        public Long readKey(ReaderContext context) {
            return Long.valueOf(context.key());
        }
    };
    private static final TypeAdapter<Float> FLOAT_TYPE_ADAPTER = new TypeAdapter<Float>() {
        @Override
        public void write(WriterContext context, Float obj) {
            context.item(obj);
        }

        @Override
        public Float read(ReaderContext context) {
            return context.floatValue();
        }

        @Override
        public void writeKey(WriterContext context, Float obj) {
            context.key(String.valueOf(obj));
        }

        @Override
        public Float readKey(ReaderContext context) {
            return Float.valueOf(context.key());
        }
    };
    private static final TypeAdapter<Double> DOUBLE_TYPE_ADAPTER = new TypeAdapter<Double>() {
        @Override
        public void write(WriterContext context, Double obj) {
            context.item(obj);
        }

        @Override
        public Double read(ReaderContext context) {
            return context.doubleValue();
        }

        @Override
        public void writeKey(WriterContext context, Double obj) {
            context.key(String.valueOf(obj));
        }

        @Override
        public Double readKey(ReaderContext context) {
            return Double.valueOf(context.key());
        }
    };
    private static final TypeAdapter<Boolean> BOOLEAN_TYPE_ADAPTER = new TypeAdapter<Boolean>() {
        @Override
        public void write(WriterContext context, Boolean obj) {
            context.item(obj);
        }

        @Override
        public Boolean read(ReaderContext context) {
            return context.booleanValue();
        }

        @Override
        public void writeKey(WriterContext context, Boolean obj) {
            context.key(String.valueOf(obj));
        }

        @Override
        public Boolean readKey(ReaderContext context) {
            return Boolean.valueOf(context.key());
        }
    };

    private PrimitiveTypeAdapterFactory() {}

    @Override
    public TypeAdapter<?> createTypeAdapter(Type type) {
        if (type == String.class || type == CharSequence.class) { return STRING_TYPE_ADAPTER; }
        if (type == Integer.class || type == int.class) { return INT_TYPE_ADAPTER; }
        if (type == Long.class || type == long.class) { return LONG_TYPE_ADAPTER; }
        if (type == Float.class || type == float.class) { return FLOAT_TYPE_ADAPTER; }
        if (type == Double.class || type == double.class) { return DOUBLE_TYPE_ADAPTER; }
        if (type == Boolean.class || type == boolean.class) { return BOOLEAN_TYPE_ADAPTER; }
        return null;
    }
}
