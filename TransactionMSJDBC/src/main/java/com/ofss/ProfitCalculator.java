package com.ofss;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ProfitCalculator {

    static class Lot {
        int volume;
        double pricePerShare;
        Lot(int volume, double pricePerShare) {
            this.volume = volume;
            this.pricePerShare = pricePerShare;
        }
    }

    public static double calculateRealizedProfit(List<Transaction> txns, Transaction currentSell) {
        List<Lot> remainingLots = new ArrayList<>();
        // Build remaining lots from all prior transactions, up to this SELL
        for (Transaction t : txns) {
            if (t.getTransactionId() != null && currentSell.getTransactionId() != null &&
                t.getTransactionId().equals(currentSell.getTransactionId())) break; // stop at this sell
            if (t.getTransactionType().equalsIgnoreCase("BUY")) {
                double pricePerShare = t.getTransactionPrice() / t.getVolume();
                remainingLots.add(new Lot(t.getVolume(), pricePerShare));
            } else if (t.getTransactionType().equalsIgnoreCase("SELL")) {
                int sellVol = t.getVolume();
                while (sellVol > 0 && !remainingLots.isEmpty()) {
                    Lot lot = Collections.min(remainingLots, Comparator.comparingDouble(l -> l.pricePerShare)); // LOFO
                    int matchedVol = Math.min(sellVol, lot.volume);
                    lot.volume -= matchedVol;
                    if(lot.volume == 0) remainingLots.remove(lot);
                    sellVol -= matchedVol;
                }
            }
        }
        // Now, for this sell, match against those lots
        double sellPricePerShare = currentSell.getTransactionPrice() / currentSell.getVolume();
        int volToSell = currentSell.getVolume();
        double realizedProfit = 0;
        while (volToSell > 0 && !remainingLots.isEmpty()) {
            Lot lot = Collections.min(remainingLots, Comparator.comparingDouble(l -> l.pricePerShare));
            int matchedVol = Math.min(volToSell, lot.volume);
            realizedProfit += (sellPricePerShare - lot.pricePerShare) * matchedVol;
            lot.volume -= matchedVol;
            if (lot.volume == 0) remainingLots.remove(lot);
            volToSell -= matchedVol;
        }
        return realizedProfit;
    }
}
