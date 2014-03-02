package net.cubespace.lib.Util;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class StringUtils {
    public static String join(String[] strings, String delimiter) {
        StringBuilder builder = new StringBuilder();
        Integer length = strings.length;

        for(Integer i = 0; i < length; i++) {
            builder.append(strings[i]);

            if(i == length - 1) {
                break;
            }

            builder.append(delimiter);
        }

        return builder.toString();
    }
}
