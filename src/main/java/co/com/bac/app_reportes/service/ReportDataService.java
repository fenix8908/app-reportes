package co.com.bac.app_reportes.service;

import co.com.bac.app_reportes.dto.ReporteRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class ReportDataService {

    public Map<String, Object> transformarData(ReporteRequest reportRequest) {
        Map<String, Object> parameters = new HashMap<>();

        // Validar y transformar datos dinámicamente
        if (reportRequest.getDatos() == null || reportRequest.getDatos().isEmpty()) {
            throw new IllegalArgumentException("El reporte requiere datos, pero no se enviaron.");
        }

        reportRequest.getParametros().forEach((key, value) -> {
            // Ejemplo de validación específica
            if (value == null) {
                throw new IllegalArgumentException("El parámetro '" + key + "' no puede ser nulo.");
            }
            parameters.put(key, value);
        });

        // Puedes agregar parámetros adicionales (fechas, usuario, etc.)
        parameters.put("fecha_generacion", new Date());
        parameters.put("usuario_solicitante", "usuario-ejemplo"); // Esto puede ser dinámico.

        return parameters;
    }
}
