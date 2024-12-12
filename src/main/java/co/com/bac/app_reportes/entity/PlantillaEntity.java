package co.com.bac.app_reportes.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Entity(name = "plantillas")
@Table(name = "plantillas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlantillaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre",nullable = false, unique = true)
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @Lob
    @Column(nullable = false,name = "contenido")
    private byte[] contenido;

    @Column(name = "fechacreacion", nullable = false, updatable = false)
    private Date fechaCreacion = new Date();
}
