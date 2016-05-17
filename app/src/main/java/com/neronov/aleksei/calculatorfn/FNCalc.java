package com.neronov.aleksei.calculatorfn;

import android.util.Log;

import java.text.DecimalFormat;
import java.util.Arrays;

public class FNCalc {
    private static final String TAG = MainActivity.class.getSimpleName();
    double accumulator;

    public void clear() {
        accumulator = 0;
    }

    // арифметические методы
    public void add(double value)
    {
        accumulator+=value;
    }

    public void mult(double value)
    {
        accumulator*=value;
    }

    public void min(double value)
    {
        accumulator-=value;
    }

    public void dev(double value)
    {
        if (value!=0) accumulator/=value;
        else clear();
    }

    public void pow(double value)
    {
        if (value != 0) accumulator= Math.pow(accumulator,value);
        else clear();
    }

//метод определения арифмитической операции
    public void checkCalcOper(int oper ,double value)
    {
        if (oper==0)
        {
            accumulator=value;
        }
        if (oper==1) mult(value);
        if (oper==2) add(value);
        if (oper==3) min(value);
        if (oper==4) dev(value);
        if (oper==5) pow(value);
    }

    public String format(double d, int length) {
        if(d < 0) length--;
        int exponent = (int)Math.log10( Math.abs(d) ) + 1;
        DecimalFormat format = null;
        if(exponent == length) {
            format = new DecimalFormat(String.valueOf(createCharArray(length)));
        } else
        if( exponent > 0) {
            if( exponent > length ) {
                format = new DecimalFormat("0." + String.valueOf(createCharArray(length-4)) + "E0");
            } else {
                char[] c = createCharArray(length);
                c[exponent] = '.';
                format = new DecimalFormat(String.valueOf(c));
            }
        } else {
            format = new DecimalFormat("0." + String.valueOf(createCharArray(length-5)) + "E0");
        }
        return format.format(d);
    }

    public static char[] createCharArray(int length) {
        char[] c = new char[length];
        Arrays.fill(c, '#');
        return c;
    }

    // Функция обрезания десятых и лишних нулей
    public static String completeToScreen(double result){
        Log.d(TAG, "± completeToScreen before format d='" + result+"'");
        int length = 16;
        double d = result;
        if(d < 0) length--;
        int exponent = (int)Math.log10( Math.abs(d) ) + 1;
        DecimalFormat format = null;

        if(exponent == length) {
            format = new DecimalFormat(String.valueOf(createCharArray(length)));
        } else
        if( exponent > 0) {
            if( exponent > length ) {
//                format = new DecimalFormat("0." + String.valueOf(createCharArray(length-4)) + "E0");
                format = new DecimalFormat("0." + String.valueOf(createCharArray(length-4)));
            } else {
                char[] c = createCharArray(length);
                c[exponent] = '.';
                format = new DecimalFormat(String.valueOf(c));
            }
        } else {
            format = new DecimalFormat("0." + String.valueOf(createCharArray(length-5)));
        }
        String forReturn=format.format(d);
        forReturn=forReturn.replace(",", ".");
        if (forReturn.equals("0E0")) forReturn="0";
        if (Double.isNaN(result)) {
            forReturn = "0";
            Log.d(TAG, "± completeToScreen NaN detected!!!");
        }

        Log.d(TAG, "± completeToScreen after format d='" + format.format(d)+"' forResult='"+forReturn+"' result=" + result+"'");
        return forReturn;
    }


}
