package com.testedti.dronedelivery.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.testedti.dronedelivery.models.dtos.request.DroneRequestDTO;
import com.testedti.dronedelivery.models.dtos.response.DroneResponseDTO;
import com.testedti.dronedelivery.service.DroneService;

@RestController
@RequestMapping("api/v1/drones")
public class DroneController {
    
    private final DroneService droneService;

    public DroneController(DroneService droneService){
        this.droneService = droneService;
    }

    @PostMapping
    public ResponseEntity<DroneResponseDTO> registrarDrone(@RequestBody DroneRequestDTO registrarDroneRequestDTO){
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(droneService.registrarDrone(registrarDroneRequestDTO));
    }

    @GetMapping
    public ResponseEntity<List<DroneResponseDTO>> listarTodosDrones(){
        return ResponseEntity.status(HttpStatus.OK)
            .body(droneService.listarTodosDrones());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DroneResponseDTO> buscarDronePorId(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK)
            .body(droneService.buscarDronePorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DroneResponseDTO> atualizarDrone(@PathVariable Long id, @RequestBody DroneRequestDTO atualizacaoDTO){
        return ResponseEntity.status(HttpStatus.OK)
            .body(droneService.atualizarDrone(id, atualizacaoDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarDrone(@PathVariable Long id){
        droneService.deletarDrone(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/disponiveis")
    public ResponseEntity<List<DroneResponseDTO>> buscarDronesDisponiveis(){
        return ResponseEntity.status(HttpStatus.OK)
            .body(droneService.buscarDronesDisponiveis());
    }

    @GetMapping("/capacidade")
    public ResponseEntity<List<DroneResponseDTO>> buscarDronesPorCapacidade(@RequestParam double peso){
        return ResponseEntity.status(HttpStatus.OK)
            .body(droneService.buscarDronesPorCapacidade(peso));
    }

}
