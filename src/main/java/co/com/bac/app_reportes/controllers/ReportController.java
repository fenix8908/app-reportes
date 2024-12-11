package co.com.bac.app_reportes.controllers;

import co.com.bac.app_reportes.dto.ReporteRequest;
import co.com.bac.app_reportes.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/reportes")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @PostMapping("/carga")
    public ResponseEntity<?> generateReport(@RequestBody ReporteRequest reporteRequest) {
        try {
            byte[] report = reportService.generarReporte(reporteRequest);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", "report.pdf");

            return ResponseEntity.ok().headers(headers).body(report);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.toString());
        }
    }
}
