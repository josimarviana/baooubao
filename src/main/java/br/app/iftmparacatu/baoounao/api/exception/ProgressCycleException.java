package br.app.iftmparacatu.baoounao.api.exception;

public class ProgressCycleException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public ProgressCycleException(String message){
        super(message);
    }
}
