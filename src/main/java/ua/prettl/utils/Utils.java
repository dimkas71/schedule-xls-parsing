package ua.prettl.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static boolean matchesNumber(String aNumber) {

        final String regex = "\\d{2}\\/\\d{4}$";

        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(aNumber);

        return matcher.find();
    }
}
