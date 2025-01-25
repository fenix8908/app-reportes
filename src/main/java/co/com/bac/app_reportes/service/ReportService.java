package co.com.bac.app_reportes.service;

import co.com.bac.app_reportes.dto.ReporteRequest;
import co.com.bac.app_reportes.dto.SaveReporteDto;
import co.com.bac.app_reportes.entity.PlantillaEntity;
import co.com.bac.app_reportes.repository.PlantillaRepository;
import jakarta.transaction.Transactional;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Service
public class ReportService {


    private final Logger log = LoggerFactory.getLogger(ReportService.class);

    private final ResourceLoader resourceLoader;

    private final PlantillaRepository plantillaRepository;

    private final ReportDataService reportDataService;

    @Autowired
    public ReportService(ResourceLoader resourceLoader, PlantillaRepository plantillaRepository, ReportDataService reportDataService) {
        this.resourceLoader = resourceLoader;
        this.plantillaRepository = plantillaRepository;
        this.reportDataService = reportDataService;
    }

    public byte[] generarReporteBD(ReporteRequest reporteRequest) {
        try {
            // Buscar plantilla en la base de datos
            PlantillaEntity template = plantillaRepository.findByNombre(reporteRequest.getNombrePlantilla())
                    .orElseThrow(() -> new RuntimeException("Plantilla no encontrada: "
                                                            + reporteRequest.getNombrePlantilla()));
            String plantilla = new String(template.getContenido(), StandardCharsets.UTF_8);
            // Cargar la plantilla como InputStream
            InputStream templateInputStream = new ByteArrayInputStream(template.getContenido());
            JasperReport jasperReport = JasperCompileManager.compileReport(templateInputStream);

            Map<String,Object> parametrosValidados = reportDataService.transformarData(reporteRequest);

            // Usar los datos dinámicos del JSON
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reporteRequest.getDatos());
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametrosValidados, dataSource);

            // Exportar a PDF
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (Exception e) {
            throw new RuntimeException("Error generando el reporte: " + e.getMessage(), e);
        }
    }

    @Transactional
    public PlantillaEntity guardarPlantilla(SaveReporteDto reporteDto){

        byte[] decodedBytes;
        try {
            decodedBytes = Base64.getDecoder().decode(reporteDto.getContenido());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("El contenido del reporte no está en un formato Base64 válido", e);
        }

        // Crear y mapear la entidad
        PlantillaEntity plantillaEntity = new PlantillaEntity();
        plantillaEntity.setNombre(reporteDto.getNombre());
        plantillaEntity.setDescripcion(reporteDto.getDescripcion());
        plantillaEntity.setContenido(decodedBytes);
        plantillaEntity.setFechaCreacion(new Date());
        plantillaEntity.setVersion(reporteDto.getVersion());

        // Guardar la entidad y manejar posibles excepciones del repositorio
        try {
            return plantillaRepository.save(plantillaEntity);
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar la plantilla en la base de datos", e);
        }

    }


}
