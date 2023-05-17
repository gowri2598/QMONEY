
package com.crio.warmup.stock.portfolio;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;
import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.crio.warmup.stock.quotes.StockQuotesService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerImpl implements PortfolioManager {

  RestTemplate restTemplate;
  StockQuotesService stockQuotesService;


  // Caution: Do not delete or modify the constructor, or else your build will break!
  // This is absolutely necessary for backward compatibility
  protected PortfolioManagerImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }
  protected PortfolioManagerImpl(StockQuotesService stockQuotesService) {
    this.stockQuotesService = stockQuotesService;
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
  //  Remember to fill out the buildUri function and use that.//sat->thur,sun->fri
  public static LocalDate getLastWorkingDate (LocalDate date){
    return (date.getDayOfWeek().toString()=="SATURDAY")?date.minus(2, ChronoUnit.DAYS):(date.getDayOfWeek().toString()=="SUNDAY")?date.minus(2, ChronoUnit.DAYS):date;
  }

  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws StockQuoteServiceException, JsonProcessingException {
        return stockQuotesService.getStockQuote(symbol, from, to);
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
      LocalDate endDate) throws StockQuoteServiceException, JsonProcessingException{
        
        List<AnnualizedReturn> anuallist=new ArrayList<>();
        
        
        for(PortfolioTrade pft:portfolioTrades){

            
          List<Candle> candleList;
        
          candleList = getStockQuote(pft.getSymbol(), pft.getPurchaseDate(), endDate);
            AnnualizedReturn anualizedOutput= mainCalculation(endDate,pft,getOpeningPriceOnStartDate(candleList),getClosingPriceOnEndDate(candleList));          
          anuallist.add(anualizedOutput);

          } 

        return anuallist.stream().sorted(getComparator()).collect(Collectors.toList());

      //   return portfolioTrades
      //   .stream()
      //   .map(portfolioTrade -> {
      //   List<Candle> candleList=new ArrayList<>();
      
      //     try {
      //       if(stockQuotesService != null){
      //         candleList = stockQuotesService.getStockQuote(portfolioTrade.getSymbol(),portfolioTrade.getPurchaseDate(),endDate);
      //       }
      //       else{
      //         candleList = stockQuotesService.getStockQuote(portfolioTrade.getSymbol(),portfolioTrade.getPurchaseDate(),endDate);
      //       }
      //       candleList.stream().
      //       filter(candle->candle.getDate().equals(portfolioTrade.getPurchaseDate())||candle.getDate().equals(getLastWorkingDate(endDate)))
      //       .collect(Collectors.toList());

      //     } catch (StockQuoteServiceException e) {
      //       // TODO Auto-generated catch block
           
      //     }
      //     return mainCalculation(endDate,portfolioTrade,getOpeningPriceOnStartDate(candleList),getClosingPriceOnEndDate(candleList));
        
      //    //System.out.print(candleList);
       
      //   })
      //   .sorted(getComparator())
      //   .collect(Collectors.toList());    
      // }

      }
  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturnParallel(
      List<PortfolioTrade> portfolioTrades, LocalDate endDate, int numThreads)
      throws InterruptedException, StockQuoteServiceException {
    // TODO Auto-generated method stub

    List<AnnualizedReturn> anuallist=new ArrayList<>();
    
    final ExecutorService pool = Executors.newFixedThreadPool(numThreads);
    List<Future<AnnualizedReturn>> futureList = new ArrayList<Future<AnnualizedReturn>>();
      
        
    for(PortfolioTrade pft:portfolioTrades){
         
      Callable<AnnualizedReturn> callableTask = () -> {
      List<Candle> candleList = getStockQuote(pft.getSymbol(), pft.getPurchaseDate(), endDate);
      //AnnualizedReturn anualizedOutput=
      return mainCalculation(endDate,pft,getOpeningPriceOnStartDate(candleList),getClosingPriceOnEndDate(candleList));          
      };
      Future<AnnualizedReturn> futureReturns = pool.submit(callableTask);
      futureList.add(futureReturns);

    }
    
    for (int i = 0; i < portfolioTrades.size(); i++) {
      Future<AnnualizedReturn> futureReturns = futureList.get(i);
      try {
        AnnualizedReturn returns = futureReturns.get();
        anuallist.add(returns);
      } catch (ExecutionException e) {
        throw new StockQuoteServiceException("Error when calling the API", e);
      }
    }
    
    return anuallist.stream().sorted(getComparator()).collect(Collectors.toList());
  }
}