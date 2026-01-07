package com.wilker.order.infrastrucuture.repository;

import com.wilker.order.infrastrucuture.entity.PedidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<PedidoEntity, Long> {

    Optional<PedidoEntity> findById(Long id);



    void deleteById(Long id);
}
