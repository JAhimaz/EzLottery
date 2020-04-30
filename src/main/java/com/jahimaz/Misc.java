package com.jahimaz;

import org.bukkit.Color;

public class Misc {
    public static Color getColor(String paramString) {
        String temp = paramString;
        if (temp.equalsIgnoreCase("AQUA")) return Color.AQUA;
        if (temp.equalsIgnoreCase("BLACK")) return Color.BLACK;
        if (temp.equalsIgnoreCase("BLUE")) return Color.BLUE;
        if (temp.equalsIgnoreCase("FUCHSIA")) return Color.FUCHSIA;
        if (temp.equalsIgnoreCase("GRAY")) return Color.GRAY;
        if (temp.equalsIgnoreCase("GREEN")) return Color.GREEN;
        if (temp.equalsIgnoreCase("LIME")) return Color.LIME;
        if (temp.equalsIgnoreCase("MAROON")) return Color.MAROON;
        if (temp.equalsIgnoreCase("NAVY")) return Color.NAVY;
        if (temp.equalsIgnoreCase("OLIVE")) return Color.OLIVE;
        if (temp.equalsIgnoreCase("ORANGE")) return Color.ORANGE;
        if (temp.equalsIgnoreCase("PURPLE")) return Color.PURPLE;
        if (temp.equalsIgnoreCase("RED")) return Color.RED;
        if (temp.equalsIgnoreCase("SILVER")) return Color.SILVER;
        if (temp.equalsIgnoreCase("TEAL")) return Color.TEAL;
        if (temp.equalsIgnoreCase("WHITE")) return Color.WHITE;
        if (temp.equalsIgnoreCase("YELLOW")) return Color.YELLOW;
        return null;
    }

    public static String isEnabledString(Boolean bool){
        if(bool){
            return "Enabled";
        }else{
            return "Disabled";
        }
    }

}
