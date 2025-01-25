package co.com.bac.app_reportes.controllers;

import co.com.bac.app_reportes.dto.ReporteRequest;
import co.com.bac.app_reportes.dto.SaveReporteDto;
import co.com.bac.app_reportes.entity.PlantillaEntity;
import co.com.bac.app_reportes.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;



@RestController
@RequestMapping("/reportes")
@CrossOrigin("*")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Operation(summary = "Generar un reporte")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reporte generado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Plantilla no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error al generar el reporte")
    })
    @PostMapping("/carga")
    public ResponseEntity<?> generateReport(@RequestBody ReporteRequest reporteRequest) {
        try {
            byte[] report = reportService.generarReporteBD(reporteRequest);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", "report.pdf");

            return ResponseEntity.ok().headers(headers).body(report);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.toString());
        }
    }

    @PostMapping(value = "/guardado", consumes = "application/json")
    public ResponseEntity<?> guardarReporte(@RequestBody SaveReporteDto reporteDto) {
        try {
            PlantillaEntity plantilla = reportService.guardarPlantilla(reporteDto);
            if(plantilla != null && plantilla.getNombre()!= null){
                return ResponseEntity.ok("El reporte se guardo con exito!!");
            }else{
                return ResponseEntity.status(202).body("No fue posible guardar el reporte!!");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.toString());
        }
    }
}
