package co.com.bac.app_reportes.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SaveReporteDto {

    private String nombre;
    private String descripcion;
    private String contenido;
    private String version;
}
