package com.wilker.order.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.wilker.order.infrastructure.dto.in.ItemRequestDTOFixture;
import com.wilker.order.infrastructure.dto.in.PedidoRequestDTOFixture;
import com.wilker.order.infrastructure.dto.out.ItemResponseDTOFixture;
import com.wilker.order.infrastructure.dto.out.PedidoResponseDTOFixture;
import com.wilker.order.infrastrucuture.dto.in.ItemRequestDTO;
import com.wilker.order.infrastrucuture.dto.in.PedidoRequestDTO;
import com.wilker.order.infrastrucuture.dto.out.ItemResponseDTO;
import com.wilker.order.infrastrucuture.dto.out.PedidoResponseDTO;
import com.wilker.order.infrastrucuture.enums.StatusEnum;
import com.wilker.order.infrastrucuture.exception.GlobalExceptionHandler;
import com.wilker.order.infrastrucuture.exception.ResourceNotFoundException;
import com.wilker.order.infrastrucuture.exception.StatusException;
import com.wilker.order.service.PedidoService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class PedidoControllerTest {

    @Mock
    private PedidoService pedidoService;

    @InjectMocks
    private PedidoController pedidoController;

    private ItemRequestDTO itemRequestDTO;
    private PedidoRequestDTO pedidoRequestDTO;
    private ItemResponseDTO itemResponseDTO;
    private PedidoResponseDTO pedidoResponseDTO;
    private List<PedidoResponseDTO> pedidoResponseDTOList;

    private MockMvc mockMvc;
    private final Long PEDIDO_ID = 1L;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @BeforeEach
    void setup() throws JsonProcessingException {

        mockMvc = MockMvcBuilders.standaloneSetup(pedidoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .alwaysDo(print())
                .build();

        itemRequestDTO = ItemRequestDTOFixture.build(
                "Nootbook Gamer",
                2,
                new BigDecimal("1000.00"));

        pedidoRequestDTO = PedidoRequestDTOFixture.build(
                "Marcos Sampaio",
               Set.of(itemRequestDTO));

        itemResponseDTO = ItemResponseDTOFixture.build(
                1L,
                "Nootbook Gamer",
                2,
                new BigDecimal("1000.00"),
                new BigDecimal("2000.00"));

        pedidoResponseDTO = PedidoResponseDTOFixture.build(
                1L,
                "Marcos Sampaio",
                StatusEnum.CRIADO,
                new BigDecimal("2000.00"),
                LocalDateTime.now(),
                Set.of(itemResponseDTO));

        pedidoResponseDTOList = List.of(pedidoResponseDTO);

    }


    @Test
    void registrarPedido_comDadosValidos_deveRetonar200Ok() throws Exception {

        when(pedidoService.criarPedido(pedidoRequestDTO)).thenReturn(pedidoResponseDTO);

        mockMvc.perform(post("/order")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pedidoRequestDTO))

        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(PEDIDO_ID));

    }

    @Test
    void registrarPedido_SemNomeDoCliente_deveRetornar400BadRequest() throws Exception {

        PedidoRequestDTO pedidoInvalido = PedidoRequestDTOFixture.build(null, Set.of(itemRequestDTO));

        mockMvc.perform(post("/order")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pedidoInvalido))

                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.clienteNome").value("O nome do cliente é obrigatório"));

        verifyNoMoreInteractions(pedidoService);
    }

    @ParameterizedTest
    @MethodSource("gerarCenariosDeErro")
    void buscarPedidosComdDadosInvalidos_deveRetornar400BadRequest(PedidoRequestDTO dtoInvalido) throws Exception {

        mockMvc.perform(post("/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest());

        verifyNoMoreInteractions(pedidoService);
    }

    static Stream<PedidoRequestDTO> gerarCenariosDeErro() {
        return Stream.of(
                // Nome nulo
                PedidoRequestDTOFixture.build(null, Set.of(ItemRequestDTOFixture.build("Nootbook Gamer", 2, new BigDecimal("1000.00")))),

                // Nome muito curto (viola o @Size(min=10))
                PedidoRequestDTOFixture.build("João", Set.of(ItemRequestDTOFixture.build("Nootbook Gamer", 2, new BigDecimal("1000.00")))),

                // Lista de itens vazia (viola o @NotEmpty)
                PedidoRequestDTOFixture.build("Marcos Sampaio de Oliveira", Set.of()),

                // Produto nulo(viola o @NotBlank)
                PedidoRequestDTOFixture.build("João", Set.of(ItemRequestDTOFixture.build(null, 2, new BigDecimal("1000.00")))),

                // Nome do produto muito curto (viola o @Size(min=10))
                PedidoRequestDTOFixture.build("João", Set.of(ItemRequestDTOFixture.build("Curto", 2, new BigDecimal("1000.00")))),

                // Quantidade null (viola @NotNull)
                PedidoRequestDTOFixture.build("João", Set.of(ItemRequestDTOFixture.build("Curto", null, new BigDecimal("1000.00")))),

                //Quantidade 0 (viola @Min))
                PedidoRequestDTOFixture.build("João", Set.of(ItemRequestDTOFixture.build("Curto", null, new BigDecimal("1000.00")))),

                // Preço unitário null (viola @NotNull)
                PedidoRequestDTOFixture.build("João", Set.of(ItemRequestDTOFixture.build("Curto", null, null))));
    }

    @Test
    void listarPedidos_deveRetornar200Ok() throws Exception {

        when(pedidoService.buscaPedidos()).thenReturn(pedidoResponseDTOList);

        mockMvc.perform(get("/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)

                ).andExpect(status().isOk());

        verifyNoMoreInteractions(pedidoService);
    }

    @Test
    void buscarPedidoPeloId_deveRetornar200ok() throws Exception {
        Long id = 1L;

        when(pedidoService.buscarPedidoPeloId(id)).thenReturn(pedidoResponseDTO);

        mockMvc.perform(get("/order/{id}" , id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pedidoRequestDTO))
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"));

        verify(pedidoService).buscarPedidoPeloId(id);
        verifyNoMoreInteractions(pedidoService);
    }

    @Test
    void buscarPedidoInexistente_deveRetornar40NotFound() throws Exception {
        Long idInexistente = 1123L;

        when(pedidoService.buscarPedidoPeloId(idInexistente)).thenThrow(new ResourceNotFoundException("O pedido " + idInexistente + " não foi encontrado"));

        mockMvc.perform(get("/order/{id}" , idInexistente)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isNotFound());

        verify(pedidoService).buscarPedidoPeloId(idInexistente);
        verifyNoMoreInteractions(pedidoService);
    }


    @Test
    void atualizarStatusValido_deveRetonar200ok() throws Exception {
        Long id = 1L;

        when(pedidoService.atualizaStatusPedido(id,StatusEnum.PAGO)).thenReturn(PedidoResponseDTOFixture.build(
                1L,
                "Marcos Sampaio",
                StatusEnum.PAGO,
                new BigDecimal("2000.00"),
                LocalDateTime.now(),
                Set.of(itemResponseDTO)));

        mockMvc.perform(put ("/order/{id}/status", id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .param("status", StatusEnum.PAGO.name())
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.statusEnum").value("PAGO"));


        verify(pedidoService).atualizaStatusPedido(id, StatusEnum.PAGO);
        verifyNoMoreInteractions(pedidoService);
    }

    @Test
    void atualizarStatusInvalido_deveRetonar200ok() throws Exception {
        Long id = 1L;

        when(pedidoService.atualizaStatusPedido(id,StatusEnum.PAGO)).thenReturn(PedidoResponseDTOFixture.build(
                1L,
                "Marcos Sampaio",
                StatusEnum.PAGO,
                new BigDecimal("2000.00"),
                LocalDateTime.now(),
                Set.of(itemResponseDTO)));

        mockMvc.perform(put ("/order/{id}/status", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("status", StatusEnum.PAGO.name())
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.statusEnum").value("PAGO"));


        verify(pedidoService).atualizaStatusPedido(id, StatusEnum.PAGO);
        verifyNoMoreInteractions(pedidoService);
    }

    @Test
    void atualizarPedidoComStatusCancelado_deveRetornar400BadRequest() throws Exception {
        Long id = 1L;

        when(pedidoService.atualizaStatusPedido(id,StatusEnum.PAGO)).thenThrow(new StatusException("Não é possível alterar o status de pedidos cancelados"));

        mockMvc.perform(put ("/order/{id}/status", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("status", StatusEnum.PAGO.name())
                ).andExpect(status().isBadRequest());

        verify(pedidoService).atualizaStatusPedido(id, StatusEnum.PAGO);
        verifyNoMoreInteractions(pedidoService);

    }

    @Test
    void atualizarPedidoComTransicaoDeStatusInvalida_deveRetornar400BadRequest() throws Exception {
        Long id = 1L;

        when(pedidoService.atualizaStatusPedido(id,StatusEnum.PAGO)).thenThrow(new StatusException("Transição de status inválida"));

        mockMvc.perform(put ("/order/{id}/status", id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .param("status", StatusEnum.PAGO.name())
        ).andExpect(status().isBadRequest());

        verify(pedidoService).atualizaStatusPedido(id, StatusEnum.PAGO);
        verifyNoMoreInteractions(pedidoService);

    }

    @Test
    void deletarPedidoValido_deveRetornar204NoContent() throws Exception {
        Long id = 1L;

        doNothing().when(pedidoService).deletaPedidoPeloId(id);

        mockMvc.perform(delete("/order/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent());

        verify(pedidoService).deletaPedidoPeloId(id);
        verifyNoMoreInteractions(pedidoService);

    }

    @Test
    void deletarPedidoInValido_deveRetornar400NotFound() throws Exception {
        Long idInexistente = 112312L;

        doThrow(new ResourceNotFoundException("O pedido " + idInexistente + " não foi encontrado"))
                .when(pedidoService).deletaPedidoPeloId(idInexistente);

        mockMvc.perform(delete("/order/{id}", idInexistente)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());

        verify(pedidoService).deletaPedidoPeloId(idInexistente);
        verifyNoMoreInteractions(pedidoService);

    }


}
