/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.config.document;

import at.yawk.config.file.TokenType;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.lang.reflect.Type;
import java.util.Map;
import lombok.Getter;

/**
 * @author yawkat
 */
class GsonWrapperTypeAdapterFactory implements TypeAdapterFactory {
    @Getter
    private static final TypeAdapterFactory instance;

    static {
        boolean available;
        try {
            Class.forName(JsonElement.class.getName());
            available = true;
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            available = false;
        }
        instance = available ? new GsonWrapperTypeAdapterFactory() : type -> null;
    }

    @Override
    public TypeAdapter<?> createTypeAdapter(Type type) {
        if (type instanceof Class) {
            if (JsonPrimitive.class.isAssignableFrom((Class<?>) type)) {
                return new TypeAdapter<JsonPrimitive>() {
                    @Override
                    public void write(WriterContext context, JsonPrimitive obj) {
                        if (obj.isNumber()) {
                            if (obj.getAsDouble() == obj.getAsLong()) {
                                context.item(obj.getAsLong());
                            } else {
                                context.item(obj.getAsDouble());
                            }
                        } else if (obj.isBoolean()) {
                            context.item(obj.getAsBoolean());
                        } else if (obj.isString()) {
                            context.item(obj.getAsString());
                        }
                    }

                    @Override
                    public JsonPrimitive read(ReaderContext context) {
                        return readPrimitive(context);
                    }
                };
            }
            if (JsonArray.class.isAssignableFrom((Class<?>) type)) {
                return new TypeAdapter<JsonArray>() {
                    @Override
                    public void write(WriterContext context, JsonArray obj) {
                        context.enterList();
                        for (JsonElement element : obj) {
                            context.writeObject(element.getClass(), element);
                        }
                        context.exitList();
                    }

                    @Override
                    public JsonArray read(ReaderContext context) {
                        return readArray(context);
                    }
                };
            }
            if (JsonObject.class.isAssignableFrom((Class<?>) type)) {
                return new TypeAdapter<JsonObject>() {
                    @Override
                    public void write(WriterContext context, JsonObject obj) {
                        context.enterObject();
                        for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                            context.key(entry.getKey());
                            context.writeObject(entry.getValue().getClass(), entry.getValue());
                        }
                        context.exitObject();
                    }

                    @Override
                    public JsonObject read(ReaderContext context) {
                        return readObject(context);
                    }
                };
            }
        }
        return null;
    }

    private JsonObject readObject(ReaderContext context) {
        JsonObject object = new JsonObject();
        context.enterObject();
        while (context.peek() != TokenType.EXIT_OBJECT) {
            String key = context.key();
            JsonElement value = readAny(context);
            object.add(key, value);
        }
        context.exitObject();
        return object;
    }

    private JsonArray readArray(ReaderContext context) {
        JsonArray array = new JsonArray();

        context.enterList();
        while (context.peek() != TokenType.EXIT_LIST) {
            array.add(readAny(context));
        }
        context.exitList();
        return array;
    }

    private JsonElement readAny(ReaderContext context) {
        switch (context.peek()) {
        case ENTER_LIST:
            return readArray(context);
        case ENTER_OBJECT:
            return readObject(context);
        default:
            return readPrimitive(context);
        }
    }

    private JsonPrimitive readPrimitive(ReaderContext context) {
        switch (context.peek()) {
        case STRING:
            return new JsonPrimitive(context.stringValue());
        case BOOLEAN:
            return new JsonPrimitive(context.booleanValue());
        default:
            return new JsonPrimitive(context.doubleValue());
        }
    }
}
