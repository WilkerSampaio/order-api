package com.wilker.order.controller;

import com.wilker.order.infrastrucuture.annotations.ApiOrderResponses;
import com.wilker.order.infrastrucuture.dto.in.PedidoRequestDTO;
import com.wilker.order.infrastrucuture.dto.out.PedidoResponseDTO;
import com.wilker.order.infrastrucuture.enums.StatusEnum;
import com.wilker.order.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
@Tag(name = "Order", description = "Gerenciamento de pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    @ApiOrderResponses
    @Operation(summary = "Cria pedido", description = "Cria um novo pedido")
    public ResponseEntity<PedidoResponseDTO> criarPedido(@Valid @RequestBody PedidoRequestDTO pedidoRequestDTO){
        return ResponseEntity.ok(pedidoService.criarPedido(pedidoRequestDTO));
    }

    @GetMapping
    @ApiOrderResponses
    @Operation(summary = "Lista pedidos", description = "Lista todos os pedidos")
    public ResponseEntity<List<PedidoResponseDTO>> listaPedidos(){
        return ResponseEntity.ok(pedidoService.buscaPedidos());
    }

    @GetMapping("/{id}")
    @ApiOrderResponses
    @Operation(summary = "Busca pedido", description = "Busca pedido pelo ID")
    public ResponseEntity<PedidoResponseDTO> buscaPedidoPeloId(@PathVariable Long id){
        return ResponseEntity.ok(pedidoService.buscarPedidoPeloId(id));
    }

    @PutMapping("/{id}/status")
    @ApiOrderResponses
    @Operation(summary = "Atualiza Status", description = "Atualiza status do pedido")
    public ResponseEntity<PedidoResponseDTO> atualizaStatus (@PathVariable Long id, @RequestParam("status") StatusEnum statusEnum){
        return ResponseEntity.ok(pedidoService.atualizaStatusPedido(id, statusEnum));
    }

    @DeleteMapping("/{id}")
    @ApiOrderResponses
    @Operation(summary = "Deleta pedido", description = "Deleta pedido pelo ID")
    public ResponseEntity<Void> deletarPedido(@PathVariable Long id){
         pedidoService.deletaPedidoPeloId(id);
         return ResponseEntity.noContent().build();
    }


}
