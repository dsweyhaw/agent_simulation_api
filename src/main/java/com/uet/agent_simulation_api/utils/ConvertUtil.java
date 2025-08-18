package com.uet.agent_simulation_api.utils;

import org.springframework.stereotype.Service;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class ConvertUtil {
    /**
     * Convert a string to an integer.
     *
     * @param str The string to convert.
     * @return The integer value of the string or null if the string is not a valid integer.
     */
    public Integer convertStringToInteger(String str) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Convert a string from snake case to camel case
     *
     * @param str the string to convert
     * @return the camel case string
     */
    public String convertSnakeCaseToCamelCase(String str) {
        var parts = str.split("_");

        return IntStream.range(0, parts.length)
                .mapToObj(index -> {
                    String part = parts[index];
                    if (index == 0)
                        return part.toLowerCase();

                    return Character.toUpperCase(part.charAt(0)) + part.substring(1).toLowerCase();
                })
                .collect(Collectors.joining());
    }
}
