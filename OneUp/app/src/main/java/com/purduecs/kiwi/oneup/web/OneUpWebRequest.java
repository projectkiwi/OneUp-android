package com.purduecs.kiwi.oneup.web;


public interface OneUpWebRequest<T, R> {
    public final String BASE_URL = "api.kiwi.purduecs.com";

    public R parseResponse(T response);
}
