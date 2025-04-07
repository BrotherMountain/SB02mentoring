package com.jyami.error;

public enum ErrorStatus {
    C001("invalid_request"),
    C002("not support channel"),
    C003("not found"),

    A001("unAuthorized"),

    S001("internal server error");

    ErrorStatus(String description) {

    }

}
