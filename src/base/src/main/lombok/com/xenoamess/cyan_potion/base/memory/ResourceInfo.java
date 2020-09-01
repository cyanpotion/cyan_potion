/*
 * MIT License
 *
 * Copyright (c) 2020 XenoAmess
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.xenoamess.cyan_potion.base.memory;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.vfs2.FileObject;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static com.xenoamess.cyan_potion.base.DataCenter.getObjectMapper;

@SuppressWarnings("rawtypes")
class ResourceInfoSerializer extends JsonSerializer<ResourceInfo> {
    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(ResourceInfo value, JsonGenerator jsonGenerator, SerializerProvider provider)
            throws IOException {
        Objects.requireNonNull(value.getValues());
        jsonGenerator.writeStartArray();
        jsonGenerator.writeString(value.getResourceClass().getCanonicalName());
        jsonGenerator.writeString(value.getType());
        jsonGenerator.writeString(value.getFileString());
        for (String string : value.getValues()) {
            jsonGenerator.writeString(string);
        }
        jsonGenerator.writeEndArray();
    }
}

@SuppressWarnings("rawtypes")
class ResourceInfoDeserializer extends JsonDeserializer<ResourceInfo> {
    @JsonIgnore
    private static final transient Logger LOGGER =
            LoggerFactory.getLogger(ResourceInfoDeserializer.class);

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public ResourceInfo deserialize(JsonParser jp, DeserializationContext deserializationContext)
            throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        if (!node.isArray()) {
            return null;
        }
        ArrayNode arrayNode = (ArrayNode) node;

        int len = arrayNode.size();
        if (len == 0) {
            return null;
        }
        Class resourceInfoResourceClass = null;
        try {
            resourceInfoResourceClass = ResourceInfo.class.getClassLoader().loadClass(arrayNode.get(0).asText());
        } catch (ClassNotFoundException e) {
            LOGGER.error("cannot find class {}", arrayNode.get(0).asText(), e);
        }

        String resourceInfoType = arrayNode.get(1).asText();
        String resourceInfoFileString = arrayNode.get(2).asText();
        String[] resourceInfoValues = new String[len - 3];
        for (int i = 3; i < len; i++) {
            resourceInfoValues[i - 3] = arrayNode.get(i).asText();
        }

        return ResourceInfo.of(
                resourceInfoResourceClass,
                resourceInfoType,
                resourceInfoFileString,
                resourceInfoValues
        );
    }
}

/**
 * ResourceInfo class.
 * ResourceInfo is used as a URL or something.
 * There will be one and only resource instance linked to each ResourceInfo instance.
 * equal ResourceInfo instances are linked to a same resource instance.
 * <p>
 * NOTICE that although we usually make this.resourceClass be a subclass of AbstractResource,
 * actually it is not a MUST.
 * in some very extreme examples,
 * WalkingAnimation4Dirs are also using ResourceInfo,
 * and it is not subclass of AbstractResource.
 * <p>
 * So never thought T MUST be AbstractResource here.
 *
 * @author XenoAmess
 * @version 0.162.3
 */
@EqualsAndHashCode
@JsonSerialize(using = ResourceInfoSerializer.class)
@JsonDeserialize(using = ResourceInfoDeserializer.class)
public final class ResourceInfo<T extends AbstractResource> {
    @JsonIgnore
    private static final transient Logger LOGGER =
            LoggerFactory.getLogger(ResourceInfo.class);

    private static final ConcurrentHashMap<String, ResourceInfo> STRING_TO_RESOURCE_INFO_MAP =
            new ConcurrentHashMap<>();

    @Getter
    private final Class<T> resourceClass;

    @Getter
    private final String type;

    @Getter
    private final String fileString;

    @Getter
    private final FileObject fileObject;

    @Getter
    private final String[] values;


    private final String toString;

    /**
     * <p>Constructor for ResourceInfo.</p>
     *
     * @param resourceClass    a {@link java.lang.Class} object.
     * @param type             a {@link java.lang.String} object.
     * @param fileObjectString a {@link java.lang.String} object.
     * @param values           a {@link java.lang.String} object.
     */
    private ResourceInfo(Class<T> resourceClass,
                         String type,
                         String fileObjectString,
                         String... values) {
        this.resourceClass = resourceClass;
        this.type = type;
        this.fileString = fileObjectString;
        this.fileObject = ResourceManager.resolveFile(this.getFileString());
        this.values = values;

        String toStringLocal = null;
        try {
            toStringLocal = getObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            LOGGER.error("toString() fails, {},{}", this.getResourceClass(), this.getValues(), e);
        }
        this.toString = toStringLocal;
    }


    public static <T extends AbstractResource> ResourceInfo<T> of(
            Class<T> resourceClass,
            String type,
            String fileObjectString,
            String... values
    ) {
        ResourceInfo<T> newResourceInfo = new ResourceInfo<T>(
                resourceClass,
                type,
                fileObjectString,
                values
        );
        ResourceInfo<T> res = STRING_TO_RESOURCE_INFO_MAP.get(newResourceInfo.toString);
        if (res == null) {
            STRING_TO_RESOURCE_INFO_MAP.put(newResourceInfo.toString, newResourceInfo);
            return newResourceInfo;
        } else {
            return res;
        }
    }

    public static <T extends AbstractResource> T fetchResource(
            ResourceManager resourceManager,
            Class<T> resourceClass,
            String type,
            String fileObjectString,
            String... values
    ) {
        return new ResourceInfo<T>(resourceClass, type, fileObjectString, values)
                .fetchResource(resourceManager);
    }

    /**
     * shortcut of resourceManager.fetchResource(this);
     *
     * @param resourceManager a {@link com.xenoamess.cyan_potion.base.memory.ResourceManager} object.
     * @return a T object.
     */
    @SuppressWarnings("unused")
    public T fetchResource(ResourceManager resourceManager) {
        return resourceManager.fetchResource(this);
    }

    /**
     * build a ResourceInfo instance from a json String.
     *
     * @param json json String.
     * @return a {@link com.xenoamess.cyan_potion.base.memory.ResourceInfo} object.
     */
    @SuppressWarnings("rawtypes")
    public static ResourceInfo of(String json) {
        ResourceInfo resourceInfo = null;
        try {
            resourceInfo = getObjectMapper().readValue(json, ResourceInfo.class);
        } catch (JsonProcessingException e) {
            LOGGER.error("getResourceJson(String json) fails, {}", json, e);
        }
        return resourceInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return toString;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ResourceInfo)) {
            return false;
        }
        return toString.equals(obj.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    /**
     * <p>Getter for the field <code>fileObject</code>.</p>
     *
     * @return a {@link org.apache.commons.vfs2.FileObject} object.
     */
    public @NotNull String[] getValues() {
        return Arrays.copyOf(values, values.length);
    }
}
