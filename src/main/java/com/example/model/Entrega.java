package com.example.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "entrega")
public class Entrega {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_entrega;

    @OneToOne
    @JoinColumn(name = "id_pedido", nullable = false, unique = true)
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "id_motorizado", nullable = false)
    private Usuario motorizado;

    private LocalDateTime fecha_asignacion;
    private LocalDateTime fecha_entrega;
    private int estado;
    private BigDecimal costo_delivery;
    private String observacion;

    public Entrega() {
        super();
    }

    public Entrega(Long id_entrega, Pedido pedido, Usuario motorizado, LocalDateTime fecha_asignacion,
            LocalDateTime fecha_entrega, int estado, BigDecimal costo_delivery, String observacion) {
        super();
        this.id_entrega = id_entrega;
        this.pedido = pedido;
        this.motorizado = motorizado;
        this.fecha_asignacion = fecha_asignacion;
        this.fecha_entrega = fecha_entrega;
        this.estado = estado;
        this.costo_delivery = costo_delivery;
        this.observacion = observacion;
    }

    public Long getId_entrega() {
        return id_entrega;
    }

    public void setId_entrega(Long id_entrega) {
        this.id_entrega = id_entrega;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Usuario getMotorizado() {
        return motorizado;
    }

    public void setMotorizado(Usuario motorizado) {
        this.motorizado = motorizado;
    }

    public LocalDateTime getFecha_asignacion() {
        return fecha_asignacion;
    }

    public void setFecha_asignacion(LocalDateTime fecha_asignacion) {
        this.fecha_asignacion = fecha_asignacion;
    }

    public LocalDateTime getFecha_entrega() {
        return fecha_entrega;
    }

    public void setFecha_entrega(LocalDateTime fecha_entrega) {
        this.fecha_entrega = fecha_entrega;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public BigDecimal getCosto_delivery() {
        return costo_delivery;
    }

    

    public void setCosto_delivery(BigDecimal costo_delivery) {
		this.costo_delivery = costo_delivery;
	}

	public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}
