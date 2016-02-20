package com.purduecs.kiwi.oneup.web;

public interface RequestHandler<T> {
    public void onSuccess(T response);
    public void onFailure();
}
