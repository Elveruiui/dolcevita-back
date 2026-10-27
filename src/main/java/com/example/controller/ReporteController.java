package com.example.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.service.ReporteService;

@RestController
@RequestMapping("/reporte")
public class ReporteController {

    private static final MediaType EXCEL =
            MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
<<<<<<< HEAD
    private static final MediaType PDF = MediaType.APPLICATION_PDF;
=======
>>>>>>> 7a8f0bed507ec3a77a98c71f15337719b7fed49e

    @Autowired
    private ReporteService reporteService;

<<<<<<< HEAD
   

    @GetMapping("/pedidos")
    public ResponseEntity<?> reportePedidos() {
        try {
            return descargarExcel(reporteService.generarReportePedidos(), "reporte-pedidos");
=======
    @GetMapping("/pedidos")
    public ResponseEntity<?> reportePedidos() {
        try {
            return descargar(reporteService.generarReportePedidos(), "reporte-pedidos");
>>>>>>> 7a8f0bed507ec3a77a98c71f15337719b7fed49e
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al generar el reporte de pedidos");
        }
    }

    @GetMapping("/entregas")
    public ResponseEntity<?> reporteEntregas() {
        try {
<<<<<<< HEAD
            return descargarExcel(reporteService.generarReporteEntregas(), "reporte-entregas");
=======
            return descargar(reporteService.generarReporteEntregas(), "reporte-entregas");
>>>>>>> 7a8f0bed507ec3a77a98c71f15337719b7fed49e
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al generar el reporte de entregas");
        }
    }

<<<<<<< HEAD
    @GetMapping("/productos")
    public ResponseEntity<?> reporteProductos() {
        try {
            return descargarExcel(reporteService.generarReporteProductos(), "reporte-productos");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al generar el reporte de productos");
        }
    }

    @GetMapping("/categorias")
    public ResponseEntity<?> reporteCategorias() {
        try {
            return descargarExcel(reporteService.generarReporteCategorias(), "reporte-categorias");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al generar el reporte de categorías");
        }
    }

    @GetMapping("/usuarios")
    public ResponseEntity<?> reporteUsuarios() {
        try {
            return descargarExcel(reporteService.generarReporteUsuarios(), "reporte-usuarios");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al generar el reporte de usuarios");
        }
    }

   

    @GetMapping("/pedidos/pdf")
    public ResponseEntity<?> reportePedidosPdf() {
        try {
            return descargarPdf(reporteService.generarPdfPedidos(), "reporte-pedidos");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al generar el PDF de pedidos");
        }
    }

    @GetMapping("/entregas/pdf")
    public ResponseEntity<?> reporteEntregasPdf() {
        try {
            return descargarPdf(reporteService.generarPdfEntregas(), "reporte-entregas");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al generar el PDF de entregas");
        }
    }

    @GetMapping("/productos/pdf")
    public ResponseEntity<?> reporteProductosPdf() {
        try {
            return descargarPdf(reporteService.generarPdfProductos(), "reporte-productos");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al generar el PDF de productos");
        }
    }

    @GetMapping("/categorias/pdf")
    public ResponseEntity<?> reporteCategoriasPdf() {
        try {
            return descargarPdf(reporteService.generarPdfCategorias(), "reporte-categorias");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al generar el PDF de categorías");
        }
    }

    @GetMapping("/usuarios/pdf")
    public ResponseEntity<?> reporteUsuariosPdf() {
        try {
            return descargarPdf(reporteService.generarPdfUsuarios(), "reporte-usuarios");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al generar el PDF de usuarios");
        }
    }

    private ResponseEntity<byte[]> descargarExcel(byte[] contenido, String nombreBase) {
=======
    private ResponseEntity<byte[]> descargar(byte[] contenido, String nombreBase) {
>>>>>>> 7a8f0bed507ec3a77a98c71f15337719b7fed49e
        String nombreArchivo = nombreBase + "-" + LocalDate.now() + ".xlsx";
        return ResponseEntity.ok()
                .contentType(EXCEL)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombreArchivo + "\"")
                .body(contenido);
    }
<<<<<<< HEAD

    private ResponseEntity<byte[]> descargarPdf(byte[] contenido, String nombreBase) {
        String nombreArchivo = nombreBase + "-" + LocalDate.now() + ".pdf";
        return ResponseEntity.ok()
                .contentType(PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombreArchivo + "\"")
                .body(contenido);
    }
}
=======
}
>>>>>>> 7a8f0bed507ec3a77a98c71f15337719b7fed49e
