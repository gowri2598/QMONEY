
package com.crio.warmup.stock.quotes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.crio.warmup.stock.dto.AlphavantageCandle;
import com.crio.warmup.stock.dto.AlphavantageDailyResponse;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.web.client.RestTemplate;

public class AlphavantageService implements StockQuotesService {

  private static final String TIME_SERIES_DAILY = null;
  RestTemplate restTemplate;


  // Caution: Do not delete or modify the constructor, or else your build will break!
  // This is absolutely necessary for backward compatibility
  protected AlphavantageService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }


  @Override
  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws StockQuoteServiceException{
    // TODO Auto-generated method stub
    String url= buildUri(symbol);
    List<Candle> stocks=new ArrayList<>();
    try{
    String data = restTemplate.getForObject(url,String.class);

    Map<LocalDate, AlphavantageCandle> dailyResponse;
    try {
      dailyResponse = getObjectMapper().readValue(data,AlphavantageDailyResponse.class).getCandles();
    } catch (JsonMappingException e) {
      // TODO Auto-generated catch block
      // e.printStackTrace();
      throw new StockQuoteServiceException(e.getMessage());
    } catch (JsonProcessingException e) {
      
      // TODO Auto-generated catch block
      // e.printStackTrace();
      throw new StockQuoteServiceException(e.getMessage());
    } 
     //System.out.print("dailyResponse is ______________:"+dailyResponse);    

    for(LocalDate date=from;!date.isAfter(to);date=date.plusDays(1)){
      AlphavantageCandle candle=dailyResponse.get(date);
      if(candle!=null)
      {
        candle.setDate(date);
        stocks.add(candle);      
      }
    }  
    // return stocks;
  } catch(NullPointerException e){
    //  throw e;
    throw new StockQuoteServiceException("{\"Information\": \"The **demo** API key is for demo purposes only. "
    + "Please claim your free API key at (https://www.alphavantage.co/support/#api-key) to "
    + "explore our full API offerings. It takes fewer than 20 seconds, and we are committed to "
    + "making it free forever.\"}", e);
  }
  return stocks;
}

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }
//https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=AAPL&output=full&apikey=HV4DI67OR0M0D2OL
//https://www.alphavantage.co/query?function=TIME_SERIES_DAILY_ADJUSTED&symbol=AAPL&outputsize=full&apikey=HV4DI67OR0M0D2OL
  protected String buildUri(String symbol) {
    
    // String token = "HV4DI67OR0M0D2OL";
    String token ="0K4LCXEQZW4RSEE6";
    // String function="TIME_SERIES_DAILY_ADJUSTED";
    // String uri = "https://www.alphavantage.co/query?function=$FUNCTION&symbol=$SYMBOL&apikey=$API_KEY"; 
    // System.out.println(uri.replace("$FUNCTION",function).replace("$APIKEY", token).replace("$SYMBOL", symbol)); 
    // return uri.replace("$FUNCTION",function).replace("$APIKEY", token).replace("$SYMBOL", symbol);    

    return "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY_ADJUSTED&symbol="+symbol+"&outputsize=full&apikey="+token;
  }

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Implement the StockQuoteService interface as per the contracts. Call Alphavantage service
  //  to fetch daily adjusted data for last 20 years.
  //  Refer to documentation here: https://www.alphavantage.co/documentation/
  //  --
  //  The implementation of this functions will be doing following tasks:
  //    1. Build the appropriate url to communicate with third-party.
  //       The url should consider startDate and endDate if it is supported by the provider.
  //    2. Perform third-party communication with the url prepared in step#1
  //    3. Map the response and convert the same to List<Candle>
  //    4. If the provider does not support startDate and endDate, then the implementation
  //       should also filter the dates based on startDate and endDate. Make sure that
  //       result contains the records for for startDate and endDate after filtering.
  //    5. Return a sorted List<Candle> sorted ascending based on Candle#getDate
  //  IMP: Do remember to write readable and maintainable code, There will be few functions like
  //    Checking if given date falls within provided date range, etc.
  //    Make sure that you write Unit tests for all such functions.
  //  Note:
  //  1. Make sure you use {RestTemplate#getForObject(URI, String)} else the test will fail.
  //  2. Run the tests using command below and make sure it passes:
  //    ./gradlew test --tests AlphavantageServiceTest
  //CHECKSTYLE:OFF
    //CHECKSTYLE:ON
  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  1. Write a method to create appropriate url to call Alphavantage service. The method should
  //     be using configurations provided in the {@link @application.properties}.
  //  2. Use this method in #getStockQuote.
  // TODO: CRIO_TASK_MODULE_EXCEPTIONS
  //   1. Update the method signature to match the signature change in the interface.
  //   2. Start throwing new StockQuoteServiceException when you get some invalid response from
  //      Alphavantage, or you encounter a runtime exception during Json parsing.
  //   3. Make sure that the exception propagates all the way from PortfolioManager, so that the
  //      external user's of our API are able to explicitly handle this exception upfront.
  //CHECKSTYLE:OFF

}