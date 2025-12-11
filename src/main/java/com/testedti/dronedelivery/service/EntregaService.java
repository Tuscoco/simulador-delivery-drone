package com.testedti.dronedelivery.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.testedti.dronedelivery.exception.exceptions.CancelamentoException;
import com.testedti.dronedelivery.exception.exceptions.EntregaNaoEncontradaException;
import com.testedti.dronedelivery.models.dtos.request.CriarEntregaRequestDTO;
import com.testedti.dronedelivery.models.dtos.response.EntregaResponseDTO;
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
    private final RotaService rotaService;
    
    private static final double VELOCIDADE_DRONE = 50.0;

    public EntregaService(EntregaRepository entregaRepository, DroneService droneService, PedidoService pedidoService, RotaService rotaService){
        this.entregaRepository = entregaRepository;
        this.droneService = droneService;
        this.pedidoService = pedidoService;
        this.rotaService = rotaService;
    }

    public EntregaResponseDTO criarEntrega(CriarEntregaRequestDTO dto){
        List<Pedido> pedidos = pedidoService.listarPedidosPorIds(dto.pedidoIds());
        
        double pesoTotal = pedidos.stream()
            .mapToDouble(Pedido::getPesoPacote)
            .sum();
        
        Drone droneIdeal = rotaService.buscarDroneIdeal(pesoTotal, pedidos);
        List<Pedido> rotaOtimizada = rotaService.otimizarRota(pedidos);
        double distanciaTotal = rotaService.calcularDistanciaTotal(droneIdeal.getCoordenadaAtual(), rotaOtimizada);
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
