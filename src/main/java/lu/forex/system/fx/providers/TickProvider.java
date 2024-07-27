package lu.forex.system.fx.providers;

import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.constraints.NotNull;
import lu.forex.system.fx.dtos.TickDto;
import lu.forex.system.fx.enums.Symbol;

public interface TickProvider {

  @Transactional(readOnly = true)
  @NotNull
  TickDto getLastTick(final @NotNull Symbol symbol);


  @Transactional()
  void updateTickData(final @NotNull TickDto currentTick);

  @Transactional()
  TickDto insertInitTick(final @NotNull TickDto currentTick);
}
