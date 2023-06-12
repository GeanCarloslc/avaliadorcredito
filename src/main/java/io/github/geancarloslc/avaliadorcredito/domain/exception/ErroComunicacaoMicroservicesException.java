package io.github.geancarloslc.avaliadorcredito.domain.exception;

public class ErroComunicacaoMicroservicesException extends Exception{
    private Integer status;
    public ErroComunicacaoMicroservicesException(String mensgem, Integer status) {
        super(mensgem);
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }
}
