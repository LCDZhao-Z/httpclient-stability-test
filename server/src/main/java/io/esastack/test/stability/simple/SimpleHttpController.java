package io.esastack.test.stability.simple;

import esa.httpserver.core.AsyncRequest;
import esa.httpserver.core.AsyncResponse;
import esa.restlight.spring.shaded.org.springframework.web.bind.annotation.GetMapping;
import esa.restlight.spring.shaded.org.springframework.web.bind.annotation.PostMapping;
import esa.restlight.spring.shaded.org.springframework.web.bind.annotation.RequestMapping;
import esa.restlight.spring.shaded.org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.stereotype.Controller;

import java.util.concurrent.ThreadLocalRandom;

@Controller
@RequestMapping("/simple-http")
public class SimpleHttpController {

    private static final int EXPECTED_0MB_BYTE_LENGTH = 0;
    private static final int EXPECTED_1MB_BYTE_LENGTH = 1024 * 1024;
    private static final byte[] EXPECTED_1MB_BODY = new byte[EXPECTED_1MB_BYTE_LENGTH];

    static {
        ThreadLocalRandom.current().nextBytes(EXPECTED_1MB_BODY);
    }

    @PostMapping("/1mb-body")
    @ResponseBody
    public void responseWith1MBBody(AsyncRequest request, AsyncResponse response) {
        int requestBodyLength = request.body().length;
        if (requestBodyLength != EXPECTED_1MB_BYTE_LENGTH) {
            System.err.println("Unexpected body size: " + requestBodyLength);
        }
        response.sendResult(200, EXPECTED_1MB_BODY);
    }

    @GetMapping("/nobody")
    @ResponseBody
    public void responseWithNoBody(AsyncRequest request, AsyncResponse response) {
        int requestBodyLength = request.body().length;
        if (requestBodyLength != EXPECTED_0MB_BYTE_LENGTH) {
            System.err.println("Unexpected body size: " + requestBodyLength);
        }
        response.sendResult(200);
    }
}
