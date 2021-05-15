package http.echo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@RestController
@RequestMapping("/**")
public class EchoController {
    private static final Logger LOGGER = LoggerFactory.getLogger(EchoController.class);

    @RequestMapping(method = {
        RequestMethod.GET,
        RequestMethod.DELETE,
        RequestMethod.OPTIONS,
        RequestMethod.PATCH,
        RequestMethod.POST,
        RequestMethod.PUT,
        RequestMethod.HEAD
    })
    public ResponseEntity<String> echo(
        @RequestParam(required = false) String echo,
        @RequestParam(required = false) Integer status,
        @RequestParam(required = false) Long time,
        HttpServletRequest request
    ) {
        LOGGER.info("method: " + request.getMethod() +", path: " + request.getRequestURI() + ", queryString: " + request.getQueryString());

        if (time != null) {
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        Integer statusCode = Objects.requireNonNullElse(status, HttpStatus.OK.value());
        String body = Objects.requireNonNullElse(echo, "echo");
        return ResponseEntity.status(statusCode).body(body);
    }
}
