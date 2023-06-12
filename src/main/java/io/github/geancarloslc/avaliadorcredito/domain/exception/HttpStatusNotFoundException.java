package io.github.geancarloslc.avaliadorcredito.domain.exception;

public class HttpStatusNotFoundException extends Exception {
    public HttpStatusNotFoundException(String mensagem) {
        super(mensagem);
    }
}
