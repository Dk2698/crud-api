package com.kumar.crudapi.controllers;

import com.kumar.crudapi.trace.TradeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TradeController {

    private final TradeService tradeService;

    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @GetMapping("/trade/process")
    public String processTrade(@RequestParam String serviceCode) {
        tradeService.processTrade(serviceCode);
        return "Trade processed successfully!";
    }
}