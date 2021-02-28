package eu.xenit.restrequests.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import eu.xenit.restrequests.api.reactive.ReactiveRestClient;
import eu.xenit.restrequests.api.reactive.ReactiveRestBuilder;
import eu.xenit.restrequests.api.exceptions.HttpStatusException;
import eu.xenit.restrequests.api.exceptions.HttpTimeoutException;
import eu.xenit.restrequests.api.http.MediaType;
import eu.xenit.restrequests.test.app.TestApp;
import java.net.URI;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactory;
import org.assertj.core.api.ThrowableAssertAlternative;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import reactor.blockhound.BlockHound;
import reactor.blockhound.BlockingOperationError;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest(
        classes = TestApp.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.mvc.dispatch-trace-request=true")
public abstract class HttpIntegrationTestSuite implements EnableBlockHound {

    protected abstract ReactiveRestBuilder<?> builder();

    protected ReactiveRestClient client() {
        return builder().build();
    }

    static {
        BlockHound.install();
    }

    /**
     * The port the spring boot application is running on.
     */
    @LocalServerPort
    private int port;

    private URI getBaseUri() {
        return URI.create("http://localhost:" + port);
    }

    @Test
    void testGetString_success() {
        var future = client()
                .get(getBaseUri().resolve("/test-get"))
                .execute(String.class);

        var mono = Mono.fromFuture(future);

        StepVerifier.create(mono).assertNext(response -> {
                    assertThat(response.statusCode()).isEqualTo(200);
                    assertThat(response.body()).isEqualTo("Don't Panic.");
                });
    }


    @Test
    void testGetBytes_success() {
        var response = client()
                .get(getBaseUri().resolve("/test-get"))
                .execute(byte[].class)
                .join();

        Charset charSet = response.headers().getContentTypeCharsetOrDefault();
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.headers().getContentType()).
                isPresent().hasValueSatisfying(MediaType.TEXT_PLAIN::includes);
        assertThat(response.body()).isEqualTo("Don't Panic.".getBytes(charSet));
    }

    @Test
    void testPost_success() {
        var response = client()
                .post(getBaseUri().resolve("/test-post"))
                .body("Time is an illusion. Lunchtime doubly so.")
                .execute(String.class)
                .join();

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isEqualTo("Time is an illusion. Lunchtime doubly so.");
    }

    @Test
    void testPut_success() {
        var response = client()
                .put(getBaseUri().resolve("/test-put"))
                .body("Time is an illusion. Lunchtime doubly so.")
                .execute(String.class)
                .join();

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isEqualTo("Time is an illusion. Lunchtime doubly so.");
    }

    @Test
    void testDelete_success() {
        var response = client()
                .delete(getBaseUri().resolve("/test-delete"))
                .execute(String.class)
                .join();

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isEqualTo("So long, and thanks for all the fish.");
    }

    @Test
    void validAcceptHeader_success() {
        var response = client()
                .get(getBaseUri().resolve("/test-accept"))
                .accept("text/plain")
                .execute(String.class)
                .join();

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isEqualTo("Beware the Leopard.");
    }

    @Test
    void invalidAcceptHeader_shouldReturnStatus406() {
        var future = client()
                .get(getBaseUri().resolve("/test-accept"))
                .accept("foo/bar")
                .execute(String.class);

        assertThat(future).failsWithin(Duration.ofSeconds(1))
                .withThrowableOfType(ExecutionException.class)
                .withCauseInstanceOf(HttpStatusException.class)
                .havingCause()
                .asInstanceOf(new InstanceOfAssertFactory<>(HttpStatusException.class, ThrowableAssertAlternative::new))
                .satisfies(httpEx -> {
                    assertThat(httpEx.statusCode()).isEqualTo(406);
                });
    }

    @Test
    void readTimeout_throwsHttpTimeoutException() {
        var future = client()
                .get(getBaseUri().resolve("/timeout?sleep=1000"))
                .timeout(Duration.ofMillis(100))
                .execute(String.class);


        assertThat(future)
                .failsWithin(Duration.ofMillis(200))
                .withThrowableOfType(ExecutionException.class)
                .withCauseExactlyInstanceOf(HttpTimeoutException.class);
    }

    @Test
    void testRedirectDefault_isFollowed() {
        var response = client()
                .get(getBaseUri().resolve("/test-redirect"))
                .execute(String.class)
                .join();

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isEqualTo("Don't Panic.");
    }

    @Test
    void testRedirectEnabled_isNotFollowed() {
        var response = builder().followRedirects(true).build()
                .get(getBaseUri().resolve("/test-redirect"))
                .execute(String.class)
                .join();

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isEqualTo("Don't Panic.");
    }

    @Test
    void testRedirectDisabled_isNotFollowed() {
        var response = builder().followRedirects(false).build()
                .get(getBaseUri().resolve("/test-redirect"))
                .execute(String.class)
                .join();

        assertThat(response.statusCode()).isEqualTo(302);
        assertThat(response.headers().getLocation()).isPresent();

    }

    static class HeadersType extends HashMap<String, List<String>> { }
    @Test
    void testHeaders() {
        var response = builder().followRedirects(false).build()
                .get(getBaseUri().resolve("/test-headers"))
                .header("foo", "bar", "baz")
                .execute(HeadersType.class)
                .join();

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).containsEntry("foo", List.of("bar", "baz"));
    }

//    def 'When a request includes query parameters, the server receives them correctly'() {
//        setup:
//        def request = new HttpRequest()
//                .setUri("${baseUrl}/testParams")
//                .addQueryParameter('foo', 'bar')
//                .addQueryParameter('key', 'value')
//
//        when:
//        def response = httpClientFactory.createHttpClient().get(request)
//
//        then:
//        response.getEntity(Map) == [foo: ['bar'], key: ['value']]
//    }
//
//    def 'When a request includes query parameters with multiple values, the server receives them correctly'() {
//        setup:
//        def request = new HttpRequest()
//                .setUri("${baseUrl}/testParams")
//                .addQueryParameter('foo', 'bar')
//                .addQueryParameter('foo', 'baz')
//                .addQueryParameter('hi', 'there')
//
//        when:
//        def response = httpClientFactory.createHttpClient().get(request)
//
//        then:
//        response.getEntity(Map) == ['foo': ['bar', 'baz'], 'hi': ['there']]
//    }


    @Test
    void onHttpStatus500_futureCompletesExceptionally_withHttpStatusException() {
        var future = client().get(getBaseUri().resolve("/test-status/500")).execute();

        assertThat(future)
                .failsWithin(Duration.ofSeconds(2))
                .withThrowableOfType(ExecutionException.class)
                .withCauseInstanceOf(HttpStatusException.class);

//                .withThrowableOfType(HttpStatusException.class)
//                .satisfies(status -> assertThat(status).hasFieldOrPropertyWithValue("statusCode", 500));
    }

//    def 'When a response has a status of 500, an HttpInternalServerErrorException is thrown'() {
//        when:
//        httpClientFactory.createHttpClient().get(new HttpRequest().setUri("${baseUrl}/test500").addFilter(new HttpStatusExceptionFilter()))
//
//        then:
//        thrown HttpInternalServerErrorException
//    }

}
