package com.example.repository;

import java.util.List;
<<<<<<< HEAD
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
=======

import org.springframework.data.jpa.repository.JpaRepository;
>>>>>>> 7a8f0bed507ec3a77a98c71f15337719b7fed49e
import org.springframework.stereotype.Repository;

import com.example.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
	List<Usuario> findByNombre(String nombre);
<<<<<<< HEAD
	
	@Query("SELECT u FROM Usuario u WHERE LOWER(u.correo) = LOWER(:correo)")
    Optional<Usuario> buscarPorCorreo(@Param("correo") String correo);
=======
>>>>>>> 7a8f0bed507ec3a77a98c71f15337719b7fed49e

}
