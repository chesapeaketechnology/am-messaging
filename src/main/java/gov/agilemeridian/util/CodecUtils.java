package gov.agilemeridian.util;

import com.google.common.io.BaseEncoding;

public enum CodecUtils {
    ;
    public static String base16Encode(byte[] bytes) {
        return BaseEncoding.base16().encode(bytes);
    }

    public static byte[] base16Decode(String hexString) {
        return BaseEncoding.base16()
                .decode(hexString.toUpperCase());
    }
}
