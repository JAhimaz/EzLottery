package com.jahimaz.dataHandler;


public class LotteryDataHandler {
    public static int convertSecondsToTicks(int ticks){
        int seconds = ticks*20;
        return seconds;
    }

    public static double convertPercentageToDecimal(int percentage){
        double decimal = (double) percentage / 100.00;
        return decimal;
    }

    public static double calculateChance(){
        return 0.0;
    }

    public static String timeHandler(int seconds){
        long sec = seconds % 60;
        long minutes = seconds % 3600 / 60;
        long hours = seconds % 86400 / 3600;

        String sSec = String.format("%02d",sec);
        String sMin = String.format("%02d", minutes);
        String sHr = String.format("%02d", hours);

        return sHr + "H : " + sMin + "M : " + sSec + "S";
    }
}
