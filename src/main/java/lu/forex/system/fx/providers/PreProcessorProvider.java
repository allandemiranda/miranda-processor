package lu.forex.system.fx.providers;

import java.io.File;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;

public interface PreProcessorProvider {

  @Async
  void startDataSystem(final @NonNull File inputFolder);

  @Async
  void startTradeDataSystem(final @NonNull File inputFolder);
}
