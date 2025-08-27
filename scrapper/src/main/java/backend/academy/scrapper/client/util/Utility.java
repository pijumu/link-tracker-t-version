package backend.academy.scrapper.client.util;

public class Utility {
    private Utility() {}

    public static String first200(String text) {
        if (text == null) return "";
        return text.length() <= 200 ? text : text.substring(0, 200);
    }
}
