package it.eng.sil.myaccount.utils;

public class LocalPrefResolver {

    private static final String LOCAL_PREFIX = "local.prefix";

    public static String getCustomizedFile(String file) {
        String localPrefix = System.getProperty(LOCAL_PREFIX);

        if (localPrefix == null || "".equals(localPrefix)) {
            return file;
        }
        int lastDot = file.lastIndexOf(".");

        return String.format(
                "%s_%s.%s",
                file.substring(0, lastDot),
                localPrefix,
                file.substring(lastDot + 1)
        );

    }
}
