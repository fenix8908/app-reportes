package co.com.bac.app_reportes.entity;

import jakarta.persistence.*;
import lombok.*;


import java.util.Date;

@Entity(name = "plantillas")
@Table(name = "plantillas")
@Getter
@Setter
@NoArgsConstructor
public class PlantillaEntity  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre",nullable = false, unique = true)
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(nullable = false,name = "contenido")
    private byte[] contenido;

    @Column(name = "fechacreacion", nullable = false, updatable = false)
    private Date fechaCreacion;

    @Column(name = "version")
    private String version;
}
