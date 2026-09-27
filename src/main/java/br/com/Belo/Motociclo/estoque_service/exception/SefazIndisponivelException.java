package br.com.Belo.Motociclo.estoque_service.exception;

public class SefazIndisponivelException extends RuntimeException {

    public SefazIndisponivelException(String message) {
        super(message);
    }
}