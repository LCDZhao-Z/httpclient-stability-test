package io.esastack.test.stability.util;

public final class Constants {

    private Constants() {

    }

    public static final class Type {
        public static final String HTTP = "HTTP";
        public static final String REST = "REST";
    }

    public static final class Command {
        public static final String H1_POST_1MB = "H1_POST_1MB";
        public static final String H2_POST_1MB = "H2_POST_1MB";
        public static final String H1_GET_NO_BODY = "H1_GET_NO_BODY";
        public static final String H2_GET_NO_BODY = "H2_GET_NO_BODY";
        public static final String H1_CHUNK = "H1_CHUNK";
        public static final String H2_CHUNK = "H2_CHUNK";
    }

}
