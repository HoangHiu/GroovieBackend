package org.myapp.groovie.response;

public interface ApiCallExecutor<T> {
    ApiCallResponse<T> call() throws ApiCallException;
}
