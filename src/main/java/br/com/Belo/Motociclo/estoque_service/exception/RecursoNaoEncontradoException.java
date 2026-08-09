package br.com.Belo.Motociclo.estoque_service.exception;

public class RecursoNaoEncontradoException extends RuntimeException {
    public RecursoNaoEncontradoException(String message) {
        super(message);
    }
}