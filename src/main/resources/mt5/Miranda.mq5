//+------------------------------------------------------------------+
//|                                                      ProjectName |
//|                                      Copyright 2020, CompanyName |
//|                                       http://www.companyname.net |
//+------------------------------------------------------------------+
#property version "1.00"
#property script_show_inputs

input string Address = "localhost";
input int Port = 8080;
input string Method = "PUT";
input string Path = "/ticks";
input int Timeout = 1000;
input ENUM_TIMEFRAMES TimeFrame = PERIOD_CURRENT;
input double VolumeOrder = 0.01;

//+------------------------------------------------------------------+
//|                                                                  |
//+------------------------------------------------------------------+
bool HTTPSend(const int socket, const string request) {
   char req[];
   const int len = StringToCharArray(request,req) - 1;
   if(len<0) {
      return(false);
   } else {
      return(SocketSend(socket,req,len)==len);
   }
}

//+------------------------------------------------------------------+
//|                                                                  |
//+------------------------------------------------------------------+
string HTTPReceive(const int socket) {
   do {
      const uint len = SocketIsReadable(socket);
      if(len) {
         char rsp[];
         const int rspLen = SocketRead(socket, rsp, len, Timeout);
         if(rspLen>0) {
            const string result = CharArrayToString(rsp,0,rspLen);
            const int headerEnd = StringFind(result,"\r\n\r\n");
            if(headerEnd>0) {
               return(CharArrayToString(rsp,headerEnd+4, rspLen));
            }
         }
      }
   } while(!IsStopped());
   return("");
}

//+------------------------------------------------------------------+
//|                                                                  |
//+------------------------------------------------------------------+
string systemComunication(const string dateTime, const double bid, const double ask, const string symbol, const string symbolMargin, const string symbolProfit, const int digits, const double swapLong, const double swapShort, const ENUM_DAY_OF_WEEK rateTriple, const ENUM_TIMEFRAMES timeFrame) {
   int socket=SocketCreate();
   if(socket!=INVALID_HANDLE) {
      if(SocketConnect(socket, Address, Port, Timeout)) {

         string http, host, body;
         StringConcatenate(host, Address, ":", Port);
         StringConcatenate(body, "{\n\t\"symbol\": \"",symbol,"\", \n\t\"timestamp\": \"", dateTime, "\",\n\t\"bid\": ", bid, ",\n\t\"ask\": ", ask, "\n}");
         StringConcatenate(http, Method, " ", Path, " HTTP/1.1\r\nContent-Type: application/json\r\nUser-Agent: MT5\r\nHost: ", host, "\r\nContent-Length: ", StringLen(body), "\r\n\r\n", body);

         if(HTTPSend(socket, http)) {
            //Print("host: ", host);
            //Print("body: ", body);
            //Print("http: ", http);
            const string receive = HTTPReceive(socket);
            if(StringLen(receive)!=0) {
               SocketClose(socket);
               return(receive);
            } else {
               // Print("Falha ao obter resposta, erro ",GetLastError());
            }
         } else {
            Print("[", symbol, "] falha ao enviar solicitação", Method, ", erro ",GetLastError());
         }

      } else {
         Print("[", symbol, "] falhou conexão a ",Address,":",Port,", erro ",GetLastError());
      }
   } else {
      Print("[", symbol, "] não foi possível criar o soquete, erro ",GetLastError());
   }
   SocketClose(socket);
   return("ERROR");
}

//+------------------------------------------------------------------+
//|                                                                  |
//+------------------------------------------------------------------+
string GetLocalDateTime(const string currentSymbol) {
   const int symbolTimeInfo = SymbolInfoInteger(currentSymbol, SYMBOL_TIME);
   string dateTime = TimeToString(symbolTimeInfo,TIME_DATE|TIME_SECONDS);
   StringReplace(dateTime, ".", "-");
   StringReplace(dateTime, " ", "T");
   return dateTime;
}

//+------------------------------------------------------------------+
//|                                                                  |
//+------------------------------------------------------------------+
int OnInit() {
   return(INIT_SUCCEEDED);
}
//+------------------------------------------------------------------+
//|                                                                  |
//+------------------------------------------------------------------+
void OnTick() {
   const string dateTime = GetLocalDateTime(Symbol());
   const double bid = SymbolInfoDouble(Symbol(), SYMBOL_BID);
   const double ask = SymbolInfoDouble(Symbol(), SYMBOL_ASK);
   const string symbol = Symbol();
   const string symbolMargin = SymbolInfoString(Symbol(), SYMBOL_CURRENCY_MARGIN);
   const string symbolProfit = SymbolInfoString(Symbol(), SYMBOL_CURRENCY_PROFIT);
   const int digits = SymbolInfoInteger(Symbol(), SYMBOL_DIGITS);
   const double swapLong = SymbolInfoDouble(Symbol(), SYMBOL_SWAP_LONG);
   const double swapShort = SymbolInfoDouble(Symbol(), SYMBOL_SWAP_SHORT);
   const ENUM_DAY_OF_WEEK rateTriple = SymbolInfoInteger(Symbol(), SYMBOL_SWAP_ROLLOVER3DAYS) - 1;
   const ENUM_TIMEFRAMES currentTimeFrame = (TimeFrame==PERIOD_CURRENT ? Period() : TimeFrame);
   const double point = 1.0/MathPow(10, digits);

   const string result = systemComunication(dateTime,bid,ask,symbol,symbolMargin,symbolProfit,digits,swapLong,swapShort,rateTriple,currentTimeFrame);


   if(StringFind(result, "1faa") < 0 && StringFind(result, "ERROR") < 0 && StringFind(result, "NOT NOW") < 0 && StringFind(result, "1413") && StringFind(result, "error") < 0){
      Print("[", symbol, "] RESPOSTA: ", result);
      string toOpen[];
      StringSplit(result,',',toOpen);
      for(int i=0;i<ArraySize(toOpen);i++) {
         string dados[];
         StringSplit(toOpen[i],' ',dados);

         ENUM_ORDER_TYPE orderType = StringFind(dados[2], "BUY") >= 0 ? ORDER_TYPE_BUY : ORDER_TYPE_SELL;
         double takeProfit = orderType == ORDER_TYPE_BUY ? SymbolInfoDouble(Symbol(), SYMBOL_ASK)+(StringToInteger(dados[3])*point) : SymbolInfoDouble(Symbol(), SYMBOL_BID)-(StringToInteger(dados[3])*point);
         double stopLoss = orderType == ORDER_TYPE_BUY ? SymbolInfoDouble(Symbol(), SYMBOL_ASK)-(StringToInteger(dados[4])*point) : SymbolInfoDouble(Symbol(), SYMBOL_BID)+(StringToInteger(dados[4])*point);

         Print("orderType: " + dados[2] + " openPrice: " + (ORDER_TYPE_BUY ? SymbolInfoDouble(Symbol(), SYMBOL_ASK) : SymbolInfoDouble(Symbol(), SYMBOL_BID)) + " takeProfit: " + NormalizeDouble(takeProfit, _Digits) + " stopLoss: " + NormalizeDouble(stopLoss, _Digits));

         MqlTradeRequest request={};
         MqlTradeResult  result={};

         request.action = TRADE_ACTION_DEAL;
         request.symbol = Symbol();
         request.volume = VolumeOrder;
         request.type = orderType;
         request.price = (orderType == ORDER_TYPE_BUY ? SymbolInfoDouble(Symbol(), SYMBOL_ASK) : SymbolInfoDouble(Symbol(), SYMBOL_BID));
         request.tp = NormalizeDouble(takeProfit, _Digits);
         request.sl = NormalizeDouble(stopLoss, _Digits);
         request.deviation = 0;

         if(!OrderSend(request,result)) {
            PrintFormat("OrderSend error %d",GetLastError());
         } else {
            PrintFormat("retcode=%u  deal=%I64u  order=%I64u", result.retcode,result.deal,result.order);
         }
         
      } 
   }
   
   
   
   
   
}
//+------------------------------------------------------------------+
