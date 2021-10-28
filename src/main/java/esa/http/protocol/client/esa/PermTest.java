/*
 * Copyright 2020 OPPO ESA Stack Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package esa.http.protocol.client.esa;

import esa.httpclient.core.HttpClient;
import esa.httpclient.core.HttpRequest;

/**
 * PermTest
 *
 * @author duanwei
 */
public class PermTest {

    private static final HttpClient CLIENT = HttpClient.ofDefault();

    public static void main(String[] args) throws Exception {

        String url = "http://10.176.101.69:9997/12345";

        for (int i = 0; i < 200; i++) {
            new Thread(() -> {
                while (true) {
                    try {
                        CLIENT.async(HttpRequest.get(url).build()).get();
                    } catch (Throwable ex) {
                        ex.printStackTrace();
                    }
                }

            }).start();
        }


        System.in.read();
    }

}
