package org.myapp.groovie.response;

import com.stripe.exception.StripeException;

public interface ApiCallExecutor<T> {
    ApiCallResponse<T> call() throws ApiCallException;
}
