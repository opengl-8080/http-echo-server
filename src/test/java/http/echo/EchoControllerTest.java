package http.echo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EchoControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate rest;

    private String urlPrefix;

    @BeforeEach
    void setUp() {
        urlPrefix = "http://localhost:" + port;

        // for PATCH Method
        // see https://stackoverflow.com/questions/29447382/resttemplate-patch-request
        rest.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    }

    @ParameterizedTest
    @EnumSource(value = HttpMethod.class, mode = EnumSource.Mode.EXCLUDE, names = {"TRACE", "HEAD"})
    void bodyEqualsToEchoInAnyMethodsAndPath(HttpMethod method) {
        ResponseEntity<String> response = sendRequest(method, "/foo/bar");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("echo");
    }

    @Test
    void responseBodyOfHeadMethodWillBeEmpty() {
        ResponseEntity<String> response = sendRequest(HttpMethod.HEAD, "/foo");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.hasBody()).isFalse();
    }

    @ParameterizedTest
    @EnumSource(value = HttpMethod.class, mode = EnumSource.Mode.EXCLUDE, names = {"TRACE", "HEAD"})
    void responseBodyEqualsToTheEchoParameter(HttpMethod method) {
        ResponseEntity<String> response = sendRequest(method, "/foo/bar", queryParams("echo", method.name()));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(method.name());
    }

    @ParameterizedTest
    @EnumSource(value = HttpMethod.class, mode = EnumSource.Mode.EXCLUDE, names = {"TRACE"})
    void statusCodeEqualsToTheStatusParameter(HttpMethod method) {
        ResponseEntity<String> response = sendRequest(method, "/foo/bar", queryParams("status", "404"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @ParameterizedTest
    @EnumSource(value = HttpMethod.class, mode = EnumSource.Mode.EXCLUDE, names = {"TRACE"})
    void waitResponseWithTheTimeParameter(HttpMethod method) {
        long begin = System.currentTimeMillis();
        sendRequest(method, "/foo/bar", queryParams("time", "150"));
        long time = System.currentTimeMillis() - begin;

        assertThat(time).isGreaterThan(150);
    }

    @ExtendWith(OutputCaptureExtension.class)
    @ParameterizedTest
    @EnumSource(value = HttpMethod.class, mode = EnumSource.Mode.EXCLUDE, names = {"TRACE"})
    void loggingRequestInformation(HttpMethod method, CapturedOutput output) {
        sendRequest(method, "/fizz/buzz", queryParams("foo", "FOO", "bar", "BAR"));

        assertThat(output)
            .contains("method: " + method.name())
            .contains("path: /fizz/buzz")
            .contains("queryString: foo=FOO&bar=BAR");
    }

    private ResponseEntity<String> sendRequest(HttpMethod method, String path) {
        return sendRequest(method, path, Map.of());
    }

    private ResponseEntity<String> sendRequest(HttpMethod method, String path, Map<String, String> queryParameters) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromHttpUrl(urlPrefix)
                .path(path);

        queryParameters.forEach(uriBuilder::queryParam);

        return rest.exchange(uriBuilder.toUriString(), method, RequestEntity.EMPTY, String.class);
    }

    private Map<String, String> queryParams(String key, String value, String... params) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put(key, value);

        for (int i=0; i+1<params.length; i+=2) {
            map.put(params[i], params[i+1]);
        }

        return map;
    }
}