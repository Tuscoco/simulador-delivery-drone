package com.testedti.dronedelivery.models.enums;

public enum StatusEntrega {
    
    PREPARANDO(1),
    ENTREGANDO(2),
    RETORNANDO(3),
    CONCLUIDA(4);

    private final int valor;

    StatusEntrega(int valor){
        this.valor = valor;
    }

    public int getValor(){
        return valor;
    }

}
