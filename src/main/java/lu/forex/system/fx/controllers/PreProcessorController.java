package lu.forex.system.fx.controllers;

import java.io.File;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lu.forex.system.fx.exceptions.InputFileNotFoundException;
import lu.forex.system.fx.providers.PreProcessorProvider;
import lu.forex.system.fx.requests.PreProcessorRequest;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Getter(AccessLevel.PRIVATE)
public class PreProcessorController implements PreProcessorRequest {

  private final PreProcessorProvider preProcessorProvider;

  @Override
  public void getPreData(final String inputFolderPath) {
    final File inputFolder = new File(inputFolderPath);
    if (inputFolder.exists()) {
      this.getPreProcessorProvider().startDataSystem(inputFolder);
    } else {
      throw new InputFileNotFoundException(inputFolderPath);
    }
  }

  @Override
  public void getTradesData(final String inputFolderPath) {
    final File inputFolder = new File(inputFolderPath);
    if (inputFolder.exists()) {
      this.getPreProcessorProvider().startTradeDataSystem(inputFolder);
    } else {
      throw new InputFileNotFoundException(inputFolderPath);
    }
  }
}
