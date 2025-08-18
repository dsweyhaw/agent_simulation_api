package com.uet.agent_simulation_api.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;

/**
 * This util is used to trim string.
 */
@Service
@Slf4j
public class TrimUtil {
    /**
     * This method is used to trim all string fields of an object.
     *
     * @param object Object
     * @return Object
     */
    public Object trimFields(Object object) {
        try {
            for (Field field : object.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                var value = field.get(object);

                if (value instanceof String) {
                    field.set(object, ((String) value).trim());
                }
            }

        } catch (Exception e) {
            log.error("Cannot trim fields of object: {}", object, e);

            return object;
        }

        return object;
    }
}
