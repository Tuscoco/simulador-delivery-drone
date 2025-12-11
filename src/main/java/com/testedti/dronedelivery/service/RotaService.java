package com.testedti.dronedelivery.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.testedti.dronedelivery.exception.exceptions.DroneNaoEncontradoException;
import com.testedti.dronedelivery.exception.exceptions.NenhumDroneDisponivelException;
import com.testedti.dronedelivery.models.dtos.response.DroneResponseDTO;
import com.testedti.dronedelivery.models.entity.Coordenada;
import com.testedti.dronedelivery.models.entity.Drone;
import com.testedti.dronedelivery.models.entity.Pedido;
import com.testedti.dronedelivery.models.enums.EstadoDrone;

@Service
public class RotaService {
    
    private final DroneService droneService;

    public RotaService(DroneService droneService){
        this.droneService = droneService;
    }

    public Drone buscarDroneIdeal(double pesoTotal, List<Pedido> pedidos){
        List<DroneResponseDTO> drones = droneService.buscarDronesPorCapacidade(pesoTotal);
        
        List<DroneResponseDTO> dronesDisponiveis = drones.stream()
            .filter(d -> d.drone().getEstado() == EstadoDrone.DISPONIVEL)
            .collect(Collectors.toList());
        
        if(dronesDisponiveis.isEmpty()){
            throw new NenhumDroneDisponivelException("Nenhum drone disponível no momento para a entrega!");
        }
        
        return dronesDisponiveis.stream()
            .min(Comparator.comparingDouble(d -> d.drone().getCapacidadeCarga()))
            .map(DroneResponseDTO::drone)
            .orElseThrow(() -> new DroneNaoEncontradoException("Erro ao selecionar drone!"));
    }

    public List<Pedido> otimizarRota(List<Pedido> pedidos){
        List<Pedido> rotaOtimizada = new ArrayList<>();
        List<Pedido> pedidosPendentes = new ArrayList<>(pedidos);
        
        Pedido atual = pedidosPendentes.remove(0);
        rotaOtimizada.add(atual);
        
        while(!pedidosPendentes.isEmpty()){
            final Pedido pedidoAtual = atual;
            Pedido proximo = pedidosPendentes.stream()
                .min(Comparator.comparingDouble(p -> 
                    calcularDistanciaManhattan(pedidoAtual.getCoordenadaDestino(), p.getCoordenadaDestino())))
                .get();
            
            rotaOtimizada.add(proximo);
            pedidosPendentes.remove(proximo);
            atual = proximo;
        }
        
        return rotaOtimizada;
    }

    public double calcularDistanciaTotal(Coordenada coordenadaInicial, List<Pedido> rota){
        double distancia = 0;
        Coordenada atual = coordenadaInicial;
        
        for(Pedido pedido : rota){
            distancia += calcularDistanciaManhattan(atual, pedido.getCoordenadaDestino());
            atual = pedido.getCoordenadaDestino();
        }
        
        return distancia;
    }

    public double calcularDistanciaManhattan(Coordenada p1, Coordenada p2){
        int dx = Math.abs(p2.getX() - p1.getX());
        int dy = Math.abs(p2.getY() - p1.getY());
        return dx + dy;
    }

}
