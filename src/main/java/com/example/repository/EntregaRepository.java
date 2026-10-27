package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.model.Entrega;

@Repository
public interface EntregaRepository extends JpaRepository<Entrega, Long>{
	@Query("SELECT e FROM Entrega e WHERE LOWER(e.motorizado.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Entrega> buscarPorNombreMotorizado(@Param("nombre") String nombre);

    @Query("SELECT e FROM Entrega e WHERE e.motorizado.id_usuario = :idMotorizado")
    List<Entrega> listarPorMotorizado(@Param("idMotorizado") Long idMotorizado);
}
