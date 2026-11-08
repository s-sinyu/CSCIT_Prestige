package com.prestige.renovation.support;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.springframework.http.HttpStatus;

import com.prestige.renovation.common.exception.ApiException;

public final class ApiAssertions {

    private ApiAssertions() {
    }

    public static void assertApiError(ThrowingCallable call, HttpStatus status) {
        assertThatThrownBy(call).isInstanceOf(ApiException.class)
                .extracting(e -> ((ApiException) e).getStatus()).isEqualTo(status);
    }
}
