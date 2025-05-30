package com.spakbor.utils;

import com.google.gson.*;
import com.google.gson.internal.*;
import com.google.gson.reflect.*;
import com.google.gson.stream.*;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Adapts values whose runtime type may differ from their declaration type. 
 * This is necessary when a field's type is abstract or an interface.
 * 
 * Taken from Google's Gson extras:
 * https://github.com/google/gson/blob/master/extras/src/main/java/com/google/gson/typeadapters/RuntimeTypeAdapterFactory.java
 */
public final class RuntimeTypeAdapterFactory<T> implements TypeAdapterFactory {
  private final Class<?> baseType;
  private final String typeFieldName;
  private final boolean maintainType;
  private final Map<String, Class<?>> labelToSubtype = new LinkedHashMap<>();
  private final Map<Class<?>, String> subtypeToLabel = new LinkedHashMap<>();

  private RuntimeTypeAdapterFactory(Class<?> baseType, String typeFieldName, boolean maintainType) {
    if (typeFieldName == null || baseType == null) {
      throw new NullPointerException();
    }
    this.baseType = baseType;
    this.typeFieldName = typeFieldName;
    this.maintainType = maintainType;
  }

  /**
   * Creates a new RuntimeTypeAdapterFactory for base type using type field name
   * @param baseType the base class for which to register subtypes
   * @param typeFieldName the JSON field name to distinguish types
   * @return the factory
   */
  public static <T> RuntimeTypeAdapterFactory<T> of(Class<T> baseType, String typeFieldName) {
    return new RuntimeTypeAdapterFactory<>(baseType, typeFieldName, false);
  }

  /**
   * Creates a new RuntimeTypeAdapterFactory for base type using "type" as the field name
   * @param baseType the base class for which to register subtypes
   * @return the factory
   */
  public static <T> RuntimeTypeAdapterFactory<T> of(Class<T> baseType) {
    return new RuntimeTypeAdapterFactory<>(baseType, "type", false);
  }

  /**
   * Registers subtype for this factory
   * @param type the subtype class
   * @param label the label used in JSON to identify this subtype
   * @return this factory for chaining
   */
  public RuntimeTypeAdapterFactory<T> registerSubtype(Class<? extends T> type, String label) {
    if (type == null || label == null) {
      throw new NullPointerException();
    }
    if (subtypeToLabel.containsKey(type) || labelToSubtype.containsKey(label)) {
      throw new IllegalArgumentException("types and labels must be unique");
    }
    labelToSubtype.put(label, type);
    subtypeToLabel.put(type, label);
    return this;
  }

  @Override
  public <R> TypeAdapter<R> create(Gson gson, TypeToken<R> type) {
    if (!baseType.isAssignableFrom(type.getRawType())) {
      return null;
    }

    final Map<String, TypeAdapter<?>> labelToDelegate = new LinkedHashMap<>();
    final Map<Class<?>, TypeAdapter<?>> subtypeToDelegate = new LinkedHashMap<>();
    for (Map.Entry<String, Class<?>> entry : labelToSubtype.entrySet()) {
      TypeAdapter<?> delegate = gson.getDelegateAdapter(this, TypeToken.get(entry.getValue()));
      labelToDelegate.put(entry.getKey(), delegate);
      subtypeToDelegate.put(entry.getValue(), delegate);
    }

    return new TypeAdapter<R>() {
      @Override
      public R read(JsonReader in) throws IOException {
        JsonElement jsonElement = JsonParser.parseReader(in);
        JsonElement labelJsonElement;
        if (maintainType) {
          labelJsonElement = jsonElement.getAsJsonObject().get(typeFieldName);
        } else {
          labelJsonElement = jsonElement.getAsJsonObject().remove(typeFieldName);
        }

        if (labelJsonElement == null) {
          throw new JsonParseException("cannot deserialize " + baseType + " because it does not define a field named " + typeFieldName);
        }
        String label = labelJsonElement.getAsString();
        @SuppressWarnings("unchecked")
        TypeAdapter<R> delegate = (TypeAdapter<R>) labelToDelegate.get(label);
        if (delegate == null) {
          throw new JsonParseException("cannot deserialize " + baseType + " subtype named " + label + "; did you forget to register a subtype?");
        }
        return delegate.fromJsonTree(jsonElement);
      }

      @Override
      public void write(JsonWriter out, R value) throws IOException {
        Class<?> srcType = value.getClass();
        String label = subtypeToLabel.get(srcType);
        @SuppressWarnings("unchecked")
        TypeAdapter<R> delegate = (TypeAdapter<R>) subtypeToDelegate.get(srcType);
        if (delegate == null) {
          throw new JsonParseException("cannot serialize " + srcType.getName() + "; did you forget to register a subtype?");
        }
        JsonObject jsonObject = delegate.toJsonTree(value).getAsJsonObject();

        if (maintainType) {
          com.google.gson.internal.Streams.write(jsonObject, out);
          return;
        }

        JsonObject clone = new JsonObject();

        clone.add(typeFieldName, new JsonPrimitive(label));
        for (Map.Entry<String, JsonElement> e : jsonObject.entrySet()) {
          clone.add(e.getKey(), e.getValue());
        }
        com.google.gson.internal.Streams.write(clone, out);
      }
    }.nullSafe();
  }
}
