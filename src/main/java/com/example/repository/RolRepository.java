package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.model.Rol;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long>{

}
