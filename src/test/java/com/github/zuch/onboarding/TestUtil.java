package com.github.zuch.onboarding;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class TestUtil {

    public static <T> T loadFileToObject(final String path, final Class<T> clazz) throws IOException {
        final ObjectMapper om = new ObjectMapper();
        om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        final JavaTimeModule javaTimeModule = new JavaTimeModule();
        om.registerModules(javaTimeModule);
        final ClassLoader classLoader = TestUtil.class.getClassLoader();
        final File file = new File(Objects.requireNonNull(classLoader.getResource(path)).getFile());
        return om.readValue(file, clazz);
    }
}
