package com.wilker.order.controller;

import com.wilker.order.infrastrucuture.dto.in.PedidoRequestDTO;
import com.wilker.order.infrastrucuture.dto.out.PedidoResponseDTO;
import com.wilker.order.infrastrucuture.enums.StatusEnum;
import com.wilker.order.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<PedidoResponseDTO> criarPedido(@Valid @RequestBody PedidoRequestDTO pedidoRequestDTO){
        return ResponseEntity.ok(pedidoService.criarPedido(pedidoRequestDTO));
    }

    @GetMapping
    public ResponseEntity<List<PedidoResponseDTO>> listaPedidos(){
        return ResponseEntity.ok(pedidoService.buscaPedidos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> buscaPedidoPeloId(@PathVariable Long id){
        return ResponseEntity.ok(pedidoService.buscarPedidoPeloId(id));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<PedidoResponseDTO> atualizaStatus (@PathVariable Long id, @RequestParam("status") StatusEnum statusEnum){
        return ResponseEntity.ok(pedidoService.atualizaStatusPedido(id, statusEnum));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPedido(@PathVariable Long id){
         pedidoService.deletaPedidoPeloId(id);
         return ResponseEntity.noContent().build();
    }


}
