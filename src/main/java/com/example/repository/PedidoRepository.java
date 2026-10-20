package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.model.Pedido;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long>{
	List<Pedido> findByEstadoIgnoreCase(String estado);

    @Query("SELECT p FROM Pedido p WHERE p.usuario.id_usuario = :idUsuario")
    List<Pedido> listarPorUsuario(@Param("idUsuario") Long idUsuario);

}
