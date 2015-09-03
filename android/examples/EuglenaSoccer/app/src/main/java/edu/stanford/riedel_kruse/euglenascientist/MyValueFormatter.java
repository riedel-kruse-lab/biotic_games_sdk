package edu.stanford.riedel_kruse.euglenascientist;

import com.github.mikephil.charting.utils.ValueFormatter;

import java.text.DecimalFormat;

/**
 * Created by honestykim on 8/5/2015.
*/
public class MyValueFormatter implements ValueFormatter {

    private DecimalFormat mFormat;

    public MyValueFormatter() {
        mFormat = new DecimalFormat("###,###,###.###"); // use one decimal
    }

    @Override
    public String getFormattedValue(float value) {
        return mFormat.format(value); // append a dollar-sign
    }
}