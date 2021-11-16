package io.esastack.test.stability.util;

import java.util.concurrent.ThreadLocalRandom;

public final class BodyUtil {

    private BodyUtil() {

    }

    public static final int DON_T_JUDGE_BODY = -1;
    public static final int EXPECTED_0MB_BYTE_LENGTH = 0;
    public static final int EXPECTED_1MB_BYTE_LENGTH = 1024 * 1024;
    public static final byte[] EXPECTED_1MB_BODY = new byte[EXPECTED_1MB_BYTE_LENGTH];
    public static final int K = 1024;
    public static final byte[][] CHUNK_BODY = new byte[K][K];

    static {
        ThreadLocalRandom.current().nextBytes(EXPECTED_1MB_BODY);
        for (int i = 0; i < K; i++) {
            ThreadLocalRandom.current().nextBytes(CHUNK_BODY[i]);
        }
    }
}
