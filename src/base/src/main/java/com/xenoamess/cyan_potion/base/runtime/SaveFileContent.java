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

package com.xenoamess.cyan_potion.base.runtime;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;


class SaveFileContentSerializer extends JsonSerializer<SaveFileContent> {
    @Override
    public void serialize(SaveFileContent value, JsonGenerator jsonGenerator, SerializerProvider provider)
            throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField(SaveFileContent.SaveTimeString, value.getSaveTime());
        jsonGenerator.writeArrayFieldStart(SaveFileContent.RuntimeVariableStructList);
        for (RuntimeVariableStruct runtimeVariableStruct : value.getRuntimeVariableStructList()) {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField(SaveFileContent.ClassNameString, runtimeVariableStruct.getClass().getCanonicalName());
            jsonGenerator.writeStringField(SaveFileContent.RuntimeVariableStructString, runtimeVariableStruct.saveToString());
            jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
    }
}

class SaveFileContentDeserializer extends JsonDeserializer<SaveFileContent> {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(SaveFileContentDeserializer.class);

    @Override
    public SaveFileContent deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        SaveFileContent result = new SaveFileContent();

        JsonNode node = jp.getCodec().readTree(jp);
        if (!node.isObject()) {
            return null;
        }

        result.setSaveTime(node.get(SaveFileContent.SaveTimeString).asLong());
        ArrayNode arrayNode = (ArrayNode) node.get(SaveFileContent.RuntimeVariableStructList);
        for (int i = 0, len = arrayNode.size(); i < len; i++) {
            ObjectNode objectNode = (ObjectNode) arrayNode.get(i);
            String className = objectNode.get(SaveFileContent.ClassNameString).asText();
            Class structClass = null;
            try {
                structClass = Class.forName(className);
            } catch (ClassNotFoundException e) {
                LOGGER.error("RuntimeVariableStruct class not found : {}", className, e);
            }
            RuntimeVariableStruct runtimeVariableStruct = null;
            try {
                runtimeVariableStruct = (RuntimeVariableStruct) structClass.getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                LOGGER.error("RuntimeVariableStruct class found but cannot create Instance : {}", className, e);
            }
            result.getRuntimeVariableStructList().add(runtimeVariableStruct);
        }
        return result;
    }
}

/**
 * SaveFileContent is a class used as a bridge / pojo between json string and saveTime / runtimeVariableStructList
 * I don't think user have any need to change / invoke this class.
 * If you wanna store some more information please go build your own RuntimeVariableStruct and register it into ResourceManager.
 *
 * @see RuntimeVariableStruct
 */
@JsonSerialize(using = SaveFileContentSerializer.class)
@JsonDeserialize(using = SaveFileContentDeserializer.class)
class SaveFileContent {
    public static final String ClassNameString = "className";
    public static final String RuntimeVariableStructString = "runtimeVariableStruct";
    public static final String SaveTimeString = "saveTime";
    public static final String RuntimeVariableStructList = "runtimeVariableStructList";

    private long saveTime;
    private final List<RuntimeVariableStruct> runtimeVariableStructList = new ArrayList<>();

    public SaveFileContent() {
        this.setSaveTime(System.currentTimeMillis());
    }

    public List<RuntimeVariableStruct> getRuntimeVariableStructList() {
        return runtimeVariableStructList;
    }

    /**
     * @return
     */
    public long getSaveTime() {
        return saveTime;
    }

    public void setSaveTime(long saveTime) {
        this.saveTime = saveTime;
    }
}