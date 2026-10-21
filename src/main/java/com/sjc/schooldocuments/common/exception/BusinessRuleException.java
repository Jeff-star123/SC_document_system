package com.sjc.schooldocuments.common.exception;

/**
 * A rule the registrar or administrator must resolve, translated into a clear
 * message rather than a stack trace.
 */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
