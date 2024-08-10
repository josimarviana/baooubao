package br.app.iftmparacatu.baoounao.api.exception;

public class NotAllowedOperation extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public NotAllowedOperation(String message){
        super(message);
    }
}
