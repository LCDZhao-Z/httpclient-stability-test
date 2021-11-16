package io.esastack.test.stability.chunk;

import esa.httpserver.core.AsyncRequest;
import esa.httpserver.core.AsyncResponse;
import esa.restlight.spring.shaded.org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;

import java.util.concurrent.ThreadLocalRandom;

@Controller
@RequestMapping("/chunk-http")
public class ChunkHttpController {

    private static final int MB = 1024 * 1024;

    private static final byte[] BODY = new byte[MB];

    static {
        ThreadLocalRandom.current().nextBytes(BODY);
    }

    @RequestMapping("/chunk")
    public void handleChunk(AsyncRequest request, AsyncResponse response) {
        if (request.body().length != MB) {
            System.err.println("Unexpected request body size: " + request.body().length);
        }

        response.sendResult(200, BODY);
    }

}
