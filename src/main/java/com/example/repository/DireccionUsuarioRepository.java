package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.model.DireccionUsuario;


@Repository
public interface DireccionUsuarioRepository  extends JpaRepository<DireccionUsuario, Long>{
	
	
	@Query("SELECT d FROM DireccionUsuario d WHERE d.usuario.id_usuario = :idUsuario")
    List<DireccionUsuario> listarPorUsuario(@Param("idUsuario") Long idUsuario);
}
