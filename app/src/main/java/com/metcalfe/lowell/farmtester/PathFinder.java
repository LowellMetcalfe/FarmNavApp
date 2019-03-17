package com.metcalfe.lowell.farmtester;

import com.google.android.gms.maps.model.LatLng;

public class PathFinder {
    //public static List<Integer> eastingList;
    //public static List<Integer> northingList;
    static int IS_EASTING = 2;
    static int IS_NORTHING = 3;

    /**
     * this function is usedd to retrieve a desiered part of a utm string
     *
     * @param utmString
     * @param EastingOrNorthing
     * @return INTEGER: sends back the desiered part of the UTM string, as an integer
     */
    private static Integer GetUTMValue(String utmString, int EastingOrNorthing) {
//        String valueString = "";
//        valueString = utmString.split(" ")[EastingOrNorthing];
        int value = Integer.parseInt(utmString.split(" ")[EastingOrNorthing]);
        //value = Integer.parseInt(valueString);
        return value;
    }

    private static Integer GetNorthing(String utmString) {
        int northing;
        String northingString = utmString.split("")[3];
        northing = Integer.parseInt(northingString);
        return northing;
    }

    /**
     * send the string andd the value, then seperate it up, change it and put it back together
     *
     * @param utmString         the previous UTM value
     * @param value             the value that is making the correction
     * @param EastingOrNorthing this value notates if it is working on easting or northing
     * @return the result of constructing the new UTM string
     */
    private static String SetNewUTMValue(String utmString, int value, int EastingOrNorthing) {
        String[] splitUpUtm = utmString.split(" ");
        //the 2 here notates the easting value
        splitUpUtm[EastingOrNorthing] = Integer.toString(value);
        return utmString.join(" ", splitUpUtm);
    }

    //TODO put the algorithm here
    public void FindRoute(String[] UTMs) {
        for (int i = 0; i < UTMs.length; i++) {
            UTMs[i] = SetNewUTMValue(UTMs[i], GetUTMValue(UTMs[i], IS_EASTING) + 300, IS_EASTING);
            //SetNewUTMValue(UTMs[i], GetUTMValue(UTMs[i], IS_NORTHING) + 1000, IS_NORTHING);
        }
        BuildLine(UTMs);
    }

    public void BuildLine(String[] UTMs) {
        LatLng[] linePositions = new LatLng[UTMs.length];
        CoordinateConversion CC = new CoordinateConversion();
        double [] LatLongResult;
        //go through each vertex of the list
        for (int i = 0; i < UTMs.length; i++) {
            LatLongResult= CC.utm2LatLon(UTMs[i]);
            linePositions[i] = new LatLng(LatLongResult[0],LatLongResult[1]);
        }
        MainActivity.PlotLine(linePositions);
    }

}

