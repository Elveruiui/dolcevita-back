package com.example.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="rol")
public class Rol {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_rol;
    private String nombre_rol;
    private int estado;

    public Rol() {
        super();
    }

    public Rol(Long id_rol, String nombre_rol, int estado) {
        super();
        this.id_rol = id_rol;
        this.nombre_rol = nombre_rol;
        this.estado = estado;
    }

    public Long getId_rol() {
        return id_rol;
    }

    public void setId_rol(Long id_rol) {
        this.id_rol = id_rol;
    }

    public String getNombre_rol() {
        return nombre_rol;
    }

    public void setNombre_rol(String nombre_rol) {
        this.nombre_rol = nombre_rol;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

}
