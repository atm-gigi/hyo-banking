package org.atmgigi.hyobankingbe.common.exception;

import org.springframework.http.ProblemDetail;

public final class ErrorResponses {

    private ErrorResponses() {}

    public static ProblemDetail of(GlobalException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(ex.getStatus());
        pd.setTitle(ex.getCode().name());
        pd.setDetail(ex.getMessage());
        pd.setProperties(ex.getMeta());
        return pd;
    }
}
