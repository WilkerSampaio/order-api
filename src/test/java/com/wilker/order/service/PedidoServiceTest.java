package com.wilker.order.service;


import com.wilker.order.infrastructure.dto.in.ItemRequestDTOFixture;
import com.wilker.order.infrastructure.dto.in.PedidoRequestDTOFixture;
import com.wilker.order.infrastructure.dto.out.PedidoResponseDTOFixture;
import com.wilker.order.infrastrucuture.dto.in.ItemRequestDTO;
import com.wilker.order.infrastrucuture.dto.in.PedidoRequestDTO;
import com.wilker.order.infrastrucuture.dto.out.PedidoResponseDTO;
import com.wilker.order.infrastrucuture.entity.ItemEntity;
import com.wilker.order.infrastrucuture.entity.PedidoEntity;
import com.wilker.order.infrastrucuture.enums.StatusEnum;
import com.wilker.order.infrastrucuture.exception.ResourceNotFoundException;
import com.wilker.order.infrastrucuture.exception.StatusException;
import com.wilker.order.infrastrucuture.mapper.PedidoMapperConverter;
import com.wilker.order.infrastrucuture.repository.PedidoRepository;
import com.wilker.order.service.PedidoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private PedidoMapperConverter pedidoMapperConverter;

    //Injetado manualmente por conta do clock
    private PedidoService pedidoService;

    private PedidoRequestDTO pedidoRequestDTO;
    private PedidoEntity pedidoEntity;
    private PedidoResponseDTO pedidoResponseDTO;
    private ItemEntity itemEntity;
    private ItemRequestDTO itemRequestDTO;
    private Clock clock;
    private LocalDateTime dataFixa;
    private List<PedidoEntity> pedidoEntityList;
    private List<PedidoResponseDTO> pedidoResponseDTOList;

    @BeforeEach
    void setup() {

        dataFixa = LocalDateTime.now();
        clock = Clock.fixed(dataFixa.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());

        pedidoService = new PedidoService(pedidoRepository, pedidoMapperConverter, clock); // Para garantir que o "agora" do Service seja o mesmo

        //Item Entity sem subTotal calculado
        itemEntity = ItemEntity.builder()
                .id(1L)
                .produtoNome("Notebook Gamer")
                .quantidade(2)
                .precoUnitario(new BigDecimal("1000.00"))
                .build();

        // Pedido Entity sem valorTotal calculado
        pedidoEntity = PedidoEntity.builder()
                .id(1L)
                .clienteNome("Marcos Sampaio")
                .statusEnum(StatusEnum.CRIADO)
                .itensEntity(Set.of(itemEntity))
                .build();

        itemRequestDTO = ItemRequestDTOFixture.build(
                "Notebook Gamer",
                2,
                new BigDecimal("1000.00"));

        pedidoRequestDTO = PedidoRequestDTOFixture.build(
                "Marcos Sampaio", Set.of(itemRequestDTO));

        pedidoResponseDTO = PedidoResponseDTOFixture.build(
                1L,
                "Marcos Sampaio",
                StatusEnum.CRIADO,
                new BigDecimal("2000.00"),
                LocalDateTime.now(),
                Set.of());


       pedidoEntityList = List.of(pedidoEntity);

       pedidoResponseDTOList = List.of(pedidoResponseDTO);
    }

    @Test
    void deveCriarPedidoComSucesso() {

        when(pedidoMapperConverter.paraPedidoEntity(pedidoRequestDTO)).thenReturn(pedidoEntity);
        when(pedidoRepository.save(any(PedidoEntity.class))).thenAnswer(i -> i.getArgument(0));
        when(pedidoMapperConverter.paraPedidoResponse(any(PedidoEntity.class))).thenReturn(pedidoResponseDTO);

        PedidoResponseDTO response = pedidoService.criarPedido(pedidoRequestDTO);

        assertEquals(pedidoResponseDTO, response);

        assertEquals(StatusEnum.CRIADO, pedidoEntity.getStatusEnum());
        assertEquals(dataFixa, pedidoEntity.getDataCriacao());

        assertEquals(new BigDecimal("2000.00"), itemEntity.getSubTotal());
        assertEquals(new BigDecimal("2000.00"), pedidoEntity.getValorTotal());
        assertEquals(pedidoEntity, itemEntity.getPedidoEntity());

        verify(pedidoMapperConverter).paraPedidoEntity(pedidoRequestDTO);
        verify(pedidoRepository).save(any(PedidoEntity.class));
        verify(pedidoMapperConverter).paraPedidoResponse(any(PedidoEntity.class));

        verifyNoMoreInteractions(pedidoMapperConverter, pedidoRepository);
    }

    @Test
    void deveBuscarTodosPedidosComSucesso(){

        when(pedidoRepository.findAll()).thenReturn(pedidoEntityList);
        when(pedidoMapperConverter.paraPedidoResponseList(pedidoEntityList)).thenReturn(pedidoResponseDTOList);

        List<PedidoResponseDTO> responseDTOList = pedidoService.buscaPedidos();

        assertEquals(1, responseDTOList.size());
        assertEquals(pedidoResponseDTOList.get(0), responseDTOList.get(0));

        verify(pedidoRepository).findAll();
        verify(pedidoMapperConverter).paraPedidoResponseList(pedidoEntityList);

        verifyNoMoreInteractions(pedidoRepository,pedidoMapperConverter);

    }

    @Test
    void deveLancarExcecaQuandoNaoEncontrarNenhumPedido(){

        List<PedidoEntity> listVazia = new ArrayList<>();

        when(pedidoRepository.findAll()).thenReturn(listVazia);

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class, ()->pedidoService.buscaPedidos());

        assertThat(e.getMessage(), is("Nenhum pedido foi encontrado"));

        verify(pedidoRepository).findAll();

        verifyNoMoreInteractions(pedidoRepository);
        verifyNoInteractions(pedidoMapperConverter);
    }

    @Test
    void deveBuscarPedidoPeloIdComSucesso(){

        Long id = 1L;

        when(pedidoRepository.findById(id)).thenReturn(Optional.of(pedidoEntity));
        when(pedidoMapperConverter.paraPedidoResponse(pedidoEntity)).thenReturn(pedidoResponseDTO);

        PedidoResponseDTO responseDTO = pedidoService.buscarPedidoPeloId(id);

        assertEquals(pedidoResponseDTO, responseDTO);

        verify(pedidoRepository).findById(id);
        verify(pedidoMapperConverter).paraPedidoResponse(pedidoEntity);

        verifyNoMoreInteractions(pedidoRepository, pedidoMapperConverter);
    }

    @Test
    void deveLancarExcecaoCasoIdInexistente(){

        Long idInexistente = 123123L;

        when(pedidoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class, () -> pedidoService.buscarPedidoPeloId(idInexistente));

        assertThat(e.getMessage(), is("O pedido " + idInexistente + " não foi encontrado"));

        verify(pedidoRepository).findById(idInexistente);
        verifyNoInteractions(pedidoMapperConverter);
    }

    @Test
    void deveAtualizarStatusDoPedidoComSucesso(){

        Long id = 1L;

        pedidoEntity.setStatusEnum(StatusEnum.CRIADO);

        when(pedidoRepository.findById(id)).thenReturn(Optional.of(pedidoEntity));
        when(pedidoRepository.save(any(PedidoEntity.class))).thenAnswer(i -> i.getArgument(0));
        when(pedidoMapperConverter.paraPedidoResponse(any(PedidoEntity.class))).thenReturn(PedidoResponseDTOFixture.build(
                1L,
                "Marcos Sampaio",
                StatusEnum.PAGO,
                new BigDecimal("2000.00"),
                LocalDateTime.now(),
                Set.of()));

        PedidoResponseDTO responseDTO = pedidoService.atualizaStatusPedido(id, StatusEnum.PAGO);

        assertEquals(StatusEnum.PAGO, responseDTO.statusEnum());
        assertEquals(StatusEnum.PAGO, pedidoEntity.getStatusEnum());

        verify(pedidoRepository).findById(id);
        verify(pedidoRepository).save(any(PedidoEntity.class));
        verify(pedidoMapperConverter).paraPedidoResponse(any(PedidoEntity.class));

       verifyNoMoreInteractions(pedidoRepository, pedidoMapperConverter);

    }

    @Test
    void deveLancarExcecaoCasoStatusAtualIgualCancelado(){
        Long id = 1L;

        pedidoEntity.setStatusEnum(StatusEnum.CANCELADO);

        when(pedidoRepository.findById(id)).thenReturn(Optional.of(pedidoEntity));

        StatusException e = assertThrows(StatusException.class, () -> pedidoService.atualizaStatusPedido(id, StatusEnum.PAGO));

        assertThat(e.getMessage(), is("Não é possível alterar o status de pedidos cancelados"));

        verify(pedidoRepository).findById(id);
        verifyNoMoreInteractions(pedidoRepository);
        verifyNoInteractions(pedidoMapperConverter);


    }

    @Test
    void deveLancarExcecaoCasoStatusSeguirFluxoInvalido(){
        Long id = 1L;

        when(pedidoRepository.findById(id)).thenReturn(Optional.of(pedidoEntity));

        StatusException e = assertThrows(StatusException.class, ()-> pedidoService.atualizaStatusPedido(id, StatusEnum.ENVIADO));

        assertThat(e.getMessage(), is("Transição inválida"));

        verify(pedidoRepository).findById(id);
        verifyNoMoreInteractions(pedidoRepository);
        verifyNoInteractions(pedidoMapperConverter);

    }

    @Test
    void deveDeletarPedidoComSucesso(){
        Long id = 1L;

        when(pedidoRepository.findById(id)).thenReturn(Optional.of(pedidoEntity));
        doNothing().when(pedidoRepository).deleteById(id);

        pedidoService.deletaPedidoPeloId(id);

        verify(pedidoRepository).findById(id);
        verify(pedidoRepository).deleteById(id);
        verifyNoMoreInteractions(pedidoRepository);


    }

}
