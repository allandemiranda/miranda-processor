package lu.forex.system.fx.exceptions;

import lombok.experimental.StandardException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@StandardException
@ResponseStatus(HttpStatus.NOT_FOUND)
public class InputFileNotFoundException extends RuntimeException {

  public InputFileNotFoundException(final String path) {
    super("Input file not found: " + path);
  }
}
