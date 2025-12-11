package com.testedti.dronedelivery.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.testedti.dronedelivery.exception.exceptions.CancelamentoException;
import com.testedti.dronedelivery.exception.exceptions.DroneNaoEncontradoException;
import com.testedti.dronedelivery.exception.exceptions.EntregaNaoEncontradaException;
import com.testedti.dronedelivery.exception.exceptions.NenhumDroneDisponivelException;
import com.testedti.dronedelivery.models.dtos.request.CriarEntregaRequestDTO;
import com.testedti.dronedelivery.models.dtos.response.DroneResponseDTO;
import com.testedti.dronedelivery.models.dtos.response.EntregaResponseDTO;
import com.testedti.dronedelivery.models.entity.Coordenada;
import com.testedti.dronedelivery.models.entity.Drone;
import com.testedti.dronedelivery.models.entity.Entrega;
import com.testedti.dronedelivery.models.entity.Pedido;
import com.testedti.dronedelivery.models.enums.EstadoDrone;
import com.testedti.dronedelivery.models.enums.StatusEntrega;
import com.testedti.dronedelivery.repository.EntregaRepository;

@Service
public class EntregaService {
    
    private final EntregaRepository entregaRepository;
    private final DroneService droneService;
    private final PedidoService pedidoService;
    
    private static final double VELOCIDADE_DRONE = 50.0;

    public EntregaService(EntregaRepository entregaRepository, DroneService droneService, PedidoService pedidoService) {
        this.entregaRepository = entregaRepository;
        this.droneService = droneService;
        this.pedidoService = pedidoService;
    }

    public EntregaResponseDTO criarEntrega(CriarEntregaRequestDTO dto){
        List<Pedido> pedidos = pedidoService.listarPedidosPorIds(dto.pedidoIds());
        
        double pesoTotal = pedidos.stream()
            .mapToDouble(Pedido::getPesoPacote)
            .sum();
        
        Drone droneIdeal = buscarDroneIdeal(pesoTotal, pedidos);
        List<Pedido> rotaOtimizada = otimizarRota(pedidos);
        
        double distanciaTotal = calcularDistanciaTotal(droneIdeal.getCoordenadaAtual(), rotaOtimizada);
        double tempoEstimado = distanciaTotal / VELOCIDADE_DRONE;
        
        Entrega entrega = new Entrega();
        entrega.setDrone(droneIdeal);
        entrega.setPedidos(rotaOtimizada);
        entrega.setDistanciaTotal(distanciaTotal);
        entrega.setTempoEstimado(tempoEstimado);
        entrega.setStatus(StatusEntrega.PREPARANDO);
        entrega.setDataHoraInicio(null);
        
        Entrega entregaSalva = entregaRepository.save(entrega);
        droneService.atualizarStatusDrone(droneIdeal.getId(), EstadoDrone.CARREGANDO);
        
        return new EntregaResponseDTO(entregaSalva);
    }

    public EntregaResponseDTO buscarEntregaPorId(Long id){
        Entrega entrega = entregaRepository.findById(id)
            .orElseThrow(() -> new EntregaNaoEncontradaException("Entrega de id " + id + " não encontrada!"));
        return new EntregaResponseDTO(entrega);
    }

    public List<EntregaResponseDTO> listarTodasEntregas(){
        return entregaRepository.findAll()
            .stream()
            .map(EntregaResponseDTO::new)
            .collect(Collectors.toList());
    }

    public List<EntregaResponseDTO> listarEntregasPorStatus(StatusEntrega status){
        return entregaRepository.findAll()
            .stream()
            .filter(entrega -> entrega.getStatus() == status)
            .map(EntregaResponseDTO::new)
            .collect(Collectors.toList());
    }

    private Drone buscarDroneIdeal(double pesoTotal, List<Pedido> pedidos){
        List<DroneResponseDTO> drones = droneService.buscarDronesPorCapacidade(pesoTotal);
        
        List<DroneResponseDTO> dronesDisponiveis = drones.stream()
            .filter(d -> d.drone().getEstado() == EstadoDrone.DISPONIVEL)
            .collect(Collectors.toList());
        
        if(dronesDisponiveis.isEmpty()){
            throw new NenhumDroneDisponivelException("Nenhum drone disponível no momento para a entrega!");
        }
        
        Coordenada primeiroPedido = pedidos.get(0).getCoordenadaDestino();
        
        return dronesDisponiveis.stream()
            .min(Comparator.comparingDouble(d -> calcularDistanciaManhattan(d.drone().getCoordenadaAtual(), primeiroPedido)))
            .map(DroneResponseDTO::drone)
            .orElseThrow(() -> new DroneNaoEncontradoException("Erro ao selecionar drone!"));
    }

    private List<Pedido> otimizarRota(List<Pedido> pedidos){
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

    private double calcularDistanciaTotal(Coordenada coordenadaInicial, List<Pedido> rota){
        double distancia = 0;
        Coordenada atual = coordenadaInicial;
        
        for(Pedido pedido : rota){
            distancia += calcularDistanciaManhattan(atual, pedido.getCoordenadaDestino());
            atual = pedido.getCoordenadaDestino();
        }
        
        return distancia;
    }

    private double calcularDistanciaManhattan(Coordenada p1, Coordenada p2){
        int dx = Math.abs(p2.getX() - p1.getX());
        int dy = Math.abs(p2.getY() - p1.getY());
        return dx + dy;
    }

    public EntregaResponseDTO iniciarEntrega(Long id){
        Entrega entrega = entregaRepository.findById(id)
            .orElseThrow(() -> new EntregaNaoEncontradaException("Entrega de id " + id + " não encontrada!"));
        
        entrega.setStatus(StatusEntrega.ENTREGANDO);
        entrega.setDataHoraInicio(LocalDateTime.now());
        
        droneService.atualizarStatusDrone(entrega.getDrone().getId(), EstadoDrone.EM_VOO);
        
        Entrega entregaAtualizada = entregaRepository.save(entrega);
        return new EntregaResponseDTO(entregaAtualizada);
    }

    public EntregaResponseDTO concluirEntrega(Long id){
        Entrega entrega = entregaRepository.findById(id)
            .orElseThrow(() -> new EntregaNaoEncontradaException("Entrega de id " + id + " não encontrada!"));
        
        entrega.setStatus(StatusEntrega.CONCLUIDA);
        entrega.setDataHoraConclusao(LocalDateTime.now());
        
        droneService.atualizarStatusDrone(entrega.getDrone().getId(), EstadoDrone.DISPONIVEL);
        
        double consumoBateria = entrega.getDistanciaTotal() * 0.1;
        droneService.atualizarAutonomia(entrega.getDrone().getId(), consumoBateria);
        
        Entrega entregaAtualizada = entregaRepository.save(entrega);
        return new EntregaResponseDTO(entregaAtualizada);
    }

    public void cancelarEntrega(Long id){
        Entrega entrega = entregaRepository.findById(id)
            .orElseThrow(() -> new EntregaNaoEncontradaException("Entrega de id " + id + " não encontrada!"));
        
        if(entrega.getStatus() != StatusEntrega.PREPARANDO){
            throw new CancelamentoException("Apenas entregas em preparação podem ser canceladas!");
        }
        
        droneService.atualizarStatusDrone(entrega.getDrone().getId(), EstadoDrone.DISPONIVEL);
        
        entregaRepository.delete(entrega);
    }

    public EntregaResponseDTO atualizarStatusEntrega(Long id, StatusEntrega novoStatus){
        Entrega entrega = entregaRepository.findById(id)
            .orElseThrow(() -> new EntregaNaoEncontradaException("Entrega de id " + id + " não encontrada!"));
        
        entrega.setStatus(novoStatus);
        Entrega entregaAtualizada = entregaRepository.save(entrega);
        return new EntregaResponseDTO(entregaAtualizada);
    }

}
