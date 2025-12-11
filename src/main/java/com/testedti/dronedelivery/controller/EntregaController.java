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

import com.testedti.dronedelivery.models.dtos.request.CriarEntregaRequestDTO;
import com.testedti.dronedelivery.models.dtos.response.EntregaResponseDTO;
import com.testedti.dronedelivery.models.enums.StatusEntrega;
import com.testedti.dronedelivery.service.EntregaService;

@RestController
@RequestMapping("api/v1/entregas")
public class EntregaController {
    
    private final EntregaService entregaService;

    public EntregaController(EntregaService entregaService){
        this.entregaService = entregaService;
    }

    @PostMapping
    public ResponseEntity<EntregaResponseDTO> criarEntrega(@RequestBody CriarEntregaRequestDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(entregaService.criarEntrega(dto));
    }

    @GetMapping
    public ResponseEntity<List<EntregaResponseDTO>> listarTodasEntregas(){
        return ResponseEntity.status(HttpStatus.OK)
            .body(entregaService.listarTodasEntregas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntregaResponseDTO> buscarEntregaPorId(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK)
            .body(entregaService.buscarEntregaPorId(id));
    }

    @GetMapping("/status")
    public ResponseEntity<List<EntregaResponseDTO>> listarEntregasPorStatus(@RequestParam StatusEntrega status){
        return ResponseEntity.status(HttpStatus.OK)
            .body(entregaService.listarEntregasPorStatus(status));
    }

    @PutMapping("/{id}/iniciar")
    public ResponseEntity<EntregaResponseDTO> iniciarEntrega(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK)
            .body(entregaService.iniciarEntrega(id));
    }

    @PutMapping("/{id}/concluir")
    public ResponseEntity<EntregaResponseDTO> concluirEntrega(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK)
            .body(entregaService.concluirEntrega(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelarEntrega(@PathVariable Long id){
        entregaService.cancelarEntrega(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
