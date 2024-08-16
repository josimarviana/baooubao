package br.app.iftmparacatu.baoounao.api.exception;

public class InvalidDomainException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public InvalidDomainException(String message) {
        super(message);
    }
}
