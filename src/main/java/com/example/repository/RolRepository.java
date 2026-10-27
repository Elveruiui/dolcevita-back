package com.example.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.model.Rol;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long>{
	@Query("SELECT r FROM Rol r WHERE LOWER(r.nombre_rol) = LOWER(:nombre)")
    Optional<Rol> buscarPorNombre(@Param("nombre") String nombre);

}
