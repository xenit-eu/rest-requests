package eu.xenit.restrequests.test;

import eu.xenit.restrequests.api.ReactiveRestClient;
import eu.xenit.restrequests.api.ReactiveRestBuilder;
import eu.xenit.restrequests.api.exceptions.HttpTimeoutException;
import eu.xenit.restrequests.api.http.MediaType;
import eu.xenit.restrequests.test.app.TestApp;
import java.net.URI;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

@SpringBootTest(
        classes = TestApp.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.mvc.dispatch-trace-request=true")
public abstract class HttpIntegrationTestSuite {

    protected abstract ReactiveRestBuilder<?> builder();

    protected ReactiveRestClient client() {
        return builder().build();
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
        var response = client()
                .get(getBaseUri().resolve("/test-get"))
                .execute(String.class)
                .join();

        Assertions.assertThat(response.statusCode()).isEqualTo(200);
        Assertions.assertThat(response.body()).isEqualTo("Don't Panic.");
    }


    @Test
    void testGetBytes_success() {
        var response = client()
                .get(getBaseUri().resolve("/test-get"))
                .execute(byte[].class)
                .join();

        Charset charSet = response.headers().getContentTypeCharsetOrDefault();
        Assertions.assertThat(response.statusCode()).isEqualTo(200);
        Assertions.assertThat(response.headers().getContentType()).
                isPresent().hasValueSatisfying(MediaType.TEXT_PLAIN::includes);
        Assertions.assertThat(response.body()).isEqualTo("Don't Panic.".getBytes(charSet));
    }

    @Test
    void testPost_success() {
        var response = client()
                .post(getBaseUri().resolve("/test-post"))
                .body("Time is an illusion. Lunchtime doubly so.")
                .execute(String.class)
                .join();

        Assertions.assertThat(response.statusCode()).isEqualTo(200);
        Assertions.assertThat(response.body()).isEqualTo("Time is an illusion. Lunchtime doubly so.");
    }

    @Test
    void testPut_success() {
        var response = client()
                .put(getBaseUri().resolve("/test-put"))
                .body("Time is an illusion. Lunchtime doubly so.")
                .execute(String.class)
                .join();

        Assertions.assertThat(response.statusCode()).isEqualTo(200);
        Assertions.assertThat(response.body()).isEqualTo("Time is an illusion. Lunchtime doubly so.");
    }

    @Test
    void testDelete_success() {
        var response = client()
                .delete(getBaseUri().resolve("/test-delete"))
                .execute(String.class)
                .join();

        Assertions.assertThat(response.statusCode()).isEqualTo(200);
        Assertions.assertThat(response.body()).isEqualTo("So long, and thanks for all the fish.");
    }

    @Test
    void validAcceptHeader_success() {
        var response = client()
                .get(getBaseUri().resolve("/test-accept"))
                .accept("text/plain")
                .execute(String.class)
                .join();

        Assertions.assertThat(response.statusCode()).isEqualTo(200);
        Assertions.assertThat(response.body()).isEqualTo("Beware the Leopard.");
    }

    @Test
    void invalidAcceptHeader_shouldReturnStatus406() {
        var response = client()
                .get(getBaseUri().resolve("/test-accept"))
                .accept("foo/bar")
                .execute(String.class)
                .join();

        Assertions.assertThat(response.statusCode()).isEqualTo(406);
    }

    @Test
    void readTimeout_throwsHttpTimeoutException() {
        var future = client()
                .get(getBaseUri().resolve("/timeout?sleep=1000"))
                .timeout(Duration.ofMillis(100))
                .execute(String.class);


        Assertions.assertThat(future)
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

        Assertions.assertThat(response.statusCode()).isEqualTo(200);
        Assertions.assertThat(response.body()).isEqualTo("Don't Panic.");
    }

    @Test
    void testRedirectEnabled_isNotFollowed() {
        var response = builder().followRedirects(true).build()
                .get(getBaseUri().resolve("/test-redirect"))
                .execute(String.class)
                .join();

        Assertions.assertThat(response.statusCode()).isEqualTo(200);
        Assertions.assertThat(response.body()).isEqualTo("Don't Panic.");
    }

    @Test
    void testRedirectDisabled_isNotFollowed() {
        var response = builder().followRedirects(false).build()
                .get(getBaseUri().resolve("/test-redirect"))
                .execute(String.class)
                .join();

        Assertions.assertThat(response.statusCode()).isEqualTo(302);
        Assertions.assertThat(response.headers().getLocation()).isPresent();

    }

    static class HeadersType extends HashMap<String, List<String>> { }
    @Test
    void testHeaders() {
        var response = builder().followRedirects(false).build()
                .get(getBaseUri().resolve("/test-headers"))
                .header("foo", "bar", "baz")
                .execute(HeadersType.class)
                .join();

        Assertions.assertThat(response.statusCode()).isEqualTo(200);
        Assertions.assertThat(response.body()).containsEntry("foo", List.of("bar", "baz"));
    }

}
