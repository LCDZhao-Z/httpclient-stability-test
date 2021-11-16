package io.esastack.test.stability.chunk;

import esa.httpserver.core.AsyncRequest;
import esa.httpserver.core.AsyncResponse;
import esa.restlight.spring.shaded.org.springframework.web.bind.annotation.PostMapping;
import esa.restlight.spring.shaded.org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

@Controller
@RequestMapping("/chunk-http")
public class ChunkHttpController {

    private static final int K = 1024;

    private static final byte[][] BODY = new byte[K][K];

    static {
        for (int i = 0; i < K; i++) {
            ThreadLocalRandom.current().nextBytes(BODY[i]);
        }
    }

    @PostMapping("/chunk")
    public void handleChunk(AsyncRequest request, AsyncResponse response) throws IOException {
        if (request.body().length != K * K) {
            System.err.println("Unexpected request body size: " + request.body().length);
        }

        response.setStatus(200);
        for (int i = 0; i < K; i++) {
            response.getOutputStream().write(BODY[i]);
            response.getOutputStream().flush();
        }
        response.getOutputStream().close();
    }

}
