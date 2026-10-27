package com.example.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.model.DetallePedido;
import com.example.model.Pedido;
import com.example.model.Producto;
import com.example.repository.DetallePedidoRepository;
import com.example.repository.PedidoRepository;
import com.example.repository.ProductoRepository;

@Service
public class PedidoService {
	

	@Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private DetallePedidoRepository detallePedidoRepository;

	    public List<Pedido> listarAll() {
	        return pedidoRepository.findAll();
	    }

	    public List<Pedido> listarPorEstado(String estado) {
	        return pedidoRepository.findByEstadoIgnoreCase(estado);
	    }

	    public List<Pedido> listarPorUsuario(Long idUsuario) {
	        return pedidoRepository.listarPorUsuario(idUsuario);
	    }

	    public Pedido listarID(Long id) {
	        return pedidoRepository.findById(id).orElse(null);
	    }


	    @Transactional
	    public Pedido guardar(Pedido pedido) throws Exception {
	        if (pedido.getDetalles() == null || pedido.getDetalles().isEmpty()) {
	            throw new Exception("El pedido debe incluir al menos un producto en el detalle.");
	        }

	        pedido.setFecha_pedido(LocalDateTime.now());
	        pedido.setEstado("PENDIENTE");

	        BigDecimal totalCalculado = BigDecimal.ZERO;

	        for (DetallePedido detalle : pedido.getDetalles()) {
	            Long idProd = detalle.getProducto().getId_producto();
	            Producto productoBD = productoRepository.findById(idProd)
	                    .orElseThrow(() -> new Exception("Producto no encontrado con ID: " + idProd));

	            if (detalle.getCantidad() <= 0) {
	                throw new Exception("La cantidad para " + productoBD.getNombre() + " debe ser mayor a 0.");
	            }

	            
	            if (productoBD.getStock() < detalle.getCantidad()) {
	                throw new Exception("Stock insuficiente para el producto: " + productoBD.getNombre()
	                        + " (Stock disponible: " + productoBD.getStock() + ")");
	            }

	          
	            productoBD.setStock(productoBD.getStock() - detalle.getCantidad());
	            productoRepository.save(productoBD);

	            detalle.setProducto(productoBD);
	            
	       
	            BigDecimal precioUnitario = new BigDecimal(productoBD.getPrecio());
	            detalle.setPrecio_unitario(precioUnitario);

	            BigDecimal subtotal = precioUnitario.multiply(new BigDecimal(detalle.getCantidad()));
	            detalle.setSubtotal(subtotal);
	            detalle.setPedido(pedido);

	            totalCalculado = totalCalculado.add(subtotal);
	        }

	        pedido.setTotal(totalCalculado);
	        return pedidoRepository.save(pedido);
	    }

	   
	    @Transactional
	    public Pedido actualizarEstado(Long id, String nuevoEstado) throws Exception {
	        Pedido pedido = pedidoRepository.findById(id)
	                .orElseThrow(() -> new Exception("Pedido no encontrado con ID: " + id));

	        String estadoActual = pedido.getEstado();
	        String nuevoEstadoUpper = nuevoEstado.toUpperCase();

	     
	        if ("CANCELADO".equals(nuevoEstadoUpper) && !"CANCELADO".equalsIgnoreCase(estadoActual)) {
	            devolverStockProductos(pedido);
	        }

	        pedido.setEstado(nuevoEstadoUpper);
	        return pedidoRepository.save(pedido);
	    }

	    
	    @Transactional
	    public Pedido actualizarDetallesYFecha(Long id, Pedido detallesNuevos) throws Exception {
	        Pedido pedidoExistente = pedidoRepository.findById(id)
	                .orElseThrow(() -> new Exception("Pedido no encontrado con ID: " + id));

	        if (!"PENDIENTE".equalsIgnoreCase(pedidoExistente.getEstado())) {
	            throw new Exception("No se pueden modificar los productos porque el pedido ya está en estado: " + pedidoExistente.getEstado());
	        }

	        if (detallesNuevos.getFecha_entrega() != null) {
	            pedidoExistente.setFecha_entrega(detallesNuevos.getFecha_entrega());
	        }
	        if (detallesNuevos.getObservacion() != null) {
	            pedidoExistente.setObservacion(detallesNuevos.getObservacion());
	        }

	        if (detallesNuevos.getDetalles() != null && !detallesNuevos.getDetalles().isEmpty()) {
	            
	            
	            devolverStockProductos(pedidoExistente);
	            detallePedidoRepository.deleteAll(pedidoExistente.getDetalles());
	            pedidoExistente.getDetalles().clear();

	            BigDecimal totalCalculado = BigDecimal.ZERO;

	            for (DetallePedido nuevoDetalle : detallesNuevos.getDetalles()) {
	                Long idProd = nuevoDetalle.getProducto().getId_producto();
	                Producto productoBD = productoRepository.findById(idProd)
	                        .orElseThrow(() -> new Exception("Producto no encontrado con ID: " + idProd));

	                if (nuevoDetalle.getCantidad() <= 0) {
	                    throw new Exception("La cantidad debe ser mayor a 0.");
	                }

	                if (productoBD.getStock() < nuevoDetalle.getCantidad()) {
	                    throw new Exception("Stock insuficiente para: " + productoBD.getNombre()
	                            + " (Disponible: " + productoBD.getStock() + ")");
	                }

	                
	                productoBD.setStock(productoBD.getStock() - nuevoDetalle.getCantidad());
	                productoRepository.save(productoBD);

	                nuevoDetalle.setProducto(productoBD);
	                
	                BigDecimal precioUnitario = new BigDecimal(productoBD.getPrecio());
	                nuevoDetalle.setPrecio_unitario(precioUnitario);
	                
	                BigDecimal subtotal = precioUnitario.multiply(new BigDecimal(nuevoDetalle.getCantidad()));
	                nuevoDetalle.setSubtotal(subtotal);
	                nuevoDetalle.setPedido(pedidoExistente);

	                pedidoExistente.getDetalles().add(nuevoDetalle);
	                totalCalculado = totalCalculado.add(subtotal);
	            }

	            pedidoExistente.setTotal(totalCalculado);
	        }

	        return pedidoRepository.save(pedidoExistente);
	    }

	    
	    @Transactional
	    public boolean eliminar(Long id) {
	        Optional<Pedido> optional = pedidoRepository.findById(id);
	        if (optional.isPresent()) {
	            Pedido pedido = optional.get();
	            
	            if (!"CANCELADO".equalsIgnoreCase(pedido.getEstado())) {
	                devolverStockProductos(pedido);
	            }
	            pedidoRepository.deleteById(id);
	            return true;
	        }
	        return false;
	    }

	    
	    private void devolverStockProductos(Pedido pedido) {
	        if (pedido.getDetalles() != null) {
	            for (DetallePedido detalle : pedido.getDetalles()) {
	                Producto producto = detalle.getProducto();
	                if (producto != null) {
	                    producto.setStock(producto.getStock() + detalle.getCantidad());
	                    productoRepository.save(producto);
	                }
	            }
	        }
	    }

}
