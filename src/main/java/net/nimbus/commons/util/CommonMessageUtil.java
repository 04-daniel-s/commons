package net.nimbus.commons.util;

import lombok.experimental.UtilityClass;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@UtilityClass
public class CommonMessageUtil {

    public String formatString(String string) {
        return Character.toUpperCase(string.charAt(0)) + string.substring(1).toLowerCase();
    }

    public String formatDate(Date date) {
        return new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.GERMANY).format(date);
    }
}
