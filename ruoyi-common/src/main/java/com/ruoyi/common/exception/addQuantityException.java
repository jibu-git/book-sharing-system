package com.ruoyi.common.exception;

public class addQuantityException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    protected final String message;

    public addQuantityException(String message)
    {
        this.message = message;
    }

    @Override
    public String getMessage()
    {
        return message;
    }
}
