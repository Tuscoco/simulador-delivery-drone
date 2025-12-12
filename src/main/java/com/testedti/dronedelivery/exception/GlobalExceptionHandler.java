package com.testedti.dronedelivery.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.testedti.dronedelivery.exception.exceptions.CancelamentoException;
import com.testedti.dronedelivery.exception.exceptions.DroneNaoEncontradoException;
import com.testedti.dronedelivery.exception.exceptions.EntregaNaoEncontradaException;
import com.testedti.dronedelivery.exception.exceptions.NenhumDroneDisponivelException;
import com.testedti.dronedelivery.exception.exceptions.PedidoNaoEncontradoException;
import com.testedti.dronedelivery.models.dtos.response.ErrorResponse;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(PedidoNaoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handlePedidoNaoEncontradoException(PedidoNaoEncontradoException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(
            LocalDateTime.now(), 404, "Pedido Nao Encontrado", ex.getMessage()
        ));
    }

    @ExceptionHandler(DroneNaoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleDroneNaoEncontradoException(DroneNaoEncontradoException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(
            LocalDateTime.now(), 404, "Drone Nao Encontrado", ex.getMessage()
        ));
    }

    @ExceptionHandler(EntregaNaoEncontradaException.class)
    public ResponseEntity<ErrorResponse> handleEntregaNaoEncontradaException(EntregaNaoEncontradaException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(
            LocalDateTime.now(), 404, "Drone Nao Encontrado", ex.getMessage()
        ));
    }

    @ExceptionHandler(NenhumDroneDisponivelException.class)
    public ResponseEntity<ErrorResponse> handleNenhumDroneDisponivelException(NenhumDroneDisponivelException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(
            LocalDateTime.now(), 400, "Nenhum drone disponível", ex.getMessage()
        ));
    }

    @ExceptionHandler(CancelamentoException.class)
    public ResponseEntity<ErrorResponse> handleCancelamentoException(CancelamentoException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(
            LocalDateTime.now(), 400, "Erro de Cancelamento", ex.getMessage()
        ));
    }

}
