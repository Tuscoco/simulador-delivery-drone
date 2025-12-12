package com.testedti.dronedelivery.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.testedti.dronedelivery.exception.exceptions.CancelamentoException;
import com.testedti.dronedelivery.exception.exceptions.EntregaNaoEncontradaException;
import com.testedti.dronedelivery.models.dtos.request.CriarEntregaRequestDTO;
import com.testedti.dronedelivery.models.dtos.response.DroneResponseDTO;
import com.testedti.dronedelivery.models.dtos.response.EntregaResponseDTO;
import com.testedti.dronedelivery.models.entity.Coordenada;
import com.testedti.dronedelivery.models.entity.Drone;
import com.testedti.dronedelivery.models.entity.Entrega;
import com.testedti.dronedelivery.models.entity.Pedido;
import com.testedti.dronedelivery.models.enums.EstadoDrone;
import com.testedti.dronedelivery.models.enums.PrioridadePedido;
import com.testedti.dronedelivery.models.enums.StatusEntrega;
import com.testedti.dronedelivery.repository.EntregaRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes EntregaService")
class EntregaServiceTest {

    @Mock
    private EntregaRepository entregaRepository;

    @Mock
    private DroneService droneService;

    @Mock
    private PedidoService pedidoService;

    @Mock
    private RotaService rotaService;

    @InjectMocks
    private EntregaService entregaService;

    private Entrega entrega;
    private Drone drone;
    private Pedido pedido1;
    private Pedido pedido2;

    @BeforeEach
    void setUp(){
        drone = new Drone();
        drone.setId(1L);
        drone.setModelo("DJI Phantom 4");
        drone.setCapacidadeCarga(5.0);
        drone.setEstado(EstadoDrone.DISPONIVEL);
        drone.setCoordenadaAtual(new Coordenada(0, 0));

        pedido1 = new Pedido();
        pedido1.setId(1L);
        pedido1.setPesoPacote(1.5);
        pedido1.setPrioridade(PrioridadePedido.ALTA);
        pedido1.setCoordenadaDestino(new Coordenada(10, 20));

        pedido2 = new Pedido();
        pedido2.setId(2L);
        pedido2.setPesoPacote(1.0);
        pedido2.setPrioridade(PrioridadePedido.MEDIA);
        pedido2.setCoordenadaDestino(new Coordenada(15, 25));

        entrega = new Entrega();
        entrega.setId(1L);
        entrega.setDrone(drone);
        entrega.setPedidos(Arrays.asList(pedido1, pedido2));
        entrega.setDistanciaTotal(50.0);
        entrega.setTempoEstimado(1.0);
        entrega.setStatus(StatusEntrega.PREPARANDO);
    }

    @Test
    @DisplayName("Deve criar uma nova entrega")
    void testCriarEntrega(){
        CriarEntregaRequestDTO dto = new CriarEntregaRequestDTO(Arrays.asList(1L, 2L));
        
        when(pedidoService.listarPedidosPorIds(Arrays.asList(1L, 2L)))
            .thenReturn(Arrays.asList(pedido1, pedido2));
        when(rotaService.buscarDroneIdeal(2.5, Arrays.asList(pedido1, pedido2)))
            .thenReturn(drone);
        when(rotaService.otimizarRota(Arrays.asList(pedido1, pedido2)))
            .thenReturn(Arrays.asList(pedido1, pedido2));
        when(rotaService.calcularDistanciaTotal(any(Coordenada.class), anyList()))
            .thenReturn(50.0);
        when(entregaRepository.save(any(Entrega.class)))
            .thenReturn(entrega);
        when(droneService.atualizarStatusDrone(1L, EstadoDrone.PREPARANDO_ENTREGA))
            .thenReturn(any());

        EntregaResponseDTO resultado = entregaService.criarEntrega(dto);

        assertNotNull(resultado);
    }

    @Test
    @DisplayName("Deve listar todas as entregas")
    void testListarTodasEntregas(){
        when(entregaRepository.findAll()).thenReturn(Arrays.asList(entrega));

        List<EntregaResponseDTO> resultado = entregaService.listarTodasEntregas();

        assertEquals(1, resultado.size());
        verify(entregaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar entrega por ID")
    void testBuscarEntregaPorId(){
        when(entregaRepository.findById(1L)).thenReturn(Optional.of(entrega));

        EntregaResponseDTO resultado = entregaService.buscarEntregaPorId(1L);

        assertNotNull(resultado);
        verify(entregaRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando entrega não encontrada")
    void testBuscarEntregaNaoEncontrada(){
        when(entregaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntregaNaoEncontradaException.class, () -> 
            entregaService.buscarEntregaPorId(999L)
        );
    }

    @Test
    @DisplayName("Deve listar entregas por status")
    void testListarEntregasPorStatus(){
        when(entregaRepository.findAll()).thenReturn(Arrays.asList(entrega));

        List<EntregaResponseDTO> resultado = entregaService.listarEntregasPorStatus(StatusEntrega.PREPARANDO);

        assertEquals(1, resultado.size());
        verify(entregaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve iniciar uma entrega")
    void testIniciarEntrega(){
        when(entregaRepository.findById(1L)).thenReturn(Optional.of(entrega));
        when(entregaRepository.save(any(Entrega.class))).thenReturn(entrega);
        when(droneService.atualizarStatusDrone(1L, EstadoDrone.EM_VOO)).thenReturn(any());

        EntregaResponseDTO resultado = entregaService.iniciarEntrega(1L);

        assertNotNull(resultado);
        verify(entregaRepository, times(1)).findById(1L);
        verify(droneService, times(1)).atualizarStatusDrone(1L, EstadoDrone.EM_VOO);
    }

    @Test
    @DisplayName("Deve concluir uma entrega")
    void testConcluirEntrega(){
        when(entregaRepository.findById(1L)).thenReturn(Optional.of(entrega));
        when(entregaRepository.save(any(Entrega.class))).thenReturn(entrega);
        when(droneService.atualizarStatusDrone(anyLong(), eq(EstadoDrone.DISPONIVEL)))
            .thenReturn(new DroneResponseDTO(drone));

        when(droneService.atualizarAutonomia(anyLong(), anyDouble()))
            .thenReturn(new DroneResponseDTO(drone));

        EntregaResponseDTO resultado = entregaService.concluirEntrega(1L);

        assertNotNull(resultado);
        verify(entregaRepository, times(1)).findById(1L);
        verify(droneService, times(1)).atualizarStatusDrone(1L, EstadoDrone.DISPONIVEL);
    }

    @Test
    @DisplayName("Deve cancelar uma entrega em preparação")
    void testCancelarEntrega(){
        when(entregaRepository.findById(1L)).thenReturn(Optional.of(entrega));
        when(droneService.atualizarStatusDrone(1L, EstadoDrone.DISPONIVEL)).thenReturn(any());

        entregaService.cancelarEntrega(1L);

        verify(entregaRepository, times(1)).delete(entrega);
    }

    @Test
    @DisplayName("Deve lançar exceção ao cancelar entrega não em preparação")
    void testCancelarEntregaEmAndamento(){
        entrega.setStatus(StatusEntrega.ENTREGANDO);
        when(entregaRepository.findById(1L)).thenReturn(Optional.of(entrega));

        assertThrows(CancelamentoException.class, () -> 
            entregaService.cancelarEntrega(1L)
        );
    }

    @Test
    @DisplayName("Deve atualizar status de entrega")
    void testAtualizarStatusEntrega(){
        when(entregaRepository.findById(1L)).thenReturn(Optional.of(entrega));
        when(entregaRepository.save(any(Entrega.class))).thenReturn(entrega);

        EntregaResponseDTO resultado = entregaService.atualizarStatusEntrega(1L, StatusEntrega.ENTREGANDO);

        assertNotNull(resultado);
        verify(entregaRepository, times(1)).findById(1L);
        verify(entregaRepository, times(1)).save(any(Entrega.class));
    }

}
