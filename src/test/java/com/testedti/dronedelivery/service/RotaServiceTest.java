package com.testedti.dronedelivery.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.testedti.dronedelivery.exception.exceptions.NenhumDroneDisponivelException;
import com.testedti.dronedelivery.models.dtos.response.DroneResponseDTO;
import com.testedti.dronedelivery.models.entity.Coordenada;
import com.testedti.dronedelivery.models.entity.Drone;
import com.testedti.dronedelivery.models.entity.Pedido;
import com.testedti.dronedelivery.models.enums.EstadoDrone;
import com.testedti.dronedelivery.models.enums.PrioridadePedido;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes RotaService")
class RotaServiceTest {

    @Mock
    private DroneService droneService;

    @InjectMocks
    private RotaService rotaService;

    private Drone droneA;
    private Drone droneB;
    private Drone droneC;
    private Pedido pedido1;
    private Pedido pedido2;
    private Pedido pedido3;

    @BeforeEach
    void setUp(){
        droneA = new Drone();
        droneA.setId(1L);
        droneA.setModelo("DJI Phantom 4");
        droneA.setCapacidadeCarga(5.0);
        droneA.setAutonomiaVoo(30.0);
        droneA.setAutonomiaAtual(30.0);
        droneA.setEstado(EstadoDrone.DISPONIVEL);
        droneA.setCoordenadaAtual(new Coordenada(0, 0));

        droneB = new Drone();
        droneB.setId(2L);
        droneB.setModelo("DJI Phantom 4 Pro");
        droneB.setCapacidadeCarga(3.0);
        droneB.setAutonomiaVoo(35.0);
        droneB.setAutonomiaAtual(35.0);
        droneB.setEstado(EstadoDrone.DISPONIVEL);
        droneB.setCoordenadaAtual(new Coordenada(0, 0));

        droneC = new Drone();
        droneC.setId(3L);
        droneC.setModelo("DJI Air 3S");
        droneC.setCapacidadeCarga(10.0);
        droneC.setAutonomiaVoo(25.0);
        droneC.setAutonomiaAtual(25.0);
        droneC.setEstado(EstadoDrone.DISPONIVEL);
        droneC.setCoordenadaAtual(new Coordenada(0, 0));

        pedido1 = new Pedido();
        pedido1.setId(1L);
        pedido1.setPesoPacote(1.5);
        pedido1.setPrioridade(PrioridadePedido.ALTA);
        pedido1.setDescricao("Pedido 1");
        pedido1.setCoordenadaDestino(new Coordenada(10, 20));

        pedido2 = new Pedido();
        pedido2.setId(2L);
        pedido2.setPesoPacote(1.0);
        pedido2.setPrioridade(PrioridadePedido.MEDIA);
        pedido2.setDescricao("Pedido 2");
        pedido2.setCoordenadaDestino(new Coordenada(15, 25));

        pedido3 = new Pedido();
        pedido3.setId(3L);
        pedido3.setPesoPacote(0.8);
        pedido3.setPrioridade(PrioridadePedido.BAIXA);
        pedido3.setDescricao("Pedido 3");
        pedido3.setCoordenadaDestino(new Coordenada(8, 16));
    }

    @Test
    @DisplayName("Deve encontrar o drone ideal com menor capacidade")
    void testBuscarDroneIdealMenorCapacidade(){
        List<Pedido> pedidos = Arrays.asList(pedido1, pedido2);
        double pesoTotal = 2.5; // pedido1(1.5) + pedido2(1.0)
        
        when(droneService.buscarDronesPorCapacidade(pesoTotal))
            .thenReturn(Arrays.asList(
                new DroneResponseDTO(droneA),
                new DroneResponseDTO(droneB),
                new DroneResponseDTO(droneC)
            ));

        Drone resultado = rotaService.buscarDroneIdeal(pesoTotal, pedidos);

        assertEquals(droneB.getId(), resultado.getId());
        assertEquals(3.0, resultado.getCapacidadeCarga());
        verify(droneService, times(1)).buscarDronesPorCapacidade(pesoTotal);
    }

    @Test
    @DisplayName("Deve lançar exceção quando nenhum drone disponível")
    void testBuscarDroneIdealSemDronesDisponiveis(){
        List<Pedido> pedidos = Arrays.asList(pedido1);
        double pesoTotal = 1.5;
        
        when(droneService.buscarDronesPorCapacidade(pesoTotal))
            .thenReturn(Arrays.asList(new DroneResponseDTO(droneA)));

        droneA.setEstado(EstadoDrone.EM_VOO);

        assertThrows(NenhumDroneDisponivelException.class, () -> 
            rotaService.buscarDroneIdeal(pesoTotal, pedidos)
        );
    }

    @Test
    @DisplayName("Deve otimizar rota com algoritmo nearest neighbor")
    void testOtimizarRota(){
        List<Pedido> pedidos = Arrays.asList(pedido1, pedido2, pedido3);
        List<Pedido> rotaOtimizada = rotaService.otimizarRota(pedidos);

        assertNotNull(rotaOtimizada);
        assertEquals(3, rotaOtimizada.size());
        assertEquals(pedido1.getId(), rotaOtimizada.get(0).getId());
    }

    @Test
    @DisplayName("Deve calcular distância Manhattan corretamente")
    void testCalcularDistanciaManhattan(){
        Coordenada p1 = new Coordenada(0, 0);
        Coordenada p2 = new Coordenada(3, 4);

        double distancia = rotaService.calcularDistanciaManhattan(p1, p2);

        assertEquals(7.0, distancia);
    }

    @Test
    @DisplayName("Deve calcular distância Manhattan com coordenadas negativas")
    void testCalcularDistanciaManhattanNegativas(){
        Coordenada p1 = new Coordenada(5, 5);
        Coordenada p2 = new Coordenada(-3, 2);

        double distancia = rotaService.calcularDistanciaManhattan(p1, p2);

        assertEquals(11.0, distancia);
    }

    @Test
    @DisplayName("Deve calcular distância total de uma rota")
    void testCalcularDistanciaTotal(){
        Coordenada inicio = new Coordenada(0, 0);
        List<Pedido> rota = Arrays.asList(pedido1, pedido2); // (10,20) -> (15,25)

        double distanciaTotal = rotaService.calcularDistanciaTotal(inicio, rota);

        assertEquals(40.0, distanciaTotal);
    }

    @Test
    @DisplayName("Deve retornar 0 para distância entre pontos iguais")
    void testCalcularDistanciaZero(){
        Coordenada p1 = new Coordenada(5, 5);
        Coordenada p2 = new Coordenada(5, 5);

        double distancia = rotaService.calcularDistanciaManhattan(p1, p2);

        assertEquals(0.0, distancia);
    }

    @Test
    @DisplayName("Deve otimizar rota com apenas um pedido")
    void testOtimizarRotaUmPedido(){
        List<Pedido> pedidos = Arrays.asList(pedido1);
        List<Pedido> rotaOtimizada = rotaService.otimizarRota(pedidos);

        assertEquals(1, rotaOtimizada.size());
        assertEquals(pedido1.getId(), rotaOtimizada.get(0).getId());
    }

    @Test
    @DisplayName("Deve encontrar drone com exata capacidade necessária")
    void testBuscarDroneCapacidadeExata(){
        List<Pedido> pedidos = Arrays.asList(pedido1);
        double pesoTotal = 1.5;
        
        when(droneService.buscarDronesPorCapacidade(pesoTotal))
            .thenReturn(Arrays.asList(new DroneResponseDTO(droneB)));

        Drone resultado = rotaService.buscarDroneIdeal(pesoTotal, pedidos);

        assertEquals(droneB.getId(), resultado.getId());
    }

}
