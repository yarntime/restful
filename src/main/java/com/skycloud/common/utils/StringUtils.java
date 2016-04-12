/**
 * 
 * StringUtils exists in Apache Commons Lang, but rather than import the entire JAR to our system,
 * for now just implement the method needed
 *
 * @author: zhangwei
 * @date: Mar 24, 2016
 * @version: 1.0
 */
package com.skycloud.common.utils;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;


public class StringUtils {
    
    public static final String[] EMPTY_ARRAY = new String[0];
    
    private static final char[] hexChar =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static String longToString(long l) {

        return (Long) l != null ? Long.toString(l) : null;
    }

    public static String join(Iterable<? extends Object> iterable, String delim) {
        StringBuilder sb = new StringBuilder();
        if (iterable != null) {
            Iterator<? extends Object> iter = iterable.iterator();
            if (iter.hasNext()) {
                Object next = iter.next();
                sb.append(next.toString());
            }
            while (iter.hasNext()) {
                Object next = iter.next();
                sb.append(delim + next.toString());
            }
        }
        return sb.toString();
    }

    public static String join(final String delimiter, final Object... components) {
        return join(asList(components), delimiter);
    }

    /**
     * @param tags
     * @return List of tags
     */

    public static List<String> csvTagsToList(String tags) {
        List<String> tagsList = new ArrayList<String>();

        if (tags != null) {
            String[] tokens = tags.split(",");
            for (int i = 0; i < tokens.length; i++) {
                tagsList.add(tokens[i].trim());
            }
        }

        return tagsList;
    }

    /**
     * Converts a List of tags to a comma separated list
     * 
     * @param tags
     * @return String containing a comma separated list of tags
     */

    public static String listToCsvTags(List<String> tagsList) {
        String tags = "";
        if (tagsList.size() > 0) {
            for (int i = 0; i < tagsList.size(); i++) {
                tags += tagsList.get(i);
                if (i != tagsList.size() - 1) {
                    tags += ",";
                }
            }
        }

        return tags;
    }

    public static String getExceptionStackInfo(Throwable e) {
        StringBuffer sb = new StringBuffer();

        sb.append(e.toString()).append("\n");
        StackTraceElement[] elemnents = e.getStackTrace();
        for (StackTraceElement element : elemnents) {
            sb.append(element.getClassName()).append(".");
            sb.append(element.getMethodName()).append("(");
            sb.append(element.getFileName()).append(":");
            sb.append(element.getLineNumber()).append(")");
            sb.append("\n");
        }

        return sb.toString();
    }

    public static String unicodeEscape(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if ((c >> 7) > 0) {
                sb.append("\\u");
                sb.append(hexChar[(c >> 12) & 0xF]); // append the hex character
                // for the left-most 4-bits
                sb.append(hexChar[(c >> 8) & 0xF]); // hex for the second group
                // of 4-bits from the left
                sb.append(hexChar[(c >> 4) & 0xF]); // hex for the third group
                sb.append(hexChar[c & 0xF]); // hex for the last group, e.g.,
                // the right most 4-bits
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String getMaskedPasswordForDisplay(String password) {
        if (password == null || password.isEmpty())
            return "*";

        StringBuffer sb = new StringBuffer();
        sb.append(password.charAt(0));
        for (int i = 1; i < password.length(); i++)
            sb.append("*");

        return sb.toString();
    }

    // removes a password request param and it's value
    private static final Pattern REGEX_PASSWORD_QUERYSTRING =
            Pattern.compile("&?password=.*?(?=[&'\"])");

    // removes a password property from a response json object
    private static final Pattern REGEX_PASSWORD_JSON = Pattern.compile("\"password\":\".*?\",?");

    // Responsible for stripping sensitive content from request and response
    // strings
    public static String cleanString(String stringToClean) {
        String cleanResult = "";
        cleanResult = REGEX_PASSWORD_QUERYSTRING.matcher(stringToClean).replaceAll("");
        cleanResult = REGEX_PASSWORD_JSON.matcher(cleanResult).replaceAll("");
        return cleanResult;
    }

    public static int formatForOutput(String text, int start, int columns, char separator) {
        if (start >= text.length()) {
            return -1;
        }

        int end = start + columns;
        if (end > text.length()) {
            end = text.length();
        }
        String searchable = text.substring(start, end);
        int found = searchable.lastIndexOf(separator);
        return found > 0 ? found : end - start;
    }

    public static String escapeSql(String s) {
        return s.replace("\\", "\\\\\\").replace("%", "\\%").replace("_", "\\_");
    }
    
    public static String[] splitStringToArray(final CharSequence s, final char c) {
        if (s == null || s.length() == 0) {
            return StringUtils.EMPTY_ARRAY;
        }
        int count = 1;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == c) {
                count++;
            }
        }
        final String[] result = new String[count];
        final StringBuilder builder = new StringBuilder();
        int res = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == c) {
                if (builder.length() > 0) {
                    result[res++] = builder.toString();
                    builder.setLength(0);
                }

            } else {
                builder.append(s.charAt(i));
            }
        }
        if (builder.length() > 0) {
            result[res++] = builder.toString();
        }
        if (res != count) {
            // we have empty strings, copy over to a new array
            String[] result1 = new String[res];
            System.arraycopy(result, 0, result1, 0, res);
            return result1;
        }
        return result;
    }
}
