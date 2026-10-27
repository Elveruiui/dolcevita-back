package com.example.service;

<<<<<<< HEAD
import java.awt.Color;
=======
>>>>>>> 7a8f0bed507ec3a77a98c71f15337719b7fed49e
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

<<<<<<< HEAD
import com.example.model.Categoria;
import com.example.model.Entrega;
import com.example.model.Pedido;
import com.example.model.Producto;
import com.example.model.Usuario;
import com.example.repository.CategoriaRepository;
import com.example.repository.EntregaRepository;
import com.example.repository.PedidoRepository;
import com.example.repository.ProductoRepository;
import com.example.repository.UsuarioRepository;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
=======
import com.example.model.Entrega;
import com.example.model.Pedido;
import com.example.model.Usuario;
import com.example.repository.EntregaRepository;
import com.example.repository.PedidoRepository;
>>>>>>> 7a8f0bed507ec3a77a98c71f15337719b7fed49e

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

<<<<<<< HEAD
    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    

=======
>>>>>>> 7a8f0bed507ec3a77a98c71f15337719b7fed49e
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

<<<<<<< HEAD
    public byte[] generarReporteProductos() throws IOException {
        List<Producto> productos = productoRepository.findAll();

        try (Workbook libro = new XSSFWorkbook(); ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
            CellStyle titulo = crearEstiloTitulo(libro);
            CellStyle moneda = crearEstiloMoneda(libro);

            Sheet hoja = libro.createSheet("Productos");
            escribirTitulos(hoja, titulo, "ID", "Producto", "Categoría", "Descripción", "Precio", "Stock", "Estado");

            int numeroFila = 1;
            for (Producto p : productos) {
                Row fila = hoja.createRow(numeroFila++);
                fila.createCell(0).setCellValue(p.getId_producto() == null ? 0 : p.getId_producto());
                fila.createCell(1).setCellValue(textoSeguro(p.getNombre()));
                fila.createCell(2).setCellValue(p.getCategoria() != null ? textoSeguro(p.getCategoria().getNombre()) : "—");
                fila.createCell(3).setCellValue(textoSeguro(p.getDescripcion()));
                escribirMoneda(fila.createCell(4), BigDecimal.valueOf(p.getPrecio()), moneda);
                fila.createCell(5).setCellValue(p.getStock());
                fila.createCell(6).setCellValue(p.getEstado() == 1 ? "Activo" : "Inactivo");
            }
            fijarAnchos(hoja, 7);

            libro.write(salida);
            return salida.toByteArray();
        }
    }

    public byte[] generarReporteCategorias() throws IOException {
        List<Categoria> categorias = categoriaRepository.findAll();
        List<Producto> productos = productoRepository.findAll();

        try (Workbook libro = new XSSFWorkbook(); ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
            CellStyle titulo = crearEstiloTitulo(libro);

            Sheet hoja = libro.createSheet("Categorías");
            escribirTitulos(hoja, titulo, "ID", "Categoría", "Descripción", "Cantidad de Productos", "Estado");

            int numeroFila = 1;
            for (Categoria c : categorias) {
                long cantProd = productos.stream()
                        .filter(p -> p.getCategoria() != null && Objects.equals(p.getCategoria().getId_categoria(), c.getId_categoria()))
                        .count();
                Row fila = hoja.createRow(numeroFila++);
                fila.createCell(0).setCellValue(c.getId_categoria() == null ? 0 : c.getId_categoria());
                fila.createCell(1).setCellValue(textoSeguro(c.getNombre()));
                fila.createCell(2).setCellValue(textoSeguro(c.getDescripcion()));
                fila.createCell(3).setCellValue(cantProd);
                fila.createCell(4).setCellValue(c.getEstado() == 1 ? "Activa" : "Inactiva");
            }
            fijarAnchos(hoja, 5);

            libro.write(salida);
            return salida.toByteArray();
        }
    }

    public byte[] generarReporteUsuarios() throws IOException {
        List<Usuario> usuarios = usuarioRepository.findAll();

        try (Workbook libro = new XSSFWorkbook(); ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
            CellStyle titulo = crearEstiloTitulo(libro);

            Sheet hoja = libro.createSheet("Usuarios");
            escribirTitulos(hoja, titulo, "ID", "Nombre Completo", "Correo", "Teléfono", "Rol", "Estado");

            int numeroFila = 1;
            for (Usuario u : usuarios) {
                Row fila = hoja.createRow(numeroFila++);
                fila.createCell(0).setCellValue(u.getId_usuario() == null ? 0 : u.getId_usuario());
                fila.createCell(1).setCellValue(nombreCompleto(u));
                fila.createCell(2).setCellValue(textoSeguro(u.getCorreo()));
                fila.createCell(3).setCellValue(textoSeguro(u.getTelefono()));
                fila.createCell(4).setCellValue(u.getRol() != null ? textoSeguro(u.getRol().getNombre_rol()) : "—");
                fila.createCell(5).setCellValue(u.getEstado() == 1 ? "Activo" : "Inactivo");
            }
            fijarAnchos(hoja, 6);

            libro.write(salida);
            return salida.toByteArray();
        }
    }

    

    public byte[] generarPdfPedidos() throws IOException {
        List<Pedido> pedidos = pedidoRepository.findAll().stream()
                .sorted(Comparator.comparing(Pedido::getId_pedido, Comparator.nullsLast(Comparator.<Long>reverseOrder())))
                .collect(Collectors.toList());

        try (ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4.rotate(), 30, 30, 30, 30);
            PdfWriter.getInstance(doc, salida);
            doc.open();

            agregarCabeceraPdf(doc, "Reporte de Pedidos Registrados");

            PdfPTable tabla = crearTablaPdf(new float[]{1.4f, 2.5f, 2.0f, 2.0f, 1.5f, 1.5f, 1.5f},
                    "Código", "Cliente", "Fecha Pedido", "Fecha Entrega", "Modalidad", "Estado", "Total");

            for (Pedido p : pedidos) {
                agregarCeldaPdf(tabla, codigoPedido(p));
                agregarCeldaPdf(tabla, nombreCompleto(p.getUsuario()));
                agregarCeldaPdf(tabla, formatearFecha(p.getFecha_pedido()));
                agregarCeldaPdf(tabla, formatearFecha(p.getFecha_entrega()));
                agregarCeldaPdf(tabla, textoSeguro(p.getModalidad_entrega()));
                agregarCeldaPdf(tabla, etiquetaEstado(p.getEstado()));
                agregarCeldaPdf(tabla, formatearMonedaTexto(p.getTotal()));
            }
            doc.add(tabla);
            doc.close();
            return salida.toByteArray();
        }
    }

    public byte[] generarPdfEntregas() throws IOException {
        List<Entrega> entregas = entregaRepository.findAll().stream()
                .sorted(Comparator.comparing(Entrega::getId_entrega, Comparator.nullsLast(Comparator.<Long>reverseOrder())))
                .collect(Collectors.toList());

        try (ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4.rotate(), 30, 30, 30, 30);
            PdfWriter.getInstance(doc, salida);
            doc.open();

            agregarCabeceraPdf(doc, "Reporte de Entregas y Despacho");

            PdfPTable tabla = crearTablaPdf(new float[]{1.3f, 2.2f, 2.8f, 2.2f, 1.8f, 1.8f, 1.3f, 1.4f},
                    "Pedido", "Cliente", "Dirección", "Motorizado", "Asignación", "Entrega", "Costo", "Estado");

            for (Entrega e : entregas) {
                Pedido p = e.getPedido();
                agregarCeldaPdf(tabla, codigoPedido(p));
                agregarCeldaPdf(tabla, nombreCompleto(p == null ? null : p.getUsuario()));
                agregarCeldaPdf(tabla, direccionDe(p));
                agregarCeldaPdf(tabla, nombreCompleto(e.getMotorizado()));
                agregarCeldaPdf(tabla, formatearFecha(e.getFecha_asignacion()));
                agregarCeldaPdf(tabla, formatearFecha(e.getFecha_entrega()));
                agregarCeldaPdf(tabla, formatearMonedaTexto(e.getCosto_delivery()));
                agregarCeldaPdf(tabla, etiquetaEstadoEntrega(e.getEstado()));
            }
            doc.add(tabla);
            doc.close();
            return salida.toByteArray();
        }
    }

    public byte[] generarPdfProductos() throws IOException {
        List<Producto> productos = productoRepository.findAll();

        try (ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4, 30, 30, 30, 30);
            PdfWriter.getInstance(doc, salida);
            doc.open();

            agregarCabeceraPdf(doc, "Catálogo e Inventario de Productos");

            PdfPTable tabla = crearTablaPdf(new float[]{0.8f, 2.5f, 2.2f, 1.4f, 1.0f, 1.2f},
                    "ID", "Producto", "Categoría", "Precio", "Stock", "Estado");

            for (Producto p : productos) {
                agregarCeldaPdf(tabla, String.valueOf(p.getId_producto() == null ? "" : p.getId_producto()));
                agregarCeldaPdf(tabla, textoSeguro(p.getNombre()));
                agregarCeldaPdf(tabla, p.getCategoria() != null ? textoSeguro(p.getCategoria().getNombre()) : "—");
                agregarCeldaPdf(tabla, formatearMonedaTexto(BigDecimal.valueOf(p.getPrecio())));
                agregarCeldaPdf(tabla, String.valueOf(p.getStock()));
                agregarCeldaPdf(tabla, p.getEstado() == 1 ? "Activo" : "Inactivo");
            }
            doc.add(tabla);
            doc.close();
            return salida.toByteArray();
        }
    }

    public byte[] generarPdfCategorias() throws IOException {
        List<Categoria> categorias = categoriaRepository.findAll();
        List<Producto> productos = productoRepository.findAll();

        try (ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4, 30, 30, 30, 30);
            PdfWriter.getInstance(doc, salida);
            doc.open();

            agregarCabeceraPdf(doc, "Reporte de Categorías");

            PdfPTable tabla = crearTablaPdf(new float[]{0.8f, 2.2f, 3.5f, 1.2f, 1.2f},
                    "ID", "Categoría", "Descripción", "Productos", "Estado");

            for (Categoria c : categorias) {
                long cantProd = productos.stream()
                        .filter(p -> p.getCategoria() != null && Objects.equals(p.getCategoria().getId_categoria(), c.getId_categoria()))
                        .count();
                agregarCeldaPdf(tabla, String.valueOf(c.getId_categoria() == null ? "" : c.getId_categoria()));
                agregarCeldaPdf(tabla, textoSeguro(c.getNombre()));
                agregarCeldaPdf(tabla, textoSeguro(c.getDescripcion()));
                agregarCeldaPdf(tabla, String.valueOf(cantProd));
                agregarCeldaPdf(tabla, c.getEstado() == 1 ? "Activa" : "Inactiva");
            }
            doc.add(tabla);
            doc.close();
            return salida.toByteArray();
        }
    }

    public byte[] generarPdfUsuarios() throws IOException {
        List<Usuario> usuarios = usuarioRepository.findAll();

        try (ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4, 30, 30, 30, 30);
            PdfWriter.getInstance(doc, salida);
            doc.open();

            agregarCabeceraPdf(doc, "Directorio de Usuarios del Sistema");

            PdfPTable tabla = crearTablaPdf(new float[]{0.8f, 2.5f, 2.8f, 1.5f, 1.5f, 1.2f},
                    "ID", "Usuario", "Correo", "Teléfono", "Rol", "Estado");

            for (Usuario u : usuarios) {
                agregarCeldaPdf(tabla, String.valueOf(u.getId_usuario() == null ? "" : u.getId_usuario()));
                agregarCeldaPdf(tabla, nombreCompleto(u));
                agregarCeldaPdf(tabla, textoSeguro(u.getCorreo()));
                agregarCeldaPdf(tabla, textoSeguro(u.getTelefono()));
                agregarCeldaPdf(tabla, u.getRol() != null ? textoSeguro(u.getRol().getNombre_rol()) : "—");
                agregarCeldaPdf(tabla, u.getEstado() == 1 ? "Activo" : "Inactivo");
            }
            doc.add(tabla);
            doc.close();
            return salida.toByteArray();
        }
    }

    
    private void agregarCabeceraPdf(Document doc, String subtitulo) {
        com.lowagie.text.Font fuenteMarca = new com.lowagie.text.Font(
                com.lowagie.text.Font.HELVETICA, 18, com.lowagie.text.Font.BOLD, new Color(61, 43, 31));
        com.lowagie.text.Font fuenteSub = new com.lowagie.text.Font(
                com.lowagie.text.Font.HELVETICA, 12, com.lowagie.text.Font.NORMAL, new Color(109, 93, 93));

        Paragraph pMarca = new Paragraph("DOLCEVITA · Pastelería Artesanal", fuenteMarca);
        pMarca.setSpacingAfter(4);
        doc.add(pMarca);

        Paragraph pSub = new Paragraph(subtitulo + " (" + LocalDateTime.now().format(FORMATO_FECHA) + ")", fuenteSub);
        pSub.setSpacingAfter(14);
        doc.add(pSub);
    }

    private PdfPTable crearTablaPdf(float[] anchos, String... cabeceras) {
        PdfPTable tabla = new PdfPTable(anchos);
        tabla.setWidthPercentage(100);
        com.lowagie.text.Font fuenteCab = new com.lowagie.text.Font(
                com.lowagie.text.Font.HELVETICA, 10, com.lowagie.text.Font.BOLD, Color.WHITE);
        Color fondoCab = new Color(61, 43, 31);

        for (String texto : cabeceras) {
            PdfPCell celda = new PdfPCell(new Phrase(texto, fuenteCab));
            celda.setBackgroundColor(fondoCab);
            celda.setPadding(6);
            celda.setHorizontalAlignment(Element.ALIGN_LEFT);
            tabla.addCell(celda);
        }
        tabla.setHeaderRows(1);
        return tabla;
    }

    private void agregarCeldaPdf(PdfPTable tabla, String valor) {
        com.lowagie.text.Font fuenteCelda = new com.lowagie.text.Font(
                com.lowagie.text.Font.HELVETICA, 9, com.lowagie.text.Font.NORMAL, new Color(40, 40, 40));
        PdfPCell celda = new PdfPCell(new Phrase(valor == null || valor.isEmpty() ? "—" : valor, fuenteCelda));
        celda.setPadding(5);
        celda.setBorderColor(new Color(232, 221, 221));
        tabla.addCell(celda);
    }

    private String formatearMonedaTexto(BigDecimal valor) {
        return String.format("S/ %.2f", valor == null ? 0.0 : valor.doubleValue());
    }

=======
>>>>>>> 7a8f0bed507ec3a77a98c71f15337719b7fed49e
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
<<<<<<< HEAD
}
=======
}
>>>>>>> 7a8f0bed507ec3a77a98c71f15337719b7fed49e
