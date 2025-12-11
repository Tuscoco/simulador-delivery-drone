package com.testedti.dronedelivery.models.enums;

public enum EstadoDrone {
    
    DISPONIVEL(1),
    EM_VOO(2),
    CARREGANDO(3);

    private final int valor;

    EstadoDrone(int valor){
        this.valor = valor;
    }

    public int getValor(){
        return valor;
    }

}
