package io.github.rocketk.jorm.util;

/**
 * @author pengyu
 */
public class StringUtils {

    public static String emptyWrap(String something) {
        return emptyWrap(something, "Unknown");
    }

    /**
     * 避免空指针异常，当输入字符串为空时，返回 defaultValue
     *
     * @param something    输入字符串
     * @param defaultValue 默认值
     * @return something or defaultValue
     */
    public static String emptyWrap(String something, String defaultValue) {
        if (something == null || something.isEmpty()) {
            return defaultValue;
        }
        return something;
    }

    /**
     * 截取关键字以前的字符串。
     *
     * @param content     原字符串
     * @param searchWords 搜索关键词，多个关键词时以最早出现（如果存在的话）的关键词所处的位置为截取位置
     * @return 截取后的新字符串，如果任何一个关键词都没有出现则返回原字符串
     */
    public static String subBeforeAny(String content, String... searchWords) {
        if (content == null || content.isEmpty()) {
            return "";
        }
        if (searchWords == null || searchWords.length == 0) {
            return content;
        }
        int index = -1;
        for (String searchWord : searchWords) {
            final int i = content.indexOf(searchWord);
            if (i >= 0 && (index < 0 || i < index)) {
                index = i;
            }
        }
        if (index < 0) {
            return content;
        }
        return content.substring(0, index);
    }

    /**
     * 去掉空字符（"WhiteChars"） ，包括换行符。
     * 本函数还会调用trim()来删除首尾两部分的空格
     *
     * @param content 原字符串
     * @return 去掉空字符之后的新字符串，空字符会替换为空格
     */
    public static String escapeWhiteChars(String content) {
        final int len = length(content);
        if (len == 0) {
            return "";
        }
        StringBuilder newContent = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            if (Character.isWhitespace(content.charAt(i))) {
                newContent.append(" ");
            } else {
                newContent.append(content.charAt(i));
            }
        }
        return newContent.toString().trim();
    }

    public static String escapeWhiteCharsUseRegex(String content) {
        final int len = length(content);
        if (len == 0) {
            return "";
        }
        return content.replaceAll("\\s+", " ").trim();
    }

    private static int length(String content) {
        return content == null ? 0 : content.length();
    }
}
