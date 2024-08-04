package br.app.iftmparacatu.baoounao.api.exception;

public class MissingTokenException extends RuntimeException{
    public MissingTokenException(){
        super("Token ausente");
    }
}
