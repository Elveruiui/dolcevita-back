package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.model.Producto;

@Repository
public interface ProductoRepository  extends JpaRepository<Producto, Long>{
	List<Producto> findByNombre(String nombre);
	
	@Query("SELECT p FROM Producto p WHERE p.categoria.id_categoria = :idCategoria")
	List<Producto> listarporCategoria(@Param("idCategoria") Long idCategoria);

}
