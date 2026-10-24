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

    @Autowired
    private ReporteService reporteService;

    @GetMapping("/pedidos")
    public ResponseEntity<?> reportePedidos() {
        try {
            return descargar(reporteService.generarReportePedidos(), "reporte-pedidos");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al generar el reporte de pedidos");
        }
    }

    @GetMapping("/entregas")
    public ResponseEntity<?> reporteEntregas() {
        try {
            return descargar(reporteService.generarReporteEntregas(), "reporte-entregas");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al generar el reporte de entregas");
        }
    }

    private ResponseEntity<byte[]> descargar(byte[] contenido, String nombreBase) {
        String nombreArchivo = nombreBase + "-" + LocalDate.now() + ".xlsx";
        return ResponseEntity.ok()
                .contentType(EXCEL)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombreArchivo + "\"")
                .body(contenido);
    }
}
