package co.com.bac.app_reportes.service;

import co.com.bac.app_reportes.dto.ReporteRequest;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class ReportService {

    private Logger log = LoggerFactory.getLogger(ReportService.class);

    private final ResourceLoader resourceLoader;

    @Value("${reports.template.path:/templates/}")
    private String templateBasePath;

    @Autowired
    public ReportService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public byte[] generarReporte(ReporteRequest reporteRequest) {
        try {
            JasperReport jasperReport = cargarPlantilla(reporteRequest.getNombrePlantilla());
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reporteRequest.getDatos());
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, reporteRequest.getParametros(), dataSource);

            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (JRException e) {
            log.error(e.toString());
            throw new RuntimeException("Error generando el reporte de jasper", e);
        }
    }

    @Cacheable("jasperTemplates")
    public JasperReport cargarPlantilla(String nombrePlantilla) {
        String jasperPath = templateBasePath + nombrePlantilla + ".jasper";
        String jrxmlPath = templateBasePath + nombrePlantilla + ".jrxml";

        try {
            // Intentar cargar el archivo `.jasper`
            Resource jasperResource = resourceLoader.getResource("classpath:" + jasperPath);
            if (jasperResource.exists()) {
                return cargarPlantillaJasper(jasperResource);
            }

            // Si no existe `.jasper`, buscar el archivo `.jrxml` y compilarlo
            Resource jrxmlResource = resourceLoader.getResource("classpath:" + jrxmlPath);
            if (jrxmlResource.exists()) {
                InputStream inputStream = jrxmlResource.getInputStream();
                return JasperCompileManager.compileReport(inputStream);
            }

            throw new RuntimeException("No se encontró la plantilla con nombre: " + nombrePlantilla);
        } catch (Exception e) {
            log.error(e.toString());
            throw new RuntimeException("Error al cargar la plantilla de jasper: " + nombrePlantilla, e);
        }
    }

    public JasperReport cargarPlantillaJasper(Resource resource) throws JRException {
        try (InputStream inputStream = resource.getInputStream()) {
            return (JasperReport) JRLoader.loadObject(inputStream);
        } catch (Exception e) {
            throw new JRException("Error al cargar el InputStream de la plantilla", e);
        }
    }



}
