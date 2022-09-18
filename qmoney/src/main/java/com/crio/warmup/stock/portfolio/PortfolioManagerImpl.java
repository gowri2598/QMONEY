
package com.crio.warmup.stock.portfolio;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;
import com.crio.warmup.stock.PortfolioManagerApplication;
import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerImpl implements PortfolioManager {

  RestTemplate restTemplate;


  // Caution: Do not delete or modify the constructor, or else your build will break!
  // This is absolutely necessary for backward compatibility
  protected PortfolioManagerImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }


  //TODO: CRIO_TASK_MODULE_REFACTOR
  // 1. Now we want to convert our code into a module, so we will not call it from main anymore.
  //    Copy your code from Module#3 PortfolioManagerApplication#calculateAnnualizedReturn
  //    into #calculateAnnualizedReturn function here and ensure it follows the method signature.
  // 2. Logic to read Json file and convert them into Objects will not be required further as our
  //    clients will take care of it, going forward.

  // Note:
  // Make sure to exercise the tests inside PortfolioManagerTest using command below:
  // ./gradlew test --tests PortfolioManagerTest

  //CHECKSTYLE:OFF


  static Double getOpeningPriceOnStartDate(List<Candle> candles) {
    return candles.get(0).getOpen();
  }


  public static Double getClosingPriceOnEndDate(List<Candle> candles) {
    return candles.get(candles.size()-1).getClose();
  }

  private static Comparator<AnnualizedReturn> getComparator() {
    return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  }

  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Extract the logic to call Tiingo third-party APIs to a separate function.
  //  Remember to fill out the buildUri function and use that.


  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException {
    String url= buildUri(symbol, from, to);
    //RestTemplate rt=new RestTemplate();
    TiingoCandle[] tingoCandles=restTemplate.getForObject(url,TiingoCandle[].class);
    if(tingoCandles!=null)
      {
        List<Candle> candleList=Arrays.asList(tingoCandles);
        return candleList;
      }
    else{
      return new ArrayList<Candle>();//return empty coz method dictates so
    }
  }

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
       String token="c6e11afe3005cdfea11bde94d9429bb9adee1c95";
       String uriTemplate = "https:api.tiingo.com/tiingo/daily/$SYMBOL/prices?"
            + "startDate=$STARTDATE&endDate=$ENDDATE&token=$APIKEY";
       
        uriTemplate.replace("$SYMBOL",symbol).replace("$STARTDATE",startDate.toString())
        .replace("$ENDDATE",endDate.toString()).replace("$APIKEY", token);

       return uriTemplate;
  }
  public static AnnualizedReturn mainCalculation (LocalDate endDate,
  PortfolioTrade trade, Double buyPrice, Double sellPrice) {

    
  double totalReturns=(double)(sellPrice-buyPrice)/buyPrice;
  double total_num_years = ChronoUnit.DAYS.between(trade.getPurchaseDate(), endDate)/365.24;
  double annualizedReturns = Math.pow((1 + totalReturns),(1 / total_num_years )) - 1;
  System.out.println(total_num_years);
  System.out.println(annualizedReturns);
  return new AnnualizedReturn(trade.getSymbol(),annualizedReturns,totalReturns);
}


  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades,
      LocalDate endDate) {
    
        return portfolioTrades
        .stream()
        .map(portfolioTrade -> {
        List<Candle> candleList=new ArrayList<>();
        try {
          candleList = getStockQuote(portfolioTrade.getSymbol(),portfolioTrade.getPurchaseDate(),endDate).stream()
          .filter(candle->candle.getDate().equals(portfolioTrade.getPurchaseDate())||candle.getDate().equals(endDate))
          .collect(Collectors.toList());
        } catch (JsonProcessingException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        return mainCalculation(endDate,portfolioTrade,getOpeningPriceOnStartDate(candleList),getClosingPriceOnEndDate(candleList));
        })
        .sorted(getComparator())
        .collect(Collectors.toList());    
  }


}