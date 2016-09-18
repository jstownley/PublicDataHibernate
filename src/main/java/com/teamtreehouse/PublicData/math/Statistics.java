package com.teamtreehouse.PublicData.math;

public class Statistics {

    public static double getMin(double[] a) {
        double min = a[0];
        for (int ii=1; ii<a.length; ii++) {
            min = a[ii] < min ? a[ii] : min;
        }
        return min;
    }

    public static double getMax(double[] a) {
        double max = a[0];
        for (int ii=1; ii<a.length; ii++) {
            max = a[ii] > max ? a[ii] : max;
        }
        return max;
    }

    public static double getMean(double[] a) {
        double sum = 0.0;
        for (int ii=0; ii<a.length; ii++) {
            sum += a[ii];
        }
        return sum/a.length;
    }
}
