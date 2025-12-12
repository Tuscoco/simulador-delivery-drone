package com.testedti.dronedelivery.models.enums;

public enum EstadoDrone {
    
    DISPONIVEL(1),
    PREPARANDO_ENTREGA(2),
    EM_VOO(3),
    CARREGANDO(4);

    private final int valor;

    EstadoDrone(int valor){
        this.valor = valor;
    }

    public int getValor(){
        return valor;
    }

}
