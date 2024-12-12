package co.com.bac.app_reportes.repository;

import co.com.bac.app_reportes.entity.PlantillaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlantillaRepository extends JpaRepository<PlantillaEntity,Integer> {

    Optional<PlantillaEntity> findByNombre(String nombre);
}
