package com.xenoamess.cyan_potion.base.memory;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.xenoamess.cyan_potion.base.DataCenter.getObjectMapper;

class ResourceJsonSerializer extends JsonSerializer<ResourceInfo> {
    @Override
    public void serialize(ResourceInfo value, JsonGenerator jsonGenerator, SerializerProvider provider)
            throws IOException {
        if (value.values == null) {
            return;
        }
        jsonGenerator.writeStartArray();
        jsonGenerator.writeString(value.resourceClass.getCanonicalName());
        jsonGenerator.writeString(value.type);
        jsonGenerator.writeString(value.fileString);
        for (String string : value.values) {
            jsonGenerator.writeString(string);
        }
        jsonGenerator.writeEndArray();
    }
}

class ResourceJsonDeserializer extends JsonDeserializer<ResourceInfo> {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ResourceJsonDeserializer.class);

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

        return new ResourceInfo(resourceInfoResourceClass, resourceInfoType, resourceInfoFileString, resourceInfoValues);
    }
}

@JsonSerialize(using = ResourceJsonSerializer.class)
@JsonDeserialize(using = ResourceJsonDeserializer.class)
public class ResourceInfo<T extends AbstractResource> {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ResourceInfo.class);
    public final Class<T> resourceClass;
    public final String type;
    public final String fileString;
    public FileObject fileObject;
    public final String[] values;

    private String toString;

    public ResourceInfo(Class<T> resourceClass,
                        String type,
                        String fileObjectString,
                        String... values) {
        this.resourceClass = resourceClass;
        this.type = type;
        this.fileString = fileObjectString;
        try {
            this.fileObject = ResourceManager.getFileSystemManager().resolveFile(this.fileString);
        } catch (FileSystemException e) {
            e.printStackTrace();
        }
        this.values = values;

        try {
            this.toString = getObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            LOGGER.error("toString() fails, {},{}", this.resourceClass, this.values, e);
        }
    }

    public T fetchResource(ResourceManager resourceManager) {
        return resourceManager.fetchResource(this);
    }

    public static ResourceInfo of(String json) {
        ResourceInfo resourceInfo = null;
        try {
            resourceInfo = getObjectMapper().readValue(json, ResourceInfo.class);
        } catch (JsonProcessingException e) {
            LOGGER.error("getResourceJson(String json) fails, {}", json, e);
        }
        return resourceInfo;
    }

    @Override
    public String toString() {
        return toString;
    }

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
//        ResourceInfo resourceInfo = (ResourceInfo) obj;
//
//        if (!Objects.equals(this.resourceClass, resourceInfo.resourceClass)) {
//            return false;
//        }
//
//        if (!Objects.equals(this.type, resourceInfo.type)) {
//            return false;
//        }
//
//        if (!Objects.equals(this.fileString, resourceInfo.fileString)) {
//            return false;
//        }
//
//        return Arrays.equals(this.values, resourceInfo.values);
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

}
