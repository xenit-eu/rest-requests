/*
 * Copyright 2016-2018 the original author or authors.
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
package eu.xenit.restrequests.test.app;

import java.io.IOException;
import java.nio.charset.Charset;
import javax.servlet.http.HttpServletResponse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class TestApp {

    public static void main(String[] args) {
        SpringApplication.run(TestApp.class, args);
    }

    @GetMapping(value = "/test-get", produces = "text/plain")
    String testGet() {
        return "Don't Panic.";
    }

    @PostMapping(value = "/test-post", produces = "text/plain")
    String testPost(@RequestBody(required = false) String entity) {
        return entity;
    }

    @PutMapping(value = "/test-put", produces = "text/plain")
    String testPut(@RequestBody(required = false) String entity) {
        return entity;
    }

    @DeleteMapping(value = "/test-delete", produces = "text/plain")
    String testDelete() {
        return "So long, and thanks for all the fish.";
    }

    @RequestMapping(value = "/test-head", method = RequestMethod.HEAD)
    void testHead(HttpServletResponse response) {
        response.addHeader("answer", "42");
        response.addHeader("foo", "hi");
        response.addHeader("foo", "there");
        response.setStatus(200);
    }

    @RequestMapping(value = "/test-options", method = RequestMethod.OPTIONS)
    ResponseEntity<String> testOptions(@RequestBody(required = false) String entity) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET");
        headers.add("Allow", "POST");

        String response = entity != null ? entity : "No entity";

        return new ResponseEntity<String>(response, headers, HttpStatus.valueOf(200));
    }

    @GetMapping(value = "/test-accept")
    ResponseEntity<String> testAccept(@RequestHeader(value = "Accept", required = false) String accept) {
        String body;
        String contentType;
        int status = 200;

        switch (accept) {
            case MediaType.APPLICATION_JSON_VALUE:
                body = "{ \"foo\" : \"bar\" }";
                contentType = accept;
                break;

            case MediaType.TEXT_PLAIN_VALUE:
                body = "Beware the Leopard.";
                contentType = accept;
                break;

            case MediaType.TEXT_XML_VALUE:
            case MediaType.APPLICATION_XML_VALUE:
                body = "<foo>bar</foo>";
                contentType = accept;
                break;

            default:
                body = "I can't handle ${accept}";
                contentType = MediaType.TEXT_PLAIN_VALUE;
                status = 406;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", contentType);

        return new ResponseEntity<String>(body, headers, HttpStatus.valueOf(status));
    }

    @GetMapping(value = "/timeout", produces = "text/plain")
    String testReadTimeout(@RequestParam(name = "sleep", defaultValue = "5000") long sleepMillis) throws InterruptedException {
        Thread.sleep(sleepMillis);
        return "Slept for "+sleepMillis+" millis!";
    }

    @GetMapping(value = "/test-redirect")
    void testRedirect(HttpServletResponse response) throws IOException {
        response.sendRedirect("test-get");
    }

    @ResponseBody
    @GetMapping(value = "/test-headers", produces = "application/json")
    MultiValueMap<String, String> echoHeaders (@RequestHeader MultiValueMap<String, String> headers) {
        return headers;
    }

    @GetMapping(value = "/test-auth", produces = "text/plain")
    ResponseEntity<String> testAuth(@RequestHeader(value = "Authorization", required = false) String authorization) {
        if (authorization == null) {
            return new ResponseEntity<String>("Authentication required", HttpStatus.valueOf(401));
        } else {
            return new ResponseEntity<String>("welcome", HttpStatus.valueOf(200));
        }
    }

    @RequestMapping(value = "/test-accept-contenttype", produces = "text/plain")
    ResponseEntity<String> acceptContentType(
            @RequestHeader(value = "Accept") MediaType accept, @RequestBody String input) {
        Charset charset = accept.getCharset() != null ? accept.getCharset() : Charset.defaultCharset();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", accept.toString());

        return new ResponseEntity<>(new String(input.getBytes(), charset), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/echo")
    ResponseEntity<byte[]> echo(
            @RequestBody byte[] body,
            @RequestHeader(value = "Echo-Content-Type", required = false) MediaType contentType) {

        HttpHeaders headers = new HttpHeaders();
        if (contentType != null) {
            headers.add("Content-Type", contentType.toString());
        }

        return new ResponseEntity<>(body, headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/test-status/{statusCode}")
    ResponseEntity<String> testStatusCode(@PathVariable int statusCode) {
        return new ResponseEntity<>(HttpStatus.valueOf(statusCode));
    }
}
