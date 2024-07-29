package lu.forex.system.fx.exceptions;

import java.util.UUID;
import lombok.experimental.StandardException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@StandardException
@ResponseStatus(HttpStatus.NOT_FOUND)
public class TradeNotFoundException extends RuntimeException {

  public TradeNotFoundException(final UUID id) {
    super("Trade with id " + id + " not found");
  }
}
