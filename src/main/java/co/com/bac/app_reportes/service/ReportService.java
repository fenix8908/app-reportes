package co.com.bac.app_reportes.service;

import co.com.bac.app_reportes.dto.ReporteRequest;
import co.com.bac.app_reportes.entity.PlantillaEntity;
import co.com.bac.app_reportes.repository.PlantillaRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
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
                    .orElseThrow(() -> new RuntimeException("Plantilla no encontrada: " + reporteRequest.getNombrePlantilla()));

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


}
