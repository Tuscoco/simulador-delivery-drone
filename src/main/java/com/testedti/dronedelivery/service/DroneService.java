package com.testedti.dronedelivery.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.testedti.dronedelivery.exception.exceptions.DroneNaoEncontradoException;
import com.testedti.dronedelivery.models.dtos.request.DroneRequestDTO;
import com.testedti.dronedelivery.models.dtos.response.DroneResponseDTO;
import com.testedti.dronedelivery.models.entity.Coordenada;
import com.testedti.dronedelivery.models.entity.Drone;
import com.testedti.dronedelivery.models.enums.EstadoDrone;
import com.testedti.dronedelivery.repository.DroneRepository;

@Service
public class DroneService {
    
    private final DroneRepository droneRepository;

    public DroneService(DroneRepository droneRepository){
        this.droneRepository = droneRepository;
    }

    public DroneResponseDTO registrarDrone(DroneRequestDTO dto){
        Drone drone = new Drone(dto);
        return new DroneResponseDTO(droneRepository.save(drone));
    }

    public List<DroneResponseDTO> listarTodosDrones(){
        return droneRepository.findAll()
            .stream()
            .map(DroneResponseDTO::new)
            .collect(Collectors.toList());
    }

    public DroneResponseDTO buscarDronePorId(Long id){
        Drone drone = droneRepository.findById(id)
            .orElseThrow(() -> new DroneNaoEncontradoException("Drone de id " + id + " não encontrado!"));
        return new DroneResponseDTO(drone);
    }

    public DroneResponseDTO atualizarDrone(Long id, DroneRequestDTO atualizacaoDTO){
        Drone drone = droneRepository.findById(id)
            .orElseThrow(() -> new DroneNaoEncontradoException("Drone de id " + id + " não encontrado!"));
        
        drone.setModelo(atualizacaoDTO.modelo());
        drone.setCapacidadeCarga(atualizacaoDTO.capacidadeCarga());
        drone.setAutonomiaVoo(atualizacaoDTO.autonomiaVoo());
        
        Drone droneAtualizado = droneRepository.save(drone);
        return new DroneResponseDTO(droneAtualizado);
    }

    public void deletarDrone(Long id){
        Drone drone = droneRepository.findById(id)
            .orElseThrow(() -> new DroneNaoEncontradoException("Drone de id " + id + " não encontrado!"));
        droneRepository.delete(drone);
    }

    public List<DroneResponseDTO> buscarDronesDisponiveis(){
        return droneRepository.findByEstado(EstadoDrone.DISPONIVEL)
            .stream()
            .map(DroneResponseDTO::new)
            .collect(Collectors.toList());
    }

    public List<DroneResponseDTO> buscarDronesEmVoo(){
        return droneRepository.findByEstado(EstadoDrone.EM_VOO)
            .stream()
            .map(DroneResponseDTO::new)
            .collect(Collectors.toList());
    }

    public List<DroneResponseDTO> buscarDronesCarregando(){
        return droneRepository.findByEstado(EstadoDrone.CARREGANDO)
            .stream()
            .map(DroneResponseDTO::new)
            .collect(Collectors.toList());
    }

    public List<DroneResponseDTO> buscarDronesPorCapacidade(double pesoMinimo){
        return droneRepository.findByCapacidadeGreaterThanOrEqual(pesoMinimo)
            .stream()
            .map(DroneResponseDTO::new)
            .collect(Collectors.toList());
    }

    public DroneResponseDTO atualizarPosicaoDrone(Long id, int x, int y){
        Drone drone = droneRepository.findById(id)
            .orElseThrow(() -> new DroneNaoEncontradoException("Drone de id " + id + " não encontrado!"));
        
        drone.setCoordenadaAtual(new Coordenada(x, y));
        Drone droneAtualizado = droneRepository.save(drone);
        return new DroneResponseDTO(droneAtualizado);
    }

    public DroneResponseDTO atualizarAutonomia(Long id, double consumoBateria){
        Drone drone = droneRepository.findById(id)
            .orElseThrow(() -> new DroneNaoEncontradoException("Drone de id " + id + " não encontrado!"));
        
        double novaAutonomia = drone.getAutonomiaAtual() - consumoBateria;
        drone.setAutonomiaAtual(Math.max(0, novaAutonomia));
        
        Drone droneAtualizado = droneRepository.save(drone);
        return new DroneResponseDTO(droneAtualizado);
    }

    public DroneResponseDTO atualizarStatusDrone(Long id, EstadoDrone novoEstado){
        Drone drone = droneRepository.findById(id)
            .orElseThrow(() -> new DroneNaoEncontradoException("Drone de id " + id + " não encontrado!"));
        
        drone.setEstado(novoEstado);
        Drone droneAtualizado = droneRepository.save(drone);
        return new DroneResponseDTO(droneAtualizado);
    }

}
