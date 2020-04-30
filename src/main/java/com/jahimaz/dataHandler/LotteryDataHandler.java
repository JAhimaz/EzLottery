package com.jahimaz.dataHandler;


public class LotteryDataHandler {
    public static int convertSecondsToTicks(int ticks){
        int seconds = ticks*20;
        return seconds;
    }

    public static double convertPercentageToDecimal(int percentage){
        double decimal = percentage / 100;
        return decimal;
    }

    public static double calculateChance(){
        return 0.0;
    }
}
