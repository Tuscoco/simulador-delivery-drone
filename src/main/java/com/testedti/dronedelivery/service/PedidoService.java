package com.testedti.dronedelivery.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.testedti.dronedelivery.exception.exceptions.PedidoNaoEncontradoException;
import com.testedti.dronedelivery.models.dtos.request.CriarPedidoRequestDTO;
import com.testedti.dronedelivery.models.dtos.response.BuscarPedidoDTO;
import com.testedti.dronedelivery.models.dtos.response.CriarPedidoResponseDTO;
import com.testedti.dronedelivery.models.entity.Coordenada;
import com.testedti.dronedelivery.models.entity.Pedido;
import com.testedti.dronedelivery.models.enums.PrioridadePedido;
import com.testedti.dronedelivery.repository.PedidoRepository;

@Service
public class PedidoService {
    
    private final PedidoRepository pedidoRepository;

    public PedidoService(PedidoRepository pedidoRepository){
        this.pedidoRepository = pedidoRepository;
    }

    public CriarPedidoResponseDTO criarPedido(CriarPedidoRequestDTO criarPedidoRequestDTO){
        Pedido pedido = new Pedido(criarPedidoRequestDTO);
        return new CriarPedidoResponseDTO(pedidoRepository.save(pedido));
    }

    public BuscarPedidoDTO buscarPedidoPorId(Long id){
        Pedido pedido = pedidoRepository.findById(id)
            .orElseThrow(() -> new PedidoNaoEncontradoException("Pedido de id " + id + " não encontrado!"));
        return new BuscarPedidoDTO(pedido);
    }

    public List<BuscarPedidoDTO> listarTodosPedidos(){
        return pedidoRepository.findAll()
            .stream()
            .map(BuscarPedidoDTO::new)
            .collect(Collectors.toList());
    }

    public List<BuscarPedidoDTO> listarPedidosPorPrioridade(PrioridadePedido prioridade){
        return pedidoRepository.findByPrioridade(prioridade)
            .stream()
            .map(BuscarPedidoDTO::new)
            .collect(Collectors.toList());
    }

    public List<Pedido> listarPedidosPorIds(List<Long> ids){
        List<Pedido> pedidos = pedidoRepository.findAllById(ids);
        if(pedidos.isEmpty()){
            throw new PedidoNaoEncontradoException("Nenhum pedido encontrado para os ids fornecidos!");
        }
        return pedidos;
    }

    public BuscarPedidoDTO atualizarPedido(Long id, CriarPedidoRequestDTO atualizacaoDTO){
        Pedido pedido = pedidoRepository.findById(id)
            .orElseThrow(() -> new PedidoNaoEncontradoException("Pedido de id " + id + " não encontrado!"));
        
        pedido.setPesoPacote(atualizacaoDTO.pesoPacote());
        pedido.setPrioridade(atualizacaoDTO.prioridade());
        pedido.setDescricao(atualizacaoDTO.descricao());
        pedido.setCoordenadaDestino(new Coordenada(
            atualizacaoDTO.coordenadaDestino().x(),
            atualizacaoDTO.coordenadaDestino().y()
        ));
        
        Pedido pedidoAtualizado = pedidoRepository.save(pedido);
        return new BuscarPedidoDTO(pedidoAtualizado);
    }

    public void deletarPedido(Long id){
        Pedido pedido = pedidoRepository.findById(id)
            .orElseThrow(() -> new PedidoNaoEncontradoException("Pedido de id " + id + " não encontrado!"));
        pedidoRepository.delete(pedido);
    }

    public void deletarTodosPedidos(){
        pedidoRepository.deleteAll();
    }

    public void salvarTodosPedidos(List<Pedido> pedidos){
        pedidoRepository.saveAll(pedidos);
    }

}
