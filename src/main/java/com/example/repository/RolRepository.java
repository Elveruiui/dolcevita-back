package com.example.repository;

<<<<<<< HEAD
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
=======
import org.springframework.data.jpa.repository.JpaRepository;
>>>>>>> 7a8f0bed507ec3a77a98c71f15337719b7fed49e
import org.springframework.stereotype.Repository;

import com.example.model.Rol;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long>{
<<<<<<< HEAD
	@Query("SELECT r FROM Rol r WHERE LOWER(r.nombre_rol) = LOWER(:nombre)")
    Optional<Rol> buscarPorNombre(@Param("nombre") String nombre);
=======
>>>>>>> 7a8f0bed507ec3a77a98c71f15337719b7fed49e

}
