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
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.commons.vfs2.FileObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

import static com.xenoamess.cyan_potion.base.DataCenter.getObjectMapper;

class ResourceInfoSerializer extends JsonSerializer<ResourceInfo> {
    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(ResourceInfo value, JsonGenerator jsonGenerator, SerializerProvider provider)
            throws IOException {
        if (value.getValues() == null) {
            return;
        }
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

class ResourceInfoDeserializer extends JsonDeserializer<ResourceInfo> {
    @JsonIgnore
    private static transient final Logger LOGGER =
            LoggerFactory.getLogger(ResourceInfoDeserializer.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public ResourceInfo deserialize(JsonParser jp, DeserializationContext ctxt)
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

        return new ResourceInfo<>(resourceInfoResourceClass, resourceInfoType, resourceInfoFileString, resourceInfoValues);
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
 * @version 0.160.0-SNAPSHOT
 */
@JsonSerialize(using = ResourceInfoSerializer.class)
@JsonDeserialize(using = ResourceInfoDeserializer.class)
public class ResourceInfo<T extends AbstractResource> {
    @JsonIgnore
    private static transient final Logger LOGGER =
            LoggerFactory.getLogger(ResourceInfo.class);

    private final Class<T> resourceClass;
    private final String type;
    private final String fileString;
    private final FileObject fileObject;
    private final String[] values;


    private String toString;

    /**
     * <p>Constructor for ResourceInfo.</p>
     *
     * @param resourceClass    a {@link java.lang.Class} object.
     * @param type             a {@link java.lang.String} object.
     * @param fileObjectString a {@link java.lang.String} object.
     * @param values           a {@link java.lang.String} object.
     */
    public ResourceInfo(Class<T> resourceClass,
                        String type,
                        String fileObjectString,
                        String... values) {
        this.resourceClass = resourceClass;
        this.type = type;
        this.fileString = fileObjectString;
        this.fileObject = ResourceManager.resolveFile(this.getFileString());
        this.values = values;

        try {
            this.toString = getObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            LOGGER.error("toString() fails, {},{}", this.getResourceClass(), this.getValues(), e);
        }
    }

    /**
     * shortcut of resourceManager.fetchResource(this);
     *
     * @param resourceManager a {@link com.xenoamess.cyan_potion.base.memory.ResourceManager} object.
     * @return a T object.
     */
    public T fetchResource(ResourceManager resourceManager) {
        return resourceManager.fetchResource(this);
    }

    /**
     * build a ResourceInfo instance from a json String.
     *
     * @param json json String.
     * @return a {@link com.xenoamess.cyan_potion.base.memory.ResourceInfo} object.
     */
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
     * <p>Getter for the field <code>resourceClass</code>.</p>
     *
     * @return resourceClass
     */
    public Class<T> getResourceClass() {
        return resourceClass;
    }

    /**
     * <p>Getter for the field <code>type</code>.</p>
     *
     * @return type
     */
    public String getType() {
        return type;
    }

    /**
     * <p>Getter for the field <code>type</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getFileString() {
        return fileString;
    }

    /**
     * <p>Getter for the field <code>fileString</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public FileObject getFileObject() {
        return fileObject;
    }

    /**
     * <p>Getter for the field <code>fileObject</code>.</p>
     *
     * @return a {@link org.apache.commons.vfs2.FileObject} object.
     */
    public String[] getValues() {
        return Arrays.copyOf(values, values.length);
    }
}