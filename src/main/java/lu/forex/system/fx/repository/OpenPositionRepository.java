package lu.forex.system.fx.repository;

import java.util.UUID;
import lu.forex.system.fx.models.OpenPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OpenPositionRepository extends JpaRepository<OpenPosition, UUID>, JpaSpecificationExecutor<OpenPosition> {

}