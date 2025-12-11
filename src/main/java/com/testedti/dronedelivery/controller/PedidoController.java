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

import com.testedti.dronedelivery.models.dtos.request.CriarPedidoRequestDTO;
import com.testedti.dronedelivery.models.dtos.response.BuscarPedidoDTO;
import com.testedti.dronedelivery.models.dtos.response.CriarPedidoResponseDTO;
import com.testedti.dronedelivery.models.enums.PrioridadePedido;
import com.testedti.dronedelivery.service.PedidoService;

@RestController
@RequestMapping("api/v1/pedidos")
public class PedidoController {
    
    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService){
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<CriarPedidoResponseDTO> criarPedido(@RequestBody CriarPedidoRequestDTO criarPedidoRequestDTO){
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(pedidoService.criarPedido(criarPedidoRequestDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BuscarPedidoDTO> buscarPedidoPorId(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK)
            .body(pedidoService.buscarPedidoPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<BuscarPedidoDTO>> listarTodosPedidos(){
        return ResponseEntity.status(HttpStatus.OK)
            .body(pedidoService.listarTodosPedidos());
    }

    @GetMapping("/prioridade")
    public ResponseEntity<List<BuscarPedidoDTO>> listarPedidosPorPrioridade(@RequestParam String prioridade){
        return ResponseEntity.status(HttpStatus.OK)
            .body(pedidoService.listarPedidosPorPrioridade(
                prioridade != null ? 
                Enum.valueOf(PrioridadePedido.class, prioridade.toUpperCase()) 
                : null));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BuscarPedidoDTO> atualizarPedido(@PathVariable Long id, @RequestBody CriarPedidoRequestDTO atualizacaoDTO){
        return ResponseEntity.status(HttpStatus.OK)
            .body(pedidoService.atualizarPedido(id, atualizacaoDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPedido(@PathVariable Long id){
        pedidoService.deletarPedido(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
