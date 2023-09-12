
package com.crio.warmup.stock.quotes;

import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
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
      throws StockQuoteServiceException{
        
      List<Candle> stocks=new ArrayList<Candle>();

      String url= buildUri(symbol, from, to);
      try{
      String data = restTemplate.getForObject(url,String.class);
      Candle[] tingoCandles;
      try {
        tingoCandles = getObjectMapper().readValue(data,TiingoCandle[].class);
        stocks=Arrays.asList(tingoCandles);
      } catch (JsonMappingException e) {
        // TODO Auto-generated catch block
       
      throw new StockQuoteServiceException(e.getMessage());
      } catch (JsonProcessingException e) {
        // TODO Auto-generated catch block
       
      throw new StockQuoteServiceException(e.getMessage());
      }
    

        // if(tingoCandles!=null)
        //   {
  
        //    // return stocks;
        //   }
        // else
        //   {
        //    // return stocks;//return empty coz method dictates so
        //   }
        
      } catch(NullPointerException e){
        throw new StockQuoteServiceException("Tiingo Response Invalid Return", e.getCause());
      }
      return stocks;
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





  // TODO: CRIO_TASK_MODULE_EXCEPTIONS
  //  1. Update the method signature to match the signature change in the interface.
  //     Start throwing new StockQuoteServiceException when you get some invalid response from
  //     Tiingo, or if Tiingo returns empty results for whatever reason, or you encounter
  //     a runtime exception during Json parsing.
  //  2. Make sure that the exception propagates all the way from
  //     PortfolioManager#calculateAnnualisedReturns so that the external user's of our API
  //     are able to explicitly handle this exception upfront.

  //CHECKSTYLE:OFF


}
