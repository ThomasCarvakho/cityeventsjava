import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    public static LocalDateTime parseIso(String s) {
        return LocalDateTime.parse(s, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public static String formatIso(LocalDateTime dt) {
        return dt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public static boolean isNowBetween(LocalDateTime start, LocalDateTime end) {
        LocalDateTime now = LocalDateTime.now();
        return (now.isEqual(start) || now.isAfter(start)) && now.isBefore(end);
    }
}
