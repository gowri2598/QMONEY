
package com.crio.warmup.stock.quotes;

import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.web.client.RestTemplate;

public class TiingoService implements StockQuotesService {

  public static final String token = "d7ee5290251fd4882f10fde8ada179ccc1450745";

  private RestTemplate restTemplate;


  protected TiingoService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }


  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Implement getStockQuote method below that was also declared in the interface.

  // Note:
  // 1. You can move the code from PortfolioManagerImpl#getStockQuote inside newly created method.
  // 2. Run the tests using command below and make sure it passes.
  //    ./gradlew test --tests TiingoServiceTest


  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Write a method to create appropriate url to call the Tiingo API.
 public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException{
        
      String url= buildUri(symbol, from, to);
      String data = restTemplate.getForObject(url,String.class);
      Candle[] tingoCandles=getObjectMapper().readValue(data,TiingoCandle[].class);
      List<Candle> stocks=new ArrayList<Candle>();

      if(tingoCandles!=null)
        {
          stocks=Arrays.asList(tingoCandles);
          return stocks;
        }
      else
        {
          return stocks;//return empty coz method dictates so
        }
    }

    private static ObjectMapper getObjectMapper() {
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.registerModule(new JavaTimeModule());
      return objectMapper;
    }

protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
    
  
  String uri = "https://api.tiingo.com/tiingo/daily/$SYMBOL/prices?"
      + "startDate=$STARTDATE&endDate=$ENDDATE&token=$APIKEY";
  return uri.replace("$APIKEY", token).replace("$SYMBOL", symbol)
      .replace("$STARTDATE", startDate.toString())
      .replace("$ENDDATE", endDate.toString());    
}

}
