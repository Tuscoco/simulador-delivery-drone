package com.testedti.dronedelivery.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
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

import com.testedti.dronedelivery.exception.exceptions.PedidoNaoEncontradoException;
import com.testedti.dronedelivery.models.dtos.request.CoordenadaDTO;
import com.testedti.dronedelivery.models.dtos.request.CriarPedidoRequestDTO;
import com.testedti.dronedelivery.models.dtos.response.BuscarPedidoDTO;
import com.testedti.dronedelivery.models.dtos.response.CriarPedidoResponseDTO;
import com.testedti.dronedelivery.models.entity.Coordenada;
import com.testedti.dronedelivery.models.entity.Pedido;
import com.testedti.dronedelivery.models.enums.PrioridadePedido;
import com.testedti.dronedelivery.repository.PedidoRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes PedidoService")
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private PedidoService pedidoService;

    private Pedido pedido;
    private CriarPedidoRequestDTO pedidoRequestDTO;

    @BeforeEach
    void setUp(){
        pedido = new Pedido();
        pedido.setId(1L);
        pedido.setPesoPacote(2.5);
        pedido.setPrioridade(PrioridadePedido.ALTA);
        pedido.setDescricao("Entrega urgente");
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setCoordenadaDestino(new Coordenada(10, 20));

        pedidoRequestDTO = new CriarPedidoRequestDTO(
            2.5,
            new CoordenadaDTO(10, 20),
            PrioridadePedido.ALTA,
            "Entrega urgente"
        );
    }

    @Test
    @DisplayName("Deve criar um novo pedido")
    void testCriarPedido(){
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        CriarPedidoResponseDTO resultado = pedidoService.criarPedido(pedidoRequestDTO);

        assertNotNull(resultado);
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    @DisplayName("Deve listar todos os pedidos")
    void testListarTodosPedidos(){
        Pedido pedido2 = new Pedido();
        pedido2.setId(2L);
        pedido2.setPrioridade(PrioridadePedido.MEDIA);
        
        when(pedidoRepository.findAll()).thenReturn(Arrays.asList(pedido, pedido2));

        List<BuscarPedidoDTO> resultado = pedidoService.listarTodosPedidos();

        assertEquals(2, resultado.size());
        verify(pedidoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar pedido por ID")
    void testBuscarPedidoPorId(){
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        BuscarPedidoDTO resultado = pedidoService.buscarPedidoPorId(1L);

        assertNotNull(resultado);
        verify(pedidoRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando pedido não encontrado")
    void testBuscarPedidoNaoEncontrado(){
        when(pedidoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(PedidoNaoEncontradoException.class, () -> 
            pedidoService.buscarPedidoPorId(999L)
        );
    }

    @Test
    @DisplayName("Deve listar pedidos por prioridade")
    void testListarPedidosPorPrioridade(){
        when(pedidoRepository.findByPrioridade(PrioridadePedido.ALTA)).thenReturn(Arrays.asList(pedido));

        List<BuscarPedidoDTO> resultado = pedidoService.listarPedidosPorPrioridade(PrioridadePedido.ALTA);

        assertEquals(1, resultado.size());
        verify(pedidoRepository, times(1)).findByPrioridade(PrioridadePedido.ALTA);
    }

    @Test
    @DisplayName("Deve atualizar pedido")
    void testAtualizarPedido(){
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        BuscarPedidoDTO resultado = pedidoService.atualizarPedido(1L, pedidoRequestDTO);

        assertNotNull(resultado);
        verify(pedidoRepository, times(1)).findById(1L);
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    @DisplayName("Deve deletar pedido")
    void testDeletarPedido(){
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        pedidoService.deletarPedido(1L);

        verify(pedidoRepository, times(1)).findById(1L);
        verify(pedidoRepository, times(1)).delete(pedido);
    }

    @Test
    @DisplayName("Deve deletar todos os pedidos")
    void testDeletarTodosPedidos(){
        pedidoService.deletarTodosPedidos();

        verify(pedidoRepository, times(1)).deleteAll();
    }

}
