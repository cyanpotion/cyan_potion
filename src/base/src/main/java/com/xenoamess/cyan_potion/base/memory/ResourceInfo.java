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
import java.util.Arrays;
import java.util.Objects;

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
        jsonGenerator.writeString(value.fileObject.getURL().toString());
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
        ResourceInfo resourceInfo = new ResourceInfo();
        int len = arrayNode.size();
        if (len == 0) {
            return null;
        }
        try {
            resourceInfo.resourceClass = ResourceInfo.class.getClassLoader().loadClass(arrayNode.get(0).asText());
        } catch (ClassNotFoundException e) {
            LOGGER.error("cannot find class {}", arrayNode.get(0).asText(), e);
        }
        resourceInfo.type = arrayNode.get(1).asText();
        resourceInfo.fileObject = ResourceManager.getFileSystemManager().resolveFile(arrayNode.get(2).asText());
        resourceInfo.values = new String[len - 3];
        for (int i = 1; i < len; i++) {
            resourceInfo.values[i - 1] = arrayNode.get(i).asText();
        }
        return resourceInfo;
    }
}

@JsonSerialize(using = ResourceJsonSerializer.class)
@JsonDeserialize(using = ResourceJsonDeserializer.class)
public class ResourceInfo {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ResourceInfo.class);
    public Class resourceClass;
    public String type;
    public FileObject fileObject;
    public String[] values;

    public ResourceInfo() {
    }

    public ResourceInfo(Class resourceClass,
                        String type,
                        FileObject fileObject,
                        String... values) {
        this.resourceClass = resourceClass;
        this.type = type;
        this.fileObject = fileObject;
        this.values = values;
    }

    public ResourceInfo(Class resourceClass,
                        String type,
                        String fileObjectString,
                        String... values) {
        this.resourceClass = resourceClass;
        this.type = type;
        try {
            this.fileObject = ResourceManager.getFileSystemManager().resolveFile(fileObjectString);
        } catch (FileSystemException e) {
            e.printStackTrace();
        }
        this.values = values;
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
        String string = "";
        try {
            string = getObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            LOGGER.error("toString() fails, {},{}", this.resourceClass, this.values, e);
        }
        return string;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ResourceInfo)) {
            return false;
        }
        ResourceInfo resourceInfo = (ResourceInfo) obj;

        if (!Objects.equals(this.resourceClass, resourceInfo.resourceClass)) {
            return false;
        }

        if (!Objects.equals(this.type, resourceInfo.type)) {
            return false;
        }

        if (!Objects.equals(this.fileObject, resourceInfo.fileObject)) {
            return false;
        }

        return Arrays.equals(this.values, resourceInfo.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resourceClass, type, fileObject, values);
    }
}
