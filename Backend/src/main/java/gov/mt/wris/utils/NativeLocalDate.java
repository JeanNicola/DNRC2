package gov.mt.wris.utils;

import java.sql.Timestamp;
import java.time.LocalDate;

public class NativeLocalDate {
    public static LocalDate cast(Object time) {
        if (time == null) return null;
        return ((Timestamp) time)
            .toLocalDateTime()
            .toLocalDate();
    }

    public static LocalDate convert(Timestamp time) {
        if (time == null) return null;
        return time
            .toLocalDateTime()
            .toLocalDate();
    }
}
