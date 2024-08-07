package br.app.iftmparacatu.baoounao.api.exception;

public class InactiveUserException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public InactiveUserException(String message){
        super(message);
    }
}
