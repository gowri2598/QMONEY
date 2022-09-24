
package com.crio.warmup.stock;


import com.crio.warmup.stock.dto.*;
import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.crio.warmup.stock.portfolio.PortfolioManager;
import com.crio.warmup.stock.portfolio.PortfolioManagerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.client.RestTemplate;


public class PortfolioManagerApplication {

  // TODO: CRIO_TASK_MODULE_JSON_PARSING
  //RestTemplate rt=new RestTemplate();
 // PortfolioManager portfolioManager=new PortfolioManagerImpl(rt);
  public static List<String> mainReadFile(String[] args) throws IOException, URISyntaxException {

     //ObjectMapper objectMapper = new ObjectMapper();
     ObjectMapper objectMapper = getObjectMapper();
     //Trade[] trades = objectMapper.readValue(inputFile, Trade[].class);
     File file = resolveFileFromResources(args[0]);
     PortfolioTrade[] trades = objectMapper.readValue(file, PortfolioTrade[].class);
    List<String> symbol =new ArrayList<String>();
    for (PortfolioTrade trade : trades) {
      symbol.add(trade.getSymbol());
    }
     return symbol;
  }
  




  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  //  Now that you have the list of PortfolioTrade and their data, calculate annualized returns
  //  for the stocks provided in the Json.
  //  Use the function you just wrote #calculateAnnualizedReturns.
  //  Return the list of AnnualizedReturns sorted by annualizedReturns in descending order.

  // Note:
  // 1. You may need to copy relevant code from #mainReadQuotes to parse the Json.
  // 2. Remember to get the latest quotes from Tiingo API.


  public static List<TotalReturnsDto> mainReadQuotesHelper(String[] args,List<PortfolioTrade> trades)
  throws IOException, URISyntaxException {
    RestTemplate rt=new RestTemplate();
    List<TotalReturnsDto> tests = new ArrayList<TotalReturnsDto>();
    LocalDate endDate=LocalDate.parse(args[1]);
    for(PortfolioTrade trade:trades){
      
      String url= prepareUrl(trade, endDate, getToken());

      TiingoCandle[] response=rt.getForObject(url,TiingoCandle[].class);
      if(response!=null)
      {
        tests.add(new TotalReturnsDto(trade.getSymbol(), response[response.length-1].getClose()));
      }
    }
    return tests;
  }






  // TODO: CRIO_TASK_MODULE_REST_API
  //  Find out the closing price of each stock on the end_date and return the list
  //  of all symbols in ascending order by its close value on end date.

  // Note:
  // 1. You may have to register on Tiingo to get the api_token.
  // 2. Look at args parameter and the module instructions carefully.
  // 2. You can copy relevant code from #mainReadFile to parse the Json.
  // 3. Use RestTemplate#getForObject in order to call the API,
  //    and deserialize the results in List<Candle>



  private static void printJsonObject(Object object) throws IOException {
    Logger logger = Logger.getLogger(PortfolioManagerApplication.class.getCanonicalName());
    ObjectMapper mapper = new ObjectMapper();
    logger.info(mapper.writeValueAsString(object));
  }

  private static File resolveFileFromResources(String filename) throws URISyntaxException {
    return Paths.get(
        Thread.currentThread().getContextClassLoader().getResource(filename).toURI()).toFile();
  }

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }


  // TODO: CRIO_TASK_MODULE_JSON_PARSING
  //  Follow the instructions provided in the task documentation and fill up the correct values for
  //  the variables provided. First value is provided for your reference.
  //  A. Put a breakpoint on the first line inside mainReadFile() which says
  //    return Collections.emptyList();
  //  B. Then Debug the test #mainReadFile provided in PortfoliomanagerApplicationTest.java
  //  following the instructions to run the test.
  //  Once you are able to run the test, perform following tasks and record the output as a
  //  String in the function below.
  //  Use this link to see how to evaluate expressions -
  //  https://code.visualstudio.com/docs/editor/debugging#_data-inspection
  //  1. evaluate the value of "args[0]" and set the value
  //     to the variable named valueOfArgument0 (This is implemented for your reference.)
  //  2. In the same window, evaluate the value of expression below and set it
  //  to resultOfResolveFilePathArgs0
  //     expression ==> resolveFileFromResources(args[0])
  //  3. In the same window, evaluate the value of expression below and set it
  //  to toStringOfObjectMapper.
  //  You might see some garbage numbers in the output. Dont worry, its expected.
  //    expression ==> getObjectMapper().toString()
  //  4. Now Go to the debug window and open stack trace. Put the name of the function you see at
  //  second place from top to variable functionNameFromTestFileInStackTrace
  //  5. In the same window, you will see the line number of the function in the stack trace window.
  //  assign the same to lineNumberFromTestFileInStackTrace
  //  Once you are done with above, just run the corresponding test and
  //  make sure its working as expected. use below command to do the same.
  //  ./gradlew test --tests PortfolioManagerApplicationTest.testDebugValues

  public static List<String> debugOutputs() {

     String valueOfArgument0 = "trades.json";
     String resultOfResolveFilePathArgs0 = "/home/crio-user/workspace/gowri2598-ME_QMONEY_V2/qmoney/bin/main/trades.json";
     String toStringOfObjectMapper = "com.fasterxml.jackson.databind.ObjectMapper@5542c4ed";
     String functionNameFromTestFileInStackTrace = "PortfolioManagerApplicationTest.mainReadFile()";
     String lineNumberFromTestFileInStackTrace = "29:1";

    


    return Arrays.asList(new String[]{valueOfArgument0, resultOfResolveFilePathArgs0,
        toStringOfObjectMapper, functionNameFromTestFileInStackTrace,
        lineNumberFromTestFileInStackTrace});
  }

  
    
  // Note:
  // Remember to confirm that you are getting same results for annualized returns as in Module 3.
  public static List<String> mainReadQuotes(String[] args) throws IOException, URISyntaxException {
    ObjectMapper objectMapper = getObjectMapper();
    //Trade[] trades = objectMapper.readValue(inputFile, Trade[].class);
    File file = resolveFileFromResources(args[0]);
    PortfolioTrade[] trades = objectMapper.readValue(file, PortfolioTrade[].class);
    List<PortfolioTrade> trades1= Arrays.asList(trades);

    List<TotalReturnsDto> sortedList=mainReadQuotesHelper(args,trades1);
    Collections.sort(sortedList,TotalReturnsDto.closingComparator);

    List<String> stocks=new ArrayList<String>();
    for(TotalReturnsDto trd:sortedList){
      stocks.add(trd.getSymbol());
    }
     return stocks;
  }
  
 

  // TODO:
  //  After refactor, make sure that the tests pass by using these two commands
  //  ./gradlew test --tests PortfolioManagerApplicationTest.readTradesFromJson
  //  ./gradlew test --tests PortfolioManagerApplicationTest.mainReadFile
  public static List<PortfolioTrade> readTradesFromJson(String filename) throws IOException, URISyntaxException {
    File inputFile = resolveFileFromResources(filename);
    PortfolioTrade[] portfolioTrade = getObjectMapper().readValue(inputFile, PortfolioTrade[].class);
    return Arrays.asList(portfolioTrade);

  }


  // TODO:
  //  Build the Url using given parameters and use this function in your code to cann the API.


  
  public static String prepareUrl(PortfolioTrade trade, LocalDate endDate, String token) {
    String Url = "https://api.tiingo.com/tiingo/daily/"+trade.getSymbol()+"/prices?startDate="+trade.getPurchaseDate().toString()+"&endDate="+endDate+"&token="+token;
    return Url;
   }

  public static String getToken() {
    //String token = "c6e11afe3005cdfea11bde94d9429bb9adee1c95";
    String token="0e13a0db0ed1675aada089c4dd39990e527e5ec6";
    //String token="4170cea0d1c7f54ba505cc34b8aec02e24536c90";
    return token;
  }


  // TODO:
  //  Ensure all tests are passing using below command
  //  ./gradlew test --tests ModuleThreeRefactorTest
  static Double getOpeningPriceOnStartDate(List<Candle> candles) {
    return candles.get(0).getOpen();
  }


  public static Double getClosingPriceOnEndDate(List<Candle> candles) {
    return candles.get(candles.size()-1).getClose();
  }


  public static List<Candle> fetchCandles(PortfolioTrade trade, LocalDate endDate, String token) {
    String url= prepareUrl(trade, endDate, getToken());
    RestTemplate rt=new RestTemplate();
    TiingoCandle[] tingoCandles=rt.getForObject(url,TiingoCandle[].class);
    List<Candle> candleList=Arrays.asList(tingoCandles);
    return candleList;
  }
  //  Now that you have the list of PortfolioTrade and their data, calculate annualized returns
  //  for the stocks provided in the Json.
  //  Use the function you just wrote #calculateAnnualizedReturns.
  //  Return the list of AnnualizedReturns sorted by annualizedReturns in descending order.

  public static List<AnnualizedReturn> mainCalculateSingleReturn(String[] args)
      throws IOException, URISyntaxException {
        List<PortfolioTrade> trades=readTradesFromJson(args[0]);
        LocalDate endDate=LocalDate.parse(args[1]);

        return trades
        .stream()
        .map(trade -> {
        List<Candle> candleList=fetchCandles(trade, endDate, getToken()).stream()
        .filter(candle->candle.getDate().equals(trade.getPurchaseDate())||candle.getDate().equals(endDate))
        .collect(Collectors.toList());
        return mainCalculation(endDate,trade,getOpeningPriceOnStartDate(candleList),getClosingPriceOnEndDate(candleList));
        })
        .sorted(Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed())
        .collect(Collectors.toList());
  }

  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  //  Return the populated list of AnnualizedReturn for all stocks.
  //  Annualized returns should be calculated in two steps:
  //   1. Calculate totalReturn = (sell_value - buy_value) / buy_value.
  //      1.1 Store the same as totalReturns
  //   2. Calculate extrapolated annualized returns by scaling the same in years span.
  //      The formula is:
  //      annualized_returns = (1 + total_returns) ^ (1 / total_num_years) - 1
  //      2.1 Store the same as annualized_returns
  //  Test the same using below specified command. The build should be successful.
  //     ./gradlew test --tests PortfolioManagerApplicationTest.testCalculateAnnualizedReturn

  public static AnnualizedReturn mainCalculation (LocalDate endDate,
      PortfolioTrade trade, Double buyPrice, Double sellPrice) {

        
      double totalReturns=(double)(sellPrice-buyPrice)/buyPrice;
      double total_num_years = ChronoUnit.DAYS.between(trade.getPurchaseDate(), endDate)/365.24;
      double annualizedReturns = Math.pow((1 + totalReturns),(1 / total_num_years )) - 1;
      System.out.println(total_num_years);
      System.out.println(annualizedReturns);
      return new AnnualizedReturn(trade.getSymbol(),annualizedReturns,totalReturns);
    }








    

    public static List<AnnualizedReturn> mainCalculateReturnsAfterRefactor(String[] args)
    throws Exception {
    String file = args[0];
    
    PortfolioManager portfolioManager=new PortfolioManagerFactory().getPortfolioManager(new RestTemplate());
    
    LocalDate endDate = LocalDate.parse(args[1]);
    String contents = readFileAsString(file);
    
    ObjectMapper objectMapper = getObjectMapper();
    PortfolioTrade[] portfolioTrade = objectMapper.readValue(contents, PortfolioTrade[].class);
    return portfolioManager.calculateAnnualizedReturn(Arrays.asList(portfolioTrade), endDate);
    }


  private static String readFileAsString(String file) throws IOException, URISyntaxException {
    String response=new String(Files.readAllBytes(Paths.get(Thread.currentThread().getContextClassLoader().getResource(file).toURI())));
    return response;
    }





  public static void main(String[] args) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    ThreadContext.put("runId", UUID.randomUUID().toString());

    printJsonObject(mainCalculateReturnsAfterRefactor(args));
  }





  public static AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate, PortfolioTrade trade,
      double buyPrice, double sellPrice) {

        LocalDate purchaseDate = trade.getPurchaseDate();
        double years = purchaseDate.until(endDate, ChronoUnit.DAYS)/365.24;
        double totalReturns = (sellPrice - buyPrice) / buyPrice;
        double annualizedReturns = Math.pow((1+totalReturns), (1/years)) - 1; 
        return new AnnualizedReturn(trade.getSymbol(), annualizedReturns, totalReturns);
  }
}

