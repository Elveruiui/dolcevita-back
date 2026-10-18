package com.example.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="direccion_usuario")
public class DireccionUsuario {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_direccion;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    private String direccion;

    public DireccionUsuario() {
        super();
    }

    public DireccionUsuario(Long id_direccion, Usuario usuario, String direccion) {
        super();
        this.id_direccion = id_direccion;
        this.usuario = usuario;
        this.direccion = direccion;
    }

    public Long getId_direccion() {
        return id_direccion;
    }

    public void setId_direccion(Long id_direccion) {
        this.id_direccion = id_direccion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

}
