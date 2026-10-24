package com.example.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.model.Entrega;
import com.example.model.Pedido;
import com.example.model.Usuario;
import com.example.repository.EntregaRepository;
import com.example.repository.PedidoRepository;

@Service
public class ReporteService {

    private static final String[] ESTADOS_PEDIDO = {"PENDIENTE", "CONFIRMADO", "EN_CAMINO", "ENTREGADO", "CANCELADO"};
    private static final String[] ESTADOS_ENTREGA = {"Asignada", "En camino", "Entregada", "Con incidencia"};
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final int ANCHO_COLUMNA = 24 * 256;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private EntregaRepository entregaRepository;

    public byte[] generarReportePedidos() throws IOException {
        List<Pedido> pedidos = pedidoRepository.findAll().stream()
                .sorted(Comparator.comparing(Pedido::getId_pedido, Comparator.nullsLast(Comparator.<Long>reverseOrder())))
                .collect(Collectors.toList());

        try (Workbook libro = new XSSFWorkbook(); ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
            CellStyle titulo = crearEstiloTitulo(libro);
            CellStyle moneda = crearEstiloMoneda(libro);

            Sheet hoja = libro.createSheet("Pedidos");
            escribirTitulos(hoja, titulo, "Código", "Cliente", "Fecha del pedido", "Fecha de entrega",
                    "Modalidad", "Estado", "Total");

            int numeroFila = 1;
            for (Pedido pedido : pedidos) {
                Row fila = hoja.createRow(numeroFila++);
                fila.createCell(0).setCellValue(codigoPedido(pedido));
                fila.createCell(1).setCellValue(nombreCompleto(pedido.getUsuario()));
                fila.createCell(2).setCellValue(formatearFecha(pedido.getFecha_pedido()));
                fila.createCell(3).setCellValue(formatearFecha(pedido.getFecha_entrega()));
                fila.createCell(4).setCellValue(textoSeguro(pedido.getModalidad_entrega()));
                fila.createCell(5).setCellValue(etiquetaEstado(pedido.getEstado()));
                escribirMoneda(fila.createCell(6), pedido.getTotal(), moneda);
            }
            fijarAnchos(hoja, 7);

            escribirResumen(libro, pedidos, titulo, moneda);

            libro.write(salida);
            return salida.toByteArray();
        }
    }

    public byte[] generarReporteEntregas() throws IOException {
        List<Entrega> entregas = entregaRepository.findAll().stream()
                .sorted(Comparator.comparing(Entrega::getId_entrega, Comparator.nullsLast(Comparator.<Long>reverseOrder())))
                .collect(Collectors.toList());

        try (Workbook libro = new XSSFWorkbook(); ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
            CellStyle titulo = crearEstiloTitulo(libro);
            CellStyle moneda = crearEstiloMoneda(libro);

            Sheet hoja = libro.createSheet("Entregas");
            escribirTitulos(hoja, titulo, "Pedido", "Cliente", "Dirección", "Motorizado", "Fecha de asignación",
                    "Fecha de entrega", "Costo de delivery", "Estado", "Observación");

            int numeroFila = 1;
            for (Entrega entrega : entregas) {
                Pedido pedido = entrega.getPedido();
                Row fila = hoja.createRow(numeroFila++);
                fila.createCell(0).setCellValue(codigoPedido(pedido));
                fila.createCell(1).setCellValue(nombreCompleto(pedido == null ? null : pedido.getUsuario()));
                fila.createCell(2).setCellValue(direccionDe(pedido));
                fila.createCell(3).setCellValue(nombreCompleto(entrega.getMotorizado()));
                fila.createCell(4).setCellValue(formatearFecha(entrega.getFecha_asignacion()));
                fila.createCell(5).setCellValue(formatearFecha(entrega.getFecha_entrega()));
                escribirMoneda(fila.createCell(6), entrega.getCosto_delivery(), moneda);
                fila.createCell(7).setCellValue(etiquetaEstadoEntrega(entrega.getEstado()));
                fila.createCell(8).setCellValue(textoSeguro(entrega.getObservacion()));
            }
            fijarAnchos(hoja, 9);

            libro.write(salida);
            return salida.toByteArray();
        }
    }

    private void escribirResumen(Workbook libro, List<Pedido> pedidos, CellStyle titulo, CellStyle moneda) {
        Sheet hoja = libro.createSheet("Resumen");
        escribirTitulos(hoja, titulo, "Estado", "Cantidad");

        int numeroFila = 1;
        for (String estado : ESTADOS_PEDIDO) {
            long cantidad = pedidos.stream().filter(p -> estado.equalsIgnoreCase(p.getEstado())).count();
            Row fila = hoja.createRow(numeroFila++);
            fila.createCell(0).setCellValue(etiquetaEstado(estado));
            fila.createCell(1).setCellValue(cantidad);
        }

        Row filaTotal = hoja.createRow(numeroFila++);
        filaTotal.createCell(0).setCellValue("Total de pedidos");
        filaTotal.createCell(1).setCellValue(pedidos.size());

        BigDecimal ventas = pedidos.stream()
                .filter(p -> !"CANCELADO".equalsIgnoreCase(p.getEstado()))
                .map(Pedido::getTotal)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Row filaVentas = hoja.createRow(numeroFila);
        filaVentas.createCell(0).setCellValue("Ventas acumuladas (sin cancelados)");
        escribirMoneda(filaVentas.createCell(1), ventas, moneda);

        fijarAnchos(hoja, 2);
    }

    private CellStyle crearEstiloTitulo(Workbook libro) {
        Font negrita = libro.createFont();
        negrita.setBold(true);
        CellStyle estilo = libro.createCellStyle();
        estilo.setFont(negrita);
        estilo.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        estilo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return estilo;
    }

    private CellStyle crearEstiloMoneda(Workbook libro) {
        CellStyle estilo = libro.createCellStyle();
        estilo.setDataFormat(libro.createDataFormat().getFormat("\"S/\" #,##0.00"));
        return estilo;
    }

    private void escribirTitulos(Sheet hoja, CellStyle estilo, String... titulos) {
        Row fila = hoja.createRow(0);
        for (int i = 0; i < titulos.length; i++) {
            Cell celda = fila.createCell(i);
            celda.setCellValue(titulos[i]);
            celda.setCellStyle(estilo);
        }
    }

    private void escribirMoneda(Cell celda, BigDecimal valor, CellStyle estilo) {
        celda.setCellValue(valor == null ? 0 : valor.doubleValue());
        celda.setCellStyle(estilo);
    }

    private void fijarAnchos(Sheet hoja, int columnas) {
        for (int i = 0; i < columnas; i++) {
            hoja.setColumnWidth(i, ANCHO_COLUMNA);
        }
    }

    private String codigoPedido(Pedido pedido) {
        if (pedido == null || pedido.getId_pedido() == null) {
            return "";
        }
        return "#DV-" + String.format("%05d", pedido.getId_pedido());
    }

    private String nombreCompleto(Usuario usuario) {
        if (usuario == null) {
            return "";
        }
        return (textoSeguro(usuario.getNombre()) + " " + textoSeguro(usuario.getApellido())).trim();
    }

    private String direccionDe(Pedido pedido) {
        if (pedido == null || pedido.getDireccion() == null) {
            return "";
        }
        return textoSeguro(pedido.getDireccion().getDireccion());
    }

    private String formatearFecha(LocalDateTime fecha) {
        return fecha == null ? "" : fecha.format(FORMATO_FECHA);
    }

    private String textoSeguro(String texto) {
        return texto == null ? "" : texto;
    }

    private String etiquetaEstado(String estado) {
        if (estado == null || estado.isEmpty()) {
            return "";
        }
        String texto = estado.replace('_', ' ').toLowerCase();
        return texto.substring(0, 1).toUpperCase() + texto.substring(1);
    }

    private String etiquetaEstadoEntrega(int estado) {
        if (estado < 0 || estado >= ESTADOS_ENTREGA.length) {
            return "Desconocido";
        }
        return ESTADOS_ENTREGA[estado];
    }
}
