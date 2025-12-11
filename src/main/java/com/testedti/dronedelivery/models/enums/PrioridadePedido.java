package com.testedti.dronedelivery.models.enums;

public enum PrioridadePedido {
    
    ALTA(1),
    MEDIA(2),
    BAIXA(3);
    
    private final int valor;
    
    PrioridadePedido(int valor){
        this.valor = valor;
    }
    
    public int getValor(){
        return valor;
    }

}
