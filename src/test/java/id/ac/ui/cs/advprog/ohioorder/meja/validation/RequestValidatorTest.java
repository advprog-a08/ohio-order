package id.ac.ui.cs.advprog.ohioorder.meja.validation;

import id.ac.ui.cs.advprog.ohioorder.meja.dto.MejaRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RequestValidatorTest {

    private boolean preValidateCalled = false;
    private boolean doValidateCalled = false;
    private boolean postValidateCalled = false;

    private final RequestValidator<MejaRequest> validator = new RequestValidator<MejaRequest>() {
        @Override
        protected void preValidate(MejaRequest request) {
            preValidateCalled = true;
        }

        @Override
        protected void doValidate(MejaRequest request) {
            doValidateCalled = true;
        }

        @Override
        protected void postValidate(MejaRequest request) {
            postValidateCalled = true;
        }
    };

    @Test
    void testValidateCallsAllMethodsInOrder() {
        MejaRequest request = MejaRequest.builder()
                .nomorMeja("A1")
                .build();

        validator.validate(request);

        assertTrue(preValidateCalled);
        assertTrue(doValidateCalled);
        assertTrue(postValidateCalled);
    }

    @Test
    void testValidateWithMinimalImplementation() {
        RequestValidator<MejaRequest> minimalValidator = new RequestValidator<MejaRequest>() {
            @Override
            protected void doValidate(MejaRequest request) {
            }
        };

        MejaRequest request = MejaRequest.builder()
                .nomorMeja("A1")
                .build();

        assertDoesNotThrow(() -> minimalValidator.validate(request));
    }

    @Test
    void testValidateWithExceptionInDoValidate() {
        RequestValidator<MejaRequest> exceptionValidator = new RequestValidator<MejaRequest>() {
            @Override
            protected void doValidate(MejaRequest request) {
                throw new RuntimeException("Validation failed");
            }
        };

        MejaRequest request = MejaRequest.builder()
                .nomorMeja("A1")
                .build();

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> exceptionValidator.validate(request)
        );

        assertEquals("Validation failed", exception.getMessage());
    }

    @Test
    void testTemplateMethodPattern() {
        StringBuilder executionOrder = new StringBuilder();

        RequestValidator<MejaRequest> templateValidator = new RequestValidator<MejaRequest>() {
            @Override
            protected void preValidate(MejaRequest request) {
                executionOrder.append("1");
            }

            @Override
            protected void doValidate(MejaRequest request) {
                executionOrder.append("2");
            }

            @Override
            protected void postValidate(MejaRequest request) {
                executionOrder.append("3");
            }
        };

        MejaRequest request = MejaRequest.builder()
                .nomorMeja("A1")
                .build();

        templateValidator.validate(request);

        assertEquals("123", executionOrder.toString());
    }
}