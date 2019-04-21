package com.metcalfe.lowell.FarmRouteNav;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PathFinder {
    static int IS_EASTING = 2;
    static int IS_NORTHING = 3;

    /**
     * this function is usedd to retrieve a desiered part of a utm string
     *
     * @param utmString
     * @param EastingOrNorthing
     * @return STRING: sends back the desiered part of the UTM string, as an string
     */
    private static String GetUTMValue(String utmString, int EastingOrNorthing) {
        String value = (utmString.split(" ")[EastingOrNorthing]);
        return value;
    }

    /**
     * the plural version of getting a singular value
     *
     * @param utmStrings
     * @param EastingOrNorthing
     * @return
     */
    private static int[] GetUTMValues(String[] utmStrings, int EastingOrNorthing) {
        int[] values = new int[utmStrings.length];
        for (int i = 0; i < utmStrings.length; i++) {
            values[i] = Integer.parseInt(utmStrings[i].split(" ")[EastingOrNorthing]);
        }
        return values;
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


    /**
     * performs the route finding logic
     * @param UTMs              an array of all of the coordinates already in UTM form
     * @param implementWidth    the size of the vehicle
     */
    public void FindRoute(String[] UTMs, int implementWidth) {
        int[] Eastings = GetUTMValues(UTMs, IS_EASTING);
        int[] Northings = GetUTMValues(UTMs, IS_NORTHING);
        int[] currentPos = new int[2];
        String UtmZoneNumber = GetUTMValue(UTMs[0], 0);
        String UtmZoneLetter = GetUTMValue(UTMs[0], 1);
        //this is the first value that was entered by the user, so we take this as their start
        int[] startPos = new int[]{Eastings[0], Northings[0]};
        //sorts in ascending order
        Arrays.sort(Eastings);
        Arrays.sort(Northings);
        int xLength = Eastings[Eastings.length - 1] - Eastings[0];
        int yLength = Northings[Northings.length - 1] - Northings[0];
        int xHighBoundary = Eastings[Eastings.length - 1];
        int xLowBoundary = Eastings[0];
        int yHighBoundary = Northings[Northings.length - 1];
        int yLowBoundary = Northings[0];
        List<String> lineRecord = new ArrayList<>();
        //add the first position to the path
        //lineRecord.add(UtmZoneNumber + " " + UtmZoneLetter + " " + startPos[0] + " " + startPos[1]);
        int direction = implementWidth;
        currentPos = startPos;
        for (int i = 0; i < (xLength * yLength) / implementWidth; i++) {
            if (xLength > yLength) {//if it is a horizontal shape
                if (currentPos[0] + direction < xHighBoundary && currentPos[0] + direction > xLowBoundary) {
                    if (currentPos[1] + implementWidth < yHighBoundary && currentPos[1] + implementWidth > yLowBoundary) {
                        lineRecord.add(UtmZoneNumber + " " + UtmZoneLetter + " " + currentPos[0] + " " + currentPos[1]);
                        currentPos[0] += direction;
                    } else {
                        break;
                    }
                } else {
                    lineRecord.add(UtmZoneNumber + " " + UtmZoneLetter + " " + currentPos[0] + " " + currentPos[1]);
                    currentPos[1] += implementWidth;
                    //invert direction
                    direction *= -1;
                }
            } else {//it is a vertical shape
                if (currentPos[1] + direction < yHighBoundary && currentPos[1] + direction > yLowBoundary) {
                    if (currentPos[0] + implementWidth < xHighBoundary && currentPos[0] + implementWidth > xLowBoundary) {
                        lineRecord.add(UtmZoneNumber + " " + UtmZoneLetter + " " + currentPos[0] + " " + currentPos[1]);
                        currentPos[1] += direction;
                    } else {
                        break;
                    }
                } else {
                    lineRecord.add(UtmZoneNumber + " " + UtmZoneLetter + " " + currentPos[0] + " " + currentPos[1]);
                    currentPos[0] += implementWidth;
                    //invert direction
                    direction *= -1;
                }
            }
        }
        BuildLine(Arrays.copyOf(lineRecord.toArray(),lineRecord.size(),String[].class));
    }

    /**
     * converts the line from UTM type to LatLng then calls the display function to print it
     * @param lineRecord the UTM coordinates for the route
     */
    public void BuildLine(String[] lineRecord) {
        LatLng[] linePositions = new LatLng[lineRecord.length];
        CoordinateConversion CC = new CoordinateConversion();
        double[] LatLongResult;
        //go through each element of the list
        for (int i = 0; i < lineRecord.length; i++) {
            //convert the UTM to Latitude and Longitude as Doubles
            LatLongResult = CC.utm2LatLon(lineRecord[i]);
            //turns the two doubles into a LatLng and places it into the array
            linePositions[i] = new LatLng(LatLongResult[0], LatLongResult[1]);
        }
        //sends the line builder an array of LatLng coordinates.
        MainActivity.PlotLine(linePositions);
    }

}

