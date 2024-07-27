package lu.forex.system.fx.exceptions;

import lombok.experimental.StandardException;
import lu.forex.system.fx.enums.Symbol;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@StandardException
@ResponseStatus(HttpStatus.NOT_FOUND)
public class LastTickNotFound extends RuntimeException {

  public LastTickNotFound(final Symbol symbolRequest) {
    super("Could not find last tick for " + symbolRequest + " symbol");
  }
}
