package lu.forex.system.fx.exceptions;

import java.util.UUID;
import lombok.experimental.StandardException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@StandardException
@ResponseStatus(HttpStatus.NOT_FOUND)
public class CandlestickNotFoundException extends RuntimeException {

  public CandlestickNotFoundException(final UUID id) {
    super("Candlestick with id " + id + " not found");
  }
}
