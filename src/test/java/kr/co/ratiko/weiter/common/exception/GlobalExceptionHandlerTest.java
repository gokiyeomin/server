package kr.co.ratiko.weiter.common.exception;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new FailingController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void businessExceptionReturnsItsClientError() throws Exception {
        mockMvc.perform(get("/fail/business"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(302))
                .andExpect(jsonPath("$.message").value("방을 찾을 수 없습니다."));
    }

    @Test
    void businessExceptionDoesNotExposeItsLogMessage() throws Exception {
        mockMvc.perform(get("/fail/business"))
                .andExpect(content().string(not(containsString("room 7"))));
    }

    @Test
    void invalidBodyListsFieldErrors() throws Exception {
        mockMvc.perform(post("/fail/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(101))
                .andExpect(jsonPath("$.errors[0].field").value("name"));
    }

    @Test
    void springClientErrorKeepsItsStatusWithInvalidRequestCode() throws Exception {
        mockMvc.perform(post("/fail/business"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.errorCode").value(101));
    }

    @Test
    void typeMismatchIsInvalidRequest() throws Exception {
        mockMvc.perform(get("/fail/number/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(101));
    }

    @Test
    void unexpectedFailureIsInternalErrorWithoutDetails() throws Exception {
        mockMvc.perform(get("/fail/unknown"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorCode").value(901))
                .andExpect(content().string(not(containsString("boom"))));
    }

    @Test
    void accessDeniedPropagatesToSecurityFilterChain() {
        assertThatThrownBy(() -> mockMvc.perform(get("/fail/denied")))
                .hasRootCauseInstanceOf(AccessDeniedException.class);
    }

    @RestController
    static class FailingController {

        @GetMapping("/fail/business")
        String business() {
            throw new BusinessException(ClientError.ROOM_NOT_FOUND, "room 7 does not exist");
        }

        @PostMapping("/fail/validate")
        String validate(@Valid @RequestBody Payload payload) {
            return payload.name();
        }

        @GetMapping("/fail/number/{value}")
        String number(@PathVariable int value) {
            return String.valueOf(value);
        }

        @GetMapping("/fail/unknown")
        String unknown() {
            throw new IllegalStateException("boom");
        }

        @GetMapping("/fail/denied")
        String denied() {
            throw new AccessDeniedException("denied");
        }
    }

    record Payload(@NotBlank String name) {
    }
}
