/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.config.document;

import at.yawk.config.ConfigurationException;
import at.yawk.config.file.TokenType;
import at.yawk.reflect.Annotations;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yawkat
 */
@Slf4j
@RequiredArgsConstructor
class BeanTypeAdapter implements TypeAdapter<Object> {
    private final Class<?> clazz;

    @Override
    public void write(WriterContext context, Object obj) {
        context.enterObject();

        boolean serializeClass = true;
        Serialize serializeClassAnnotation = clazz.getAnnotation(Serialize.class);
        if (serializeClassAnnotation != null) {
            serializeClass = serializeClassAnnotation.value();
        }
        for (Method method : clazz.getMethods()) {
            if (method.getParameterCount() != 0) { continue; }
            if (Modifier.isStatic(method.getModifiers())) { continue; }
            if (method.getName().equals("getClass")) { continue; }

            String itemName = getItemName(method.getName());
            if (itemName == null) { continue; }

            boolean serialize = serializeClass;
            Serialize methodSerialize = Annotations.locateAnnotation(Serialize.class, method);
            if (methodSerialize != null) {
                serialize = methodSerialize.value();
            }
            if (!serialize) { continue; }

            Object value;
            try {
                value = method.invoke(obj);
            } catch (IllegalAccessException | InvocationTargetException e) {
                log.warn("Error while getting bean property", e);
                continue;
            }

            if (value == null) {
                continue;
            }

            DescribedAs description = method.getAnnotation(DescribedAs.class);
            if (description == null) {
                try {
                    // check field too
                    description = method.getDeclaringClass()
                            .getDeclaredField(itemName)
                            .getAnnotation(DescribedAs.class);
                } catch (NoSuchFieldException ignored) {}
            }
            if (description != null) {
                context.comment(description.value());
            }

            context.key(itemName);
            context.writeObject(method.getGenericReturnType(), value);
        }

        context.exitObject();
    }

    private String getItemName(String getterMethodName) {
        int prefixLength;
        if (getterMethodName.startsWith("is")) {
            prefixLength = 2;
        } else if (getterMethodName.startsWith("get")) {
            prefixLength = 3;
        } else {
            return null;
        }
        if (prefixLength == getterMethodName.length()) { return null; }
        StringBuilder sb = new StringBuilder(getterMethodName.length() - prefixLength);
        sb.append(getterMethodName, prefixLength, getterMethodName.length());
        sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
        return sb.toString();
    }

    @Override
    public Object read(ReaderContext context) {
        Object instance;
        try {
            instance = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ConfigurationException(e);
        }
        context.enterObject();
        while (true) {
            TokenType peek = context.peek();
            if (peek == TokenType.EXIT_OBJECT) {
                context.exitObject();
                break;
            }
            String name = context.key();
            String setterName = getSetterName(name);
            Method target = null;
            for (Method method : instance.getClass().getMethods()) {
                if (method.getName().equals(setterName) && method.getParameterCount() == 1) {
                    target = method;
                    break;
                }
            }
            if (target == null) {
                context.skipDeep();
                // todo: error?
                continue;
            }
            Object obj = context.readObject(target.getGenericParameterTypes()[0]);
            try {
                target.invoke(instance, obj);
            } catch (IllegalAccessException | InvocationTargetException e) {
                log.warn("Error while setting bean property", e);
            }
        }
        return instance;
    }

    private String getSetterName(String itemName) {
        return "set" + Character.toUpperCase(itemName.charAt(0)) + itemName.substring(1);
    }
}
