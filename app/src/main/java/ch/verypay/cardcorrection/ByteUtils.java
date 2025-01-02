package ch.verypay.cardcorrection;


import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

public class ByteUtils {
    private static final char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static byte[] fromHexString(String hexSpaceString) {
        hexSpaceString = hexSpaceString.replace(" ", "");
        int len = hexSpaceString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexSpaceString.charAt(i), 16) << 4)
                    + Character.digit(hexSpaceString.charAt(i + 1), 16));
        }
        return data;
    }

    public static String bytesToHexString(byte[] bytes) {

        return formatByte(bytes, "%02x ", false);
    }

    private static String formatByte(byte[] pByte, String pFormat, boolean pTruncate) {
        StringBuffer sb = new StringBuffer();
        if (pByte == null) {
            sb.append("");
        } else {
            boolean t = false;
            byte[] arr$ = pByte;
            int len$ = pByte.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                byte b = arr$[i$];
                if (b != 0 || !pTruncate || t) {
                    t = true;
                    sb.append(String.format(pFormat, new Object[]{Integer.valueOf(b & 255)}));
                }
            }
        }

        return sb.toString().toUpperCase(Locale.getDefault()).trim();
    }

    public static String bytesToHexStringNoSpace(byte[] bytes) {
        return formatByte(bytes, "%02x", false);
    }

    public static int byteArrayToInt(byte[] byteArray) {
        return byteArrayToInt(byteArray, 0, byteArray.length);
    }

    public static int byteToInt(byte byteValue) {
        return byteValue & 255;
    }

    public static short byteToShort(byte byteValue) {
        return (short) (byteValue & 255);
    }

    public static int byteArrayToInt(byte[] byteArray, int startPos, int length) {
        if (byteArray == null) {
            throw new IllegalArgumentException("Parameter \'byteArray\' cannot be null");
        } else if (length > 0 && length <= 4) {
            if (startPos >= 0 && byteArray.length >= startPos + length) {
                int value = 0;

                for (int i = 0; i < length; ++i) {
                    value += (byteArray[startPos + i] & 255) << 8 * (length - i - 1);
                }

                return value;
            } else {
                throw new IllegalArgumentException("Length or startPos not valid");
            }
        } else {
            throw new IllegalArgumentException("Length must be between 1 and 4. Length = " + length);
        }
    }

    public static byte[] toByteArray(int value) {
        return new byte[]{(byte) (value >> 24), (byte) (value >> 16), (byte) (value >> 8), (byte) (value)};
    }

    public static short getShort(byte[] bArray, short offset) {
        return (short) (((short) bArray[offset] << 8) + ((short) bArray[offset + 1] & 255));
    }

    public static boolean matchBitByBitIndex(byte byteVal, int bitIndex) {
        if (bitIndex < 0 || bitIndex > 31) {
            throw new IllegalArgumentException("Bit index must be between 0 and 31: " + bitIndex);
        }
        return (byteVal & 1 << bitIndex) != 0;
    }

    public static byte[] intToBytes(int intValue, int byteLength) {
        byte[] bytes = new byte[byteLength];
        for (int i = 0; i < byteLength; i++) {
            bytes[i] = (byte) (intValue >> ((byteLength - i - 1) * 8));
        }
        return bytes;
    }

    public static byte[] charArrayToBytes(char[] chars) {
        byte[] byteArray = new byte[chars.length];
        for (int i = 0; i < chars.length; i++) {
            byteArray[i] = (byte) chars[i];
        }
        return byteArray;
    }

    public static byte[] concatArrays(byte[] first, byte[]... rest) {
        int totalLength = first.length;
        for (byte[] array : rest) {
            totalLength += array.length;
        }
        byte[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (byte[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

    public static byte[] getRandom(int size) {
        byte[] randomValue = new byte[size];
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            randomValue[i] = (byte) ((random.nextInt() % 200) & 0x0FF);
        }

        return randomValue;
    }

}
