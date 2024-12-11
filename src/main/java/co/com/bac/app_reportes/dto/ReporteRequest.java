package co.com.bac.app_reportes.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReporteRequest {
    private String templateName;
    private Map<String, Object> parameters;
    private List<Object> data;
}
