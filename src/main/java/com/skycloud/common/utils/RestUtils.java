/**
 *  
 * Function Description
 *
 * @author: zhangwei
 * @date:  Apr 12, 2016
 * @version: 1.0
 */
package com.skycloud.common.utils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class RestUtils {

    public static final PathTrie.Decoder REST_DECODER = new PathTrie.Decoder() {
        @Override
        public String decode(String value) {
            return RestUtils.decodeComponent(value);
        }
    };
    
    public static String decodeComponent(final String s) {
        return decodeComponent(s, StandardCharsets.UTF_8);
    }
    
    public static String decodeComponent(final String s, final Charset charset) {
        if (s == null) {
            return "";
        }
        final int size = s.length();
        if (!decodingNeeded(s, size)) {
            return s;
        }
        final byte[] buf = new byte[size];
        int pos = decode(s, size, buf);
        return new String(buf, 0, pos, charset);
    }
    
    @SuppressWarnings("fallthrough")
    private static boolean decodingNeeded(String s, int size) {
        boolean decodingNeeded = false;
        for (int i = 0; i < size; i++) {
            final char c = s.charAt(i);
            switch (c) {
                case '%':
                    i++;  // We can skip at least one char, e.g. `%%'.
                    // Fall through.
                case '+':
                    decodingNeeded = true;
                    break;
            }
        }
        return decodingNeeded;
    }

    @SuppressWarnings("fallthrough")
    private static int decode(String s, int size, byte[] buf) {
        int pos = 0;  // position in `buf'.
        for (int i = 0; i < size; i++) {
            char c = s.charAt(i);
            switch (c) {
                case '+':
                    buf[pos++] = ' ';  // "+" -> " "
                    break;
                case '%':
                    if (i == size - 1) {
                        throw new IllegalArgumentException("unterminated escape sequence at end of string: " + s);
                    }
                    c = s.charAt(++i);
                    if (c == '%') {
                        buf[pos++] = '%';  // "%%" -> "%"
                        break;
                    } else if (i == size - 1) {
                        throw new IllegalArgumentException("partial escape sequence at end of string: " + s);
                    }
                    c = decodeHexNibble(c);
                    final char c2 = decodeHexNibble(s.charAt(++i));
                    if (c == Character.MAX_VALUE || c2 == Character.MAX_VALUE) {
                        throw new IllegalArgumentException(
                            "invalid escape sequence `%" + s.charAt(i - 1)
                                + s.charAt(i) + "' at index " + (i - 2)
                                + " of: " + s);
                    }
                    c = (char) (c * 16 + c2);
                    // Fall through.
                default:
                    buf[pos++] = (byte) c;
                    break;
            }
        }
        return pos;
    }
    
    private static char decodeHexNibble(final char c) {
        if ('0' <= c && c <= '9') {
            return (char) (c - '0');
        } else if ('a' <= c && c <= 'f') {
            return (char) (c - 'a' + 10);
        } else if ('A' <= c && c <= 'F') {
            return (char) (c - 'A' + 10);
        } else {
            return Character.MAX_VALUE;
        }
    }
}
