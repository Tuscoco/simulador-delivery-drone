package com.testedti.dronedelivery.service;

import static org.junit.jupiter.api.Assertions.*;
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

import com.testedti.dronedelivery.exception.exceptions.DroneNaoEncontradoException;
import com.testedti.dronedelivery.models.dtos.request.DroneRequestDTO;
import com.testedti.dronedelivery.models.dtos.response.DroneResponseDTO;
import com.testedti.dronedelivery.models.entity.Coordenada;
import com.testedti.dronedelivery.models.entity.Drone;
import com.testedti.dronedelivery.models.enums.EstadoDrone;
import com.testedti.dronedelivery.repository.DroneRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do DroneService")
class DroneServiceTest {

    @Mock
    private DroneRepository droneRepository;

    @InjectMocks
    private DroneService droneService;

    private Drone drone;
    private DroneRequestDTO droneRequestDTO;

    @BeforeEach
    void setUp() {
        drone = new Drone();
        drone.setId(1L);
        drone.setModelo("DJI Phantom 4");
        drone.setCapacidadeCarga(5.0);
        drone.setAutonomiaVoo(30.0);
        drone.setAutonomiaAtual(30.0);
        drone.setEstado(EstadoDrone.DISPONIVEL);
        drone.setCoordenadaAtual(new Coordenada(0, 0));

        droneRequestDTO = new DroneRequestDTO("DJI Phantom 4", 5.0, 30.0);
    }

    @Test
    @DisplayName("Deve registrar um novo drone")
    void testRegistrarDrone() {
        // Arrange
        when(droneRepository.save(any(Drone.class))).thenReturn(drone);

        // Act
        DroneResponseDTO resultado = droneService.registrarDrone(droneRequestDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals(drone.getModelo(), resultado.drone().getModelo());
        verify(droneRepository, times(1)).save(any(Drone.class));
    }

    @Test
    @DisplayName("Deve listar todos os drones")
    void testListarTodosDrones() {
        // Arrange
        Drone drone2 = new Drone();
        drone2.setId(2L);
        drone2.setModelo("DJI Air 3S");
        
        when(droneRepository.findAll()).thenReturn(Arrays.asList(drone, drone2));

        // Act
        List<DroneResponseDTO> resultado = droneService.listarTodosDrones();

        // Assert
        assertEquals(2, resultado.size());
        verify(droneRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar drone por ID")
    void testBuscarDronePorId() {
        // Arrange
        when(droneRepository.findById(1L)).thenReturn(Optional.of(drone));

        // Act
        DroneResponseDTO resultado = droneService.buscarDronePorId(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(drone.getId(), resultado.drone().getId());
        verify(droneRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando drone não encontrado")
    void testBuscarDroneNaoEncontrado() {
        // Arrange
        when(droneRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(DroneNaoEncontradoException.class, () -> 
            droneService.buscarDronePorId(999L)
        );
    }

    @Test
    @DisplayName("Deve atualizar drone")
    void testAtualizarDrone() {
        // Arrange
        DroneRequestDTO atualizacao = new DroneRequestDTO("DJI Phantom 4 Pro", 6.0, 35.0);
        
        when(droneRepository.findById(1L)).thenReturn(Optional.of(drone));
        when(droneRepository.save(any(Drone.class))).thenReturn(drone);

        // Act
        DroneResponseDTO resultado = droneService.atualizarDrone(1L, atualizacao);

        // Assert
        assertNotNull(resultado);
        verify(droneRepository, times(1)).findById(1L);
        verify(droneRepository, times(1)).save(any(Drone.class));
    }

    @Test
    @DisplayName("Deve deletar drone")
    void testDeletarDrone() {
        // Arrange
        when(droneRepository.findById(1L)).thenReturn(Optional.of(drone));

        // Act
        droneService.deletarDrone(1L);

        // Assert
        verify(droneRepository, times(1)).findById(1L);
        verify(droneRepository, times(1)).delete(drone);
    }

    @Test
    @DisplayName("Deve listar drones disponíveis")
    void testBuscarDronesDisponiveis() {
        // Arrange
        Drone droneEmVoo = new Drone();
        droneEmVoo.setId(2L);
        droneEmVoo.setEstado(EstadoDrone.EM_VOO);
        
        when(droneRepository.findByEstado(EstadoDrone.DISPONIVEL))
            .thenReturn(Arrays.asList(drone));

        // Act
        List<DroneResponseDTO> resultado = droneService.buscarDronesDisponiveis();

        // Assert
        assertEquals(1, resultado.size());
        verify(droneRepository, times(1)).findByEstado(EstadoDrone.DISPONIVEL);
    }

    @Test
    @DisplayName("Deve buscar drones por capacidade")
    void testBuscarDronesPorCapacidade() {
        // Arrange
        when(droneRepository.findByCapacidadeGreaterThanOrEqual(3.0))
            .thenReturn(Arrays.asList(drone));

        // Act
        List<DroneResponseDTO> resultado = droneService.buscarDronesPorCapacidade(3.0);

        // Assert
        assertEquals(1, resultado.size());
        verify(droneRepository, times(1)).findByCapacidadeGreaterThanOrEqual(3.0);
    }

    @Test
    @DisplayName("Deve atualizar posição do drone")
    void testAtualizarPosicaoDrone() {
        // Arrange
        when(droneRepository.findById(1L)).thenReturn(Optional.of(drone));
        when(droneRepository.save(any(Drone.class))).thenReturn(drone);

        // Act
        DroneResponseDTO resultado = droneService.atualizarPosicaoDrone(1L, 10, 20);

        // Assert
        assertNotNull(resultado);
        verify(droneRepository, times(1)).findById(1L);
        verify(droneRepository, times(1)).save(any(Drone.class));
    }

    @Test
    @DisplayName("Deve atualizar autonomia do drone")
    void testAtualizarAutonomia() {
        // Arrange
        when(droneRepository.findById(1L)).thenReturn(Optional.of(drone));
        when(droneRepository.save(any(Drone.class))).thenReturn(drone);

        // Act
        DroneResponseDTO resultado = droneService.atualizarAutonomia(1L, 5.0);

        // Assert
        assertNotNull(resultado);
        verify(droneRepository, times(1)).findById(1L);
        verify(droneRepository, times(1)).save(any(Drone.class));
    }

    @Test
    @DisplayName("Deve atualizar status do drone")
    void testAtualizarStatusDrone() {
        // Arrange
        when(droneRepository.findById(1L)).thenReturn(Optional.of(drone));
        when(droneRepository.save(any(Drone.class))).thenReturn(drone);

        // Act
        DroneResponseDTO resultado = droneService.atualizarStatusDrone(1L, EstadoDrone.EM_VOO);

        // Assert
        assertNotNull(resultado);
        verify(droneRepository, times(1)).findById(1L);
        verify(droneRepository, times(1)).save(any(Drone.class));
    }

}
