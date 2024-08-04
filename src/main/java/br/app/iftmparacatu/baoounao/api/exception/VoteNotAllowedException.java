package br.app.iftmparacatu.baoounao.api.exception;

public class VoteNotAllowedException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public VoteNotAllowedException(String message){
        super(message);
    }
}
