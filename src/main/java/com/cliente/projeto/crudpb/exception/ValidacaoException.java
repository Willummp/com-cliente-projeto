package com.cliente.projeto.crudpb.exception;

public class ValidacaoException extends RuntimeException {

    public ValidacaoException(String message) {
        // "super(message)" passa a mensagem para o construtor da RuntimeException,
        // permitindo que o "ex.getMessage()" funcione
        super(message);
    }
}