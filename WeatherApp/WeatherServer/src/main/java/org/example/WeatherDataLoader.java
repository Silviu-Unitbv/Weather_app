package org.example;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class WeatherDataLoader  extends WeatherData {
    public static List<WeatherData> loadData(String filePath) {
        ObjectMapper mapper = new ObjectMapper();
        try{
            return mapper.readValue(new File(filePath), new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

