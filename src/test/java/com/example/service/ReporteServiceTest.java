package com.example.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.model.DireccionUsuario;
import com.example.model.Entrega;
import com.example.model.Pedido;
import com.example.model.Usuario;
import com.example.repository.EntregaRepository;
import com.example.repository.PedidoRepository;

@ExtendWith(MockitoExtension.class)
class ReporteServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private EntregaRepository entregaRepository;

    @InjectMocks
    private ReporteService reporteService;

    private Usuario crearUsuario(Long id, String nombre, String apellido) {
        Usuario usuario = new Usuario();
        usuario.setId_usuario(id);
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        return usuario;
    }

    private Pedido crearPedido(Long id, String estado, String total) {
        Pedido pedido = new Pedido();
        pedido.setId_pedido(id);
        pedido.setUsuario(crearUsuario(1L, "Lucia", "Paredes"));
        pedido.setModalidad_entrega("Delivery");
        pedido.setEstado(estado);
        pedido.setTotal(new BigDecimal(total));
        pedido.setFecha_pedido(LocalDateTime.of(2026, 10, 20, 10, 30));
        pedido.setFecha_entrega(LocalDateTime.of(2026, 10, 22, 15, 0));
        return pedido;
    }

    @Test
    void reportePedidos_creaLasHojasPedidosYResumen() throws Exception {
        when(pedidoRepository.findAll()).thenReturn(Arrays.asList(
                crearPedido(1L, "PENDIENTE", "100.00"), crearPedido(2L, "CANCELADO", "50.00")));

        XSSFWorkbook libro = new XSSFWorkbook(new ByteArrayInputStream(reporteService.generarReportePedidos()));

        assertEquals(2, libro.getNumberOfSheets());
        assertEquals("Pedidos", libro.getSheetName(0));
        assertEquals("Resumen", libro.getSheetName(1));
    }

    @Test
    void reportePedidos_escribeUnaFilaPorPedidoConSusDatos() throws Exception {
        when(pedidoRepository.findAll()).thenReturn(Arrays.asList(
                crearPedido(1L, "PENDIENTE", "100.00"), crearPedido(2L, "CANCELADO", "50.00")));

        XSSFWorkbook libro = new XSSFWorkbook(new ByteArrayInputStream(reporteService.generarReportePedidos()));
        Sheet hoja = libro.getSheet("Pedidos");

        assertEquals(2, hoja.getLastRowNum());
        assertEquals("Cliente", hoja.getRow(0).getCell(1).getStringCellValue());
        assertEquals("#DV-00002", hoja.getRow(1).getCell(0).getStringCellValue());
        assertEquals("#DV-00001", hoja.getRow(2).getCell(0).getStringCellValue());
        assertEquals("Lucia Paredes", hoja.getRow(2).getCell(1).getStringCellValue());
        assertEquals("Pendiente", hoja.getRow(2).getCell(5).getStringCellValue());
        assertEquals(100.0, hoja.getRow(2).getCell(6).getNumericCellValue(), 0.001);
    }

    @Test
    void reportePedidos_resumenCuentaPorEstadoYExcluyeCanceladosDeLasVentas() throws Exception {
        when(pedidoRepository.findAll()).thenReturn(Arrays.asList(
                crearPedido(1L, "PENDIENTE", "100.00"), crearPedido(2L, "CANCELADO", "50.00")));

        XSSFWorkbook libro = new XSSFWorkbook(new ByteArrayInputStream(reporteService.generarReportePedidos()));
        Sheet resumen = libro.getSheet("Resumen");

        assertEquals(1.0, resumen.getRow(1).getCell(1).getNumericCellValue(), 0.001);
        assertEquals(1.0, resumen.getRow(5).getCell(1).getNumericCellValue(), 0.001);
        assertEquals(2.0, resumen.getRow(6).getCell(1).getNumericCellValue(), 0.001);
        assertEquals(100.0, resumen.getRow(7).getCell(1).getNumericCellValue(), 0.001);
    }

    @Test
    void reportePedidos_sinPedidosSoloTieneLosTitulos() throws Exception {
        when(pedidoRepository.findAll()).thenReturn(new ArrayList<>());

        XSSFWorkbook libro = new XSSFWorkbook(new ByteArrayInputStream(reporteService.generarReportePedidos()));

        assertEquals(0, libro.getSheet("Pedidos").getLastRowNum());
        assertEquals(0.0, libro.getSheet("Resumen").getRow(7).getCell(1).getNumericCellValue(), 0.001);
    }

    @Test
    void reporteEntregas_escribeLosDatosDeLaEntrega() throws Exception {
        Pedido pedido = crearPedido(3L, "CONFIRMADO", "155.00");
        DireccionUsuario direccion = new DireccionUsuario();
        direccion.setDireccion("Av. Primavera 123");
        pedido.setDireccion(direccion);

        Entrega entrega = new Entrega();
        entrega.setId_entrega(1L);
        entrega.setPedido(pedido);
        entrega.setMotorizado(crearUsuario(3L, "Marco", "Huaman"));
        entrega.setEstado(1);
        entrega.setCosto_delivery(new BigDecimal("8.00"));
        entrega.setObservacion("Llamar al llegar");
        when(entregaRepository.findAll()).thenReturn(Arrays.asList(entrega));

        XSSFWorkbook libro = new XSSFWorkbook(new ByteArrayInputStream(reporteService.generarReporteEntregas()));
        Sheet hoja = libro.getSheet("Entregas");

        assertEquals(1, hoja.getLastRowNum());
        assertEquals("Pedido", hoja.getRow(0).getCell(0).getStringCellValue());
        assertEquals("#DV-00003", hoja.getRow(1).getCell(0).getStringCellValue());
        assertEquals("Lucia Paredes", hoja.getRow(1).getCell(1).getStringCellValue());
        assertEquals("Av. Primavera 123", hoja.getRow(1).getCell(2).getStringCellValue());
        assertEquals("Marco Huaman", hoja.getRow(1).getCell(3).getStringCellValue());
        assertEquals(8.0, hoja.getRow(1).getCell(6).getNumericCellValue(), 0.001);
        assertEquals("En camino", hoja.getRow(1).getCell(7).getStringCellValue());
        assertEquals("Llamar al llegar", hoja.getRow(1).getCell(8).getStringCellValue());
    }

    @Test
    void reporteEntregas_sinEntregasSoloTieneLosTitulos() throws Exception {
        when(entregaRepository.findAll()).thenReturn(new ArrayList<>());

        XSSFWorkbook libro = new XSSFWorkbook(new ByteArrayInputStream(reporteService.generarReporteEntregas()));

        assertEquals(0, libro.getSheet("Entregas").getLastRowNum());
    }
}
