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
class EnumTypeAdapterFactory implements TypeAdapterFactory {
    @Getter
    private static final TypeAdapterFactory instance = new EnumTypeAdapterFactory();

    private EnumTypeAdapterFactory() {}

    @Override
    public TypeAdapter<?> createTypeAdapter(Type type) {
        if (type instanceof Class && ((Class) type).isEnum()) {
            return new TypeAdapter<Enum>() {
                @Override
                public void write(WriterContext context, Enum obj) {
                    context.item(obj.name());
                }

                @Override
                public Enum read(ReaderContext context) {
                    return Enum.valueOf((Class) type, context.stringValue());
                }

                @Override
                public void writeKey(WriterContext context, Enum obj) {
                    context.key(obj.name());
                }

                @Override
                public Enum readKey(ReaderContext context) {
                    return Enum.valueOf((Class) type, context.key());
                }
            };
        }
        return null;
    }
}
