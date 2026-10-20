package com.example.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


	

@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_pedido;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_direccion", nullable = false)
    private DireccionUsuario direccion;

    private LocalDateTime fecha_pedido;
    private LocalDateTime fecha_entrega;
    private String modalidad_entrega;
    private String estado;
    private BigDecimal total;
    private String observacion;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("pedido")
    private List<DetallePedido> detalles;

    public Pedido() {
        super();
    }

    public Pedido(Long id_pedido, Usuario usuario, DireccionUsuario direccion, LocalDateTime fecha_pedido,
            LocalDateTime fecha_entrega, String modalidad_entrega, String estado, BigDecimal total, String observacion) {
        super();
        this.id_pedido = id_pedido;
        this.usuario = usuario;
        this.direccion = direccion;
        this.fecha_pedido = fecha_pedido;
        this.fecha_entrega = fecha_entrega;
        this.modalidad_entrega = modalidad_entrega;
        this.estado = estado;
        this.total = total;
        this.observacion = observacion;
    }

    public Long getId_pedido() {
        return id_pedido;
    }

    public void setId_pedido(Long id_pedido) {
        this.id_pedido = id_pedido;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public DireccionUsuario getDireccion() {
        return direccion;
    }

    public void setDireccion(DireccionUsuario direccion) {
        this.direccion = direccion;
    }

    public LocalDateTime getFecha_pedido() {
        return fecha_pedido;
    }

    public void setFecha_pedido(LocalDateTime fecha_pedido) {
        this.fecha_pedido = fecha_pedido;
    }

    public LocalDateTime getFecha_entrega() {
        return fecha_entrega;
    }

    public void setFecha_entrega(LocalDateTime fecha_entrega) {
        this.fecha_entrega = fecha_entrega;
    }

    public String getModalidad_entrega() {
        return modalidad_entrega;
    }

    public void setModalidad_entrega(String modalidad_entrega) {
        this.modalidad_entrega = modalidad_entrega;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public List<DetallePedido> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetallePedido> detalles) {
        this.detalles = detalles;
    }
}
