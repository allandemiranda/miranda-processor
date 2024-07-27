package lu.forex.system.fx.repository;

import java.util.Optional;
import java.util.UUID;
import lu.forex.system.fx.enums.Symbol;
import lu.forex.system.fx.models.Tick;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

public interface TickRepository extends JpaRepository<Tick, UUID>, JpaSpecificationExecutor<Tick> {

  @NonNull
  @Transactional(readOnly = true)
  Optional<Tick> getFirstBySymbol(@NonNull Symbol symbol);
}