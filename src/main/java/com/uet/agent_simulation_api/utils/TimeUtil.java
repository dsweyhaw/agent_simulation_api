package com.uet.agent_simulation_api.utils;

import com.uet.agent_simulation_api.constant.TimeConst;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Time utility class.
 */
@Service
public class TimeUtil {
    @Value("${app.timezone}")
    private String timezone;

    /**
     * This method is used to get time zone.
     *
     * @return String - Time zone.
     */
    private String getTimezone() {
        return timezone.equalsIgnoreCase(TimeConst.UTC_TIME_ZONE) ? TimeConst.UTC_TIME_ZONE : TimeConst.DEFAULT_TIME_ZONE;
    }

    /**
     * This method is used to get current time in milliseconds.
     *
     * @return long - Current time in milliseconds.
     */
    public long getCurrentTime() {
        return System.currentTimeMillis();
    }

    /**
     * This method is used to get current time in nanoseconds.
     *
     * @return long - Current time in nanoseconds.
     */
    public long getCurrentTimeNano() {
        return System.nanoTime();
    }

    /**
     * This method is used to get current time string in default format and default time zone.
     *
     * @return String - Current time in default format and default time zone.
     */
    public String getCurrentTimeString() {
        return getCurrentTimeString(TimeConst.DEFAULT_DATE_TIME_FORMAT, getTimezone());
    }

    /**
     * This method is used to get current time string in specific format.
     *
     * @param format String - specific format
     *
     * @return String - Current time in specific format.
     */
    public String getCurrentTimeString(String format) {
        return getCurrentTimeString(format, getTimezone());
    }

    /**
     * This method is used to get current time string in specific format and specific time zone.
     *
     * @param format String - specific format
     * @param zoneId String - specific time zone
     *
     * @return String - Current time in specific format and specific time zone.
     */
    public String getCurrentTimeString(String format, String zoneId) {
        final ZonedDateTime now = Instant.now().atZone(ZoneId.of(zoneId));
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);

        return now.format(formatter);
    }
}
