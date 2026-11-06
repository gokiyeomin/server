package kr.co.ratiko.weiter.common.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final WebRequest request = new ServletWebRequest(new MockHttpServletRequest("GET", "/rooms/7"));

    @Test
    void businessExceptionReturnsItsClientError() {
        BusinessException exception = new BusinessException(ClientError.ROOM_NOT_FOUND, "room 7 does not exist");

        ResponseEntity<ErrorResponse> response = handler.handleBusinessException(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo(new ErrorResponse(302, "방을 찾을 수 없습니다."));
    }

    @Test
    void businessExceptionDoesNotExposeItsLogMessage() {
        BusinessException exception = new BusinessException(ClientError.ROOM_NOT_FOUND, "room 7 does not exist");

        ResponseEntity<ErrorResponse> response = handler.handleBusinessException(exception, request);

        assertThat(response.getBody().message()).doesNotContain("room 7");
    }

    @Test
    void unexpectedFailureIsInternalErrorWithoutDetails() throws Exception {
        ResponseEntity<ErrorResponse> response = handler.handleUnexpected(new IllegalStateException("boom"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isEqualTo(new ErrorResponse(901, "요청을 처리하는 중 오류가 발생했습니다."));
    }

    @Test
    void accessDeniedIsRethrownForTheSecurityFilterChain() {
        AccessDeniedException exception = new AccessDeniedException("denied");

        assertThatThrownBy(() -> handler.handleUnexpected(exception, request)).isSameAs(exception);
    }

    @Test
    void authenticationFailureIsRethrownForTheSecurityFilterChain() {
        BadCredentialsException exception = new BadCredentialsException("bad");

        assertThatThrownBy(() -> handler.handleUnexpected(exception, request)).isSameAs(exception);
    }

    @Test
    void clientErrorStatusFromTheFrameworkBecomesInvalidRequest() {
        ResponseEntity<Object> response = handler.handleExceptionInternal(
                new IllegalArgumentException("bad"), null, new HttpHeaders(), HttpStatus.METHOD_NOT_ALLOWED, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
        assertThat(response.getBody()).isEqualTo(new ErrorResponse(101, "요청 값이 올바르지 않습니다."));
    }

    @Test
    void serverErrorStatusFromTheFrameworkBecomesInternalError() {
        ResponseEntity<Object> response = handler.handleExceptionInternal(
                new IllegalStateException("boom"), null, new HttpHeaders(), HttpStatus.SERVICE_UNAVAILABLE, request);

        assertThat(response.getBody()).isEqualTo(new ErrorResponse(901, "요청을 처리하는 중 오류가 발생했습니다."));
    }

    @Test
    void validationFailureListsEveryFieldError() throws Exception {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "payload");
        bindingResult.addError(new FieldError("payload", "name", "must not be blank"));
        bindingResult.addError(new FieldError("payload", "size", null, false, null, null, null));

        ResponseEntity<Object> response = handler.handleExceptionInternal(
                new MethodArgumentNotValidException(parameter(), bindingResult),
                null, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);

        assertThat(response.getBody()).isEqualTo(ValidationErrorResponse.from(ClientError.INVALID_REQUEST, java.util.List.of(
                new ValidationErrorResponse.FieldError("name", "must not be blank"),
                new ValidationErrorResponse.FieldError("size", "올바르지 않은 값입니다."))));
    }

    private MethodParameter parameter() throws NoSuchMethodException {
        Method method = GlobalExceptionHandlerTest.class.getDeclaredMethod("endpoint", Object.class);
        return new MethodParameter(method, 0);
    }

    @SuppressWarnings("unused")
    private void endpoint(Object payload) {
    }
}
