package lu.forex.system.fx.requests;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping("/preProcessor")
public interface PreProcessorRequest {

  @PostMapping("/start")
  @ResponseStatus(HttpStatus.CREATED)
  void getPreData(@RequestParam String inputFolderPath);

  @PostMapping("/trades")
  @ResponseStatus(HttpStatus.CREATED)
  void getTradesData(@RequestParam String inputFolderPath);

}
