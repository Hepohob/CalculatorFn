package com.neronov.aleksei.calculatorfn;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import static com.neronov.aleksei.calculatorfn.FNCalc.completeToScreen;

public class MainActivity extends AppCompatActivity {
    public static GoogleAnalytics analytics;
    public static Tracker tracker;

    public final String[] mItemsArray = new String[]{"cos", "sin", "tan",
            "sinh", "cosh", "tanh", "acos", "asin", "atan",
            "π", "e", "x!", "log2", "log10", "ln",
            "1/x", "10^x", "x^2", "x^3", "exp(x)", "expm1(x)",
            "x^y", "round"
    };

    private static final String TAG = MainActivity.class.getSimpleName();

    public String[] fnArrayOfString = new String[8];

    // это будет именем файла настроек
    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_FN1 = "fn1";
    public static final String APP_PREFERENCES_FN2 = "fn2";
    public static final String APP_PREFERENCES_FN3 = "fn3";
    public static final String APP_PREFERENCES_FN4 = "fn4";
    public static final String APP_PREFERENCES_FN5 = "fn5";
    public static final String APP_PREFERENCES_FN6 = "fn6";
    public static final String APP_PREFERENCES_FN7 = "fn7";
    public static final String APP_PREFERENCES_FN8 = "fn8";
    private SharedPreferences mSettings;

    double buffer;

    {
        buffer = 0;
    }

    double mem;

    {
        mem = 0;
    }

    int oper;

    {
        oper = 0;
    }

    int fButtonNumber;

    {
        fButtonNumber = 0;
    }

    int fButton;

    {
        fButton = 0;
    }

    int digitsCount;

    {
        digitsCount = 16;
    }

    boolean perv;

    {
        perv = false;
    }

    boolean dot;

    {
        dot = false;
    }

    boolean percent;

    {
        percent = false;
    }

    boolean memRC;

    {
        memRC = false;
    }

    FNCalc fnCalc;

    {
        fnCalc = new FNCalc();
    }

    private TextView mScreenView;
    private TextView mSignDisplayView;

    private Button mPercentButton;
    private Button mMemRCButton;

    private Button mButtonF1;
    private Button mButtonF2;
    private Button mButtonF3;
    private Button mButtonF4;
    private Button mButtonF5;
    private Button mButtonF6;
    private Button mButtonF7;
    private Button mButtonF8;


    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);


        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);

        tracker = analytics.newTracker("UA-49982138-14"); // Replace with actual tracker/property Id
        tracker.enableExceptionReporting(true);
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableAutoActivityTracking(true);


        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        Log.d(TAG, "± Start calc");

        Runtime rt = Runtime.getRuntime();
        long maxMemory = rt.maxMemory();
        Log.d(TAG, "± maxMemory:" + Long.toString(maxMemory));

        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        int memoryClass = am.getMemoryClass();
        Log.d(TAG, "± memoryClass:" + Integer.toString(memoryClass));

        mScreenView = (TextView) findViewById(R.id.screen);
        mSignDisplayView = (TextView) findViewById(R.id.signdisplay);
        mPercentButton = (Button) findViewById(R.id.buttonPercent);
        mMemRCButton = (Button) findViewById(R.id.buttonMRC);

        mButtonF1 = (Button) findViewById(R.id.buttonF1);
        mButtonF2 = (Button) findViewById(R.id.buttonF2);
        mButtonF3 = (Button) findViewById(R.id.buttonF3);
        mButtonF4 = (Button) findViewById(R.id.buttonF4);
        mButtonF5 = (Button) findViewById(R.id.buttonF5);
        mButtonF6 = (Button) findViewById(R.id.buttonF6);
        mButtonF7 = (Button) findViewById(R.id.buttonF7);
        mButtonF8 = (Button) findViewById(R.id.buttonF8);

        for (int i = 0; i < 8; i++) {
            fnArrayOfString[i] = mItemsArray[i];
        }

        if (mSettings.contains(APP_PREFERENCES_FN1)) {
            // Получаем число из настроек
            fnArrayOfString[0] = mSettings.getString(APP_PREFERENCES_FN1, "mItemsArray[i]");
            fnArrayOfString[1] = mSettings.getString(APP_PREFERENCES_FN2, "mItemsArray[i]");
            fnArrayOfString[2] = mSettings.getString(APP_PREFERENCES_FN3, "mItemsArray[i]");
            fnArrayOfString[3] = mSettings.getString(APP_PREFERENCES_FN4, "mItemsArray[i]");
            fnArrayOfString[4] = mSettings.getString(APP_PREFERENCES_FN5, "mItemsArray[i]");
            fnArrayOfString[5] = mSettings.getString(APP_PREFERENCES_FN6, "mItemsArray[i]");
            fnArrayOfString[6] = mSettings.getString(APP_PREFERENCES_FN7, "mItemsArray[i]");
            fnArrayOfString[7] = mSettings.getString(APP_PREFERENCES_FN8, "mItemsArray[i]");
        } else {
            // Запоминаем данные
            saveFnKeys();
        }

        mButtonF1.setText(fnArrayOfString[0]);
        mButtonF2.setText(fnArrayOfString[1]);
        mButtonF3.setText(fnArrayOfString[2]);
        mButtonF4.setText(fnArrayOfString[3]);
        mButtonF5.setText(fnArrayOfString[4]);
        mButtonF6.setText(fnArrayOfString[5]);
        mButtonF7.setText(fnArrayOfString[6]);
        mButtonF8.setText(fnArrayOfString[7]);

        String itemString = "";

        Intent intent1 = getIntent();
        fButton = intent1.getIntExtra("fButton", 0);
        itemString = intent1.getStringExtra("item");
//        Toast.makeText(getApplicationContext(), itemString + " f" + fButton, Toast.LENGTH_SHORT).show();
        if ((itemString != null) && (itemString.trim().length() > 0)) {

            buffer = intent1.getDoubleExtra("buffer", 0);
            mem = intent1.getDoubleExtra("mem", 0);
            oper = intent1.getIntExtra("oper", 0);
            perv = intent1.getBooleanExtra("perv", false);
            dot = intent1.getBooleanExtra("dot", false);
            percent = intent1.getBooleanExtra("percent", false);
            memRC = intent1.getBooleanExtra("memRC", false);
            Log.d(TAG, "± !!!buffer=" + buffer + " mem=" + mem + " oper=" + oper + " perv=" + perv + " dot=" + dot + " percent=" + percent + " memRC=" + memRC);
            switch (fButton) {
                case 1:
                    mButtonF1.setText(itemString);
                    fnArrayOfString[0] = itemString;
                    break;

                case 2:
                    mButtonF2.setText(itemString);
                    fnArrayOfString[1] = itemString;
                    break;

                case 3:
                    mButtonF3.setText(itemString);
                    fnArrayOfString[2] = itemString;
                    break;

                case 4:
                    mButtonF4.setText(itemString);
                    fnArrayOfString[3] = itemString;
                    break;

                case 5:
                    mButtonF5.setText(itemString);
                    fnArrayOfString[4] = itemString;
                    break;

                case 6:
                    mButtonF6.setText(itemString);
                    fnArrayOfString[5] = itemString;
                    break;

                case 7:
                    mButtonF7.setText(itemString);
                    fnArrayOfString[6] = itemString;
                    break;

                case 8:
                    mButtonF8.setText(itemString);
                    fnArrayOfString[7] = itemString;
                    break;

                default:
                    break;
            }
            // Запоминаем данные
            saveFnKeys();
        }

        mButtonF1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                intent.putExtra("fButton", 1);
                intent.putExtra("buffer", buffer);
                intent.putExtra("mem", mem);
                intent.putExtra("oper", oper);
                intent.putExtra("perv", perv);
                intent.putExtra("dot", dot);
                intent.putExtra("percent", percent);
                intent.putExtra("memRC", memRC);
                startActivity(intent);
                return false;
            }
        });

        mButtonF2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                intent.putExtra("fButton", 2);
                intent.putExtra("buffer", buffer);
                intent.putExtra("mem", mem);
                intent.putExtra("oper", oper);
                intent.putExtra("perv", perv);
                intent.putExtra("dot", dot);
                intent.putExtra("percent", percent);
                intent.putExtra("memRC", memRC);
                startActivity(intent);
                return false;
            }
        });

        mButtonF3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                intent.putExtra("fButton", 3);
                intent.putExtra("buffer", buffer);
                intent.putExtra("mem", mem);
                intent.putExtra("oper", oper);
                intent.putExtra("perv", perv);
                intent.putExtra("dot", dot);
                intent.putExtra("percent", percent);
                intent.putExtra("memRC", memRC);
                startActivity(intent);
                return false;
            }
        });

        mButtonF4.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                intent.putExtra("fButton", 4);
                intent.putExtra("buffer", buffer);
                intent.putExtra("mem", mem);
                intent.putExtra("oper", oper);
                intent.putExtra("perv", perv);
                intent.putExtra("dot", dot);
                intent.putExtra("percent", percent);
                intent.putExtra("memRC", memRC);
                startActivity(intent);
                return false;
            }
        });

        mButtonF5.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                intent.putExtra("fButton", 5);
                intent.putExtra("buffer", buffer);
                intent.putExtra("mem", mem);
                intent.putExtra("oper", oper);
                intent.putExtra("perv", perv);
                intent.putExtra("dot", dot);
                intent.putExtra("percent", percent);
                intent.putExtra("memRC", memRC);
                startActivity(intent);
                return false;
            }
        });

        mButtonF6.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                intent.putExtra("fButton", 6);
                intent.putExtra("buffer", buffer);
                intent.putExtra("mem", mem);
                intent.putExtra("oper", oper);
                intent.putExtra("perv", perv);
                intent.putExtra("dot", dot);
                intent.putExtra("percent", percent);
                intent.putExtra("memRC", memRC);
                startActivity(intent);
                return false;
            }
        });

        mButtonF7.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                intent.putExtra("fButton", 7);
                intent.putExtra("buffer", buffer);
                intent.putExtra("mem", mem);
                intent.putExtra("oper", oper);
                intent.putExtra("perv", perv);
                intent.putExtra("dot", dot);
                intent.putExtra("percent", percent);
                intent.putExtra("memRC", memRC);
                startActivity(intent);
                return false;
            }
        });

        mButtonF8.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                intent.putExtra("fButton", 8);
                intent.putExtra("buffer", buffer);
                intent.putExtra("mem", mem);
                intent.putExtra("oper", oper);
                intent.putExtra("perv", perv);
                intent.putExtra("dot", dot);
                intent.putExtra("percent", percent);
                intent.putExtra("memRC", memRC);
                startActivity(intent);
                return false;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // функция рассчета арифметических операций
    public String ifOper(String str) {
        Log.d(TAG, "± ifOper before =" + str + " oper=" + oper);
        if (oper == 1) { //операция умножения
            double result = buffer * Double.parseDouble(str);
            return completeToScreen(result);
        } else if (oper == 2) { //операция сложения
            double result = buffer + Double.parseDouble(str);
            return completeToScreen(result);
        } else if (oper == 3) { //операция вычитания
            double result = buffer - Double.parseDouble(str);
            return completeToScreen(result);
        } else if (oper == 4) { //операция деления
            double result;
            if (Double.parseDouble(str) == 0) {
                result = 0;
            } else {
                result = buffer / Double.parseDouble(str);
            }
            Log.d(TAG, "± completeToScreen before result=" + result);
            return completeToScreen(result);

        } else if (oper == 5) { //операция возведения в степень
            double result;
            if (Double.parseDouble(str) == 0) {
                result = 0;
            } else {
                result = Math.pow(buffer, Double.parseDouble(str));
            }
            return completeToScreen(result);
        } else {
            return str;
        }
    }

    // функция рассчета операций с процентами
    public String ifPercent(String str) {
        if (oper == 1) {
            double result = buffer * (Double.parseDouble(str) / 100);
            return completeToScreen(result);
        } else if (oper == 2) {
            double result = buffer + (buffer / 100 * Double.parseDouble(str));
            return completeToScreen(result);
        } else if (oper == 3) {
            double result = buffer - (buffer / 100 * Double.parseDouble(str));
            return completeToScreen(result);
        } else if (oper == 4) {
            double result;
            if (Double.parseDouble(str) == 0) {
                result = 0;
            } else {
                result = buffer / (Double.parseDouble(str) / 100);
            }
            return completeToScreen(result);
        } else {
            return str;
        }
    }

    // Функция рассчета операций при нажатии Fn клавиш
    public String fnOper(String str, String fnc) {
        Log.d(TAG, "± fnOper before =" + str);
        double result = (Double.parseDouble(str));

        if (fnc.equals("cos")) result = Math.cos(result);
        if (fnc.equals("sin")) result = Math.sin(result);
        if (fnc.equals("tan")) result = Math.tan(result);
        if (fnc.equals("sinh")) result = Math.sinh(result);
        if (fnc.equals("cosh")) result = Math.cosh(result);
        if (fnc.equals("tanh")) result = Math.tanh(result);
        if (fnc.equals("acos")) result = Math.acos(result);
        if (fnc.equals("asin")) result = Math.asin(result);
        if (fnc.equals("atan")) result = Math.atan(result);
        if (fnc.equals("π")) result = 3.141592653589;
        if (fnc.equals("e")) result = 2.71828182845904;
        if (fnc.equals("x!")) {
            int factorial = 1;
            int i = 1;
            while (i <= result) {
                factorial = factorial * i;
                i++;
            }
            result = factorial;
        }
        if (fnc.equals("log2")) result = Math.log(result) / Math.log(2);
        if (fnc.equals("log10")) result = Math.log10(result);
        if (fnc.equals("ln")) result = Math.log(result);
        if (fnc.equals("1/x")) result = 1 / result;
        if (fnc.equals("10^x")) result = Math.pow(10, result);
        if (fnc.equals("x^2")) result = result * result;
        if (fnc.equals("x^3")) result = result * result * result;
        if (fnc.equals("exp(x)")) result = Math.exp(result);
        if (fnc.equals("expm1(x)")) result = Math.expm1(result);
        if (fnc.equals("x^y")) {
            oper = 5;
            perv = true;
            dot = false;
            percent = true;
            memRC = false;
        }
        if (fnc.equals("round")) result = Math.round(result);
        double bufTemp = buffer;
        String scr = completeToScreen(result);
        buffer = bufTemp;
        Log.d(TAG, "± fnOper after =" + scr);

        return scr;
    }

    public void saveFnKeys() {
        // Запоминаем данные
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(APP_PREFERENCES_FN1, fnArrayOfString[0]);
        editor.putString(APP_PREFERENCES_FN2, fnArrayOfString[1]);
        editor.putString(APP_PREFERENCES_FN3, fnArrayOfString[2]);
        editor.putString(APP_PREFERENCES_FN4, fnArrayOfString[3]);
        editor.putString(APP_PREFERENCES_FN5, fnArrayOfString[4]);
        editor.putString(APP_PREFERENCES_FN6, fnArrayOfString[5]);
        editor.putString(APP_PREFERENCES_FN7, fnArrayOfString[6]);
        editor.putString(APP_PREFERENCES_FN8, fnArrayOfString[7]);
        editor.apply();
    }

    public void onClickButton1(View view) {
        if (perv) {
            mScreenView.setText("");
            perv = false;
        }
        if (mScreenView.getText().toString().equals("0")) {
            mScreenView.setText("");
        }
        if (mScreenView.getText().toString().trim().length() < digitsCount) {
            mScreenView.setText(mScreenView.getText().toString() + "1");
        }
        if (percent) {
            mPercentButton.setEnabled(true);
            mPercentButton.setAlpha(1.0f);
        }
        memRC = false;
    }

    public void onClickButton2(View view) {
        if (perv) {
            mScreenView.setText("");
            perv = false;
        }
        if (mScreenView.getText().toString().equals("0")) {
            mScreenView.setText("");
        }
        if (mScreenView.getText().toString().trim().length() < digitsCount) {
            mScreenView.setText(mScreenView.getText().toString() + "2");
        }
        if (percent) {
            mPercentButton.setEnabled(true);
            mPercentButton.setAlpha(1.0f);
        }
        memRC = false;
    }

    public void onClickButton3(View view) {
        if (perv) {
            mScreenView.setText("");
            perv = false;
        }
        if (mScreenView.getText().toString().equals("0")) {
            mScreenView.setText("");
        }
        if (mScreenView.getText().toString().trim().length() < digitsCount) {
            mScreenView.setText(mScreenView.getText().toString() + "3");
        }
        if (percent) {
            mPercentButton.setEnabled(true);
            mPercentButton.setAlpha(1.0f);
        }
        memRC = false;
    }

    public void onClickButton4(View view) {
        if (perv) {
            mScreenView.setText("");
            perv = false;
        }
        if (mScreenView.getText().toString().equals("0")) {
            mScreenView.setText("");
        }
        if (mScreenView.getText().toString().trim().length() < digitsCount) {
            mScreenView.setText(mScreenView.getText().toString() + "4");
        }
        if (percent) {
            mPercentButton.setEnabled(true);
            mPercentButton.setAlpha(1.0f);
        }
        memRC = false;
    }

    public void onClickButton5(View view) {
        if (perv) {
            mScreenView.setText("");
            perv = false;
        }
        if (mScreenView.getText().toString().equals("0")) {
            mScreenView.setText("");
        }
        if (mScreenView.getText().toString().trim().length() < digitsCount) {
            mScreenView.setText(mScreenView.getText().toString() + "5");
        }
        if (percent) {
            mPercentButton.setEnabled(true);
            mPercentButton.setAlpha(1.0f);
        }
        memRC = false;
    }

    public void onClickButton6(View view) {
        if (perv) {
            mScreenView.setText("");
            perv = false;
        }
        if (mScreenView.getText().toString().equals("0")) {
            mScreenView.setText("");
        }
        if (mScreenView.getText().toString().trim().length() < digitsCount) {
            mScreenView.setText(mScreenView.getText().toString() + "6");
        }
        if (percent) {
            mPercentButton.setEnabled(true);
            mPercentButton.setAlpha(1.0f);
        }
        memRC = false;
    }

    public void onClickButton7(View view) {
        if (perv) {
            mScreenView.setText("");
            perv = false;
        }
        if (mScreenView.getText().toString().equals("0")) {
            mScreenView.setText("");
        }
        if (mScreenView.getText().toString().trim().length() < digitsCount) {
            mScreenView.setText(mScreenView.getText().toString() + "7");
        }
        if (percent) {
            mPercentButton.setEnabled(true);
            mPercentButton.setAlpha(1.0f);
        }
        memRC = false;
    }

    public void onClickButton8(View view) {
        if (perv) {
            mScreenView.setText("");
            perv = false;
        }
        if (mScreenView.getText().toString().equals("0")) {
            mScreenView.setText("");
        }
        if (mScreenView.getText().toString().trim().length() < digitsCount) {
            mScreenView.setText(mScreenView.getText().toString() + "8");
        }
        if (percent) {
            mPercentButton.setEnabled(true);
            mPercentButton.setAlpha(1.0f);
        }
        memRC = false;
    }

    public void onClickButton9(View view) {
        if (perv) {
            mScreenView.setText("");
            perv = false;
        }
        if (mScreenView.getText().toString().equals("0")) {
            mScreenView.setText("");
        }
        if (mScreenView.getText().toString().trim().length() < digitsCount) {
            mScreenView.setText(mScreenView.getText().toString() + "9");
        }
        if (percent) {
            mPercentButton.setEnabled(true);
            mPercentButton.setAlpha(1.0f);
        }
        memRC = false;
    }

    public void onClickButton0(View view) {
        if (perv) {
            mScreenView.setText("");
            perv = false;
        }
        if (mScreenView.getText().toString().equals("0")) {
            mScreenView.setText("");
        }
        if (mScreenView.getText().toString().trim().length() < digitsCount) {
            mScreenView.setText(mScreenView.getText().toString() + "0");
        }
        if (percent) {
            mPercentButton.setEnabled(true);
            mPercentButton.setAlpha(1.0f);
        }
        memRC = false;
    }

    public void onClickButton00(View view) {
        if (perv) {
            mScreenView.setText("");
            perv = false;
        }
        if (mScreenView.getText().toString().equals("0")) {
            mScreenView.setText("0");
        } else {
            if (mScreenView.getText().toString().trim().length() < digitsCount) {
                mScreenView.setText(mScreenView.getText().toString() + "00");
            }
        }
        if (percent) {
            mPercentButton.setEnabled(true);
            mPercentButton.setAlpha(1.0f);
        }
        memRC = false;
    }

    public void onClickButtonPoint(View view) {
        if (perv) {
            mScreenView.setText("0");
            perv = false;
        }
        if (!dot) {
            dot = true;
            if (mScreenView.getText().toString().trim().length() < digitsCount) {
                mScreenView.setText(mScreenView.getText().toString() + ".");
            }
        }
        if (percent) {
            mPercentButton.setEnabled(true);
            mPercentButton.setAlpha(1.0f);
        }
        memRC = false;
    }


    public void onClickButtonAC(View view) {
        mSignDisplayView.setText("");
        mScreenView.setText("0");
        fnCalc.accumulator = 0;
        dot = false;
        oper = 0;
        perv = false;
        buffer = 0;
        percent = false;
        mPercentButton.setEnabled(false);
        mPercentButton.setAlpha(0.5f);
        memRC = false;
        Log.d(TAG, "± AC buffer=" + buffer + " mem=" + mem + " oper=" + oper + " perv=" + perv + " dot=" + dot + " percent=" + percent + " memRC=" + memRC);
    }

    public void onClickButtonBackspace(View view) {
        String str = mScreenView.getText().toString().substring(0, mScreenView.getText().toString().trim().length() - 1);
        mScreenView.setText(str);
        if (str.trim().length() == 0 || str.equals("")) mScreenView.setText("0");
        if (str.equals("-")) mScreenView.setText("0");
        memRC = false;
        mSignDisplayView.setText("");
    }

    public void onClickButtonPlusMinus(View view) {
        double strNumber = Double.parseDouble(mScreenView.getText().toString());
        if (strNumber < 0) {
            String str = mScreenView.getText().toString().substring(1, mScreenView.getText().toString().trim().length());
            mScreenView.setText(str);
        } else if (strNumber != 0) {
            String str = "-" + mScreenView.getText().toString().substring(0, mScreenView.getText().toString().trim().length());
            mScreenView.setText(str);
        }
        memRC = false;
    }

    public void onClickButtonMinus(View view) {
        Log.d(TAG, "± onClickButtonMinus before =" + mScreenView.getText().toString());
        fnCalc.checkCalcOper(oper, Double.parseDouble(mScreenView.getText().toString()));
        mSignDisplayView.setText("-");
        mScreenView.setText(ifOper(mScreenView.getText().toString()));
        buffer = Double.parseDouble(mScreenView.getText().toString());
        oper = 3;
        perv = true;
        dot = false;
        percent = true;
        memRC = false;
        if (mScreenView.getText().toString().equals("")) {
            mScreenView.setText("0");
        }
    }

    public void onClickButtonMult(View view) {
        Log.d(TAG, "± onClickButtonMult before =" + mScreenView.getText().toString());
        fnCalc.checkCalcOper(oper, Double.parseDouble(mScreenView.getText().toString()));
        mSignDisplayView.setText("x");
        mScreenView.setText(ifOper(mScreenView.getText().toString()));
        buffer = Double.parseDouble(mScreenView.getText().toString());
        oper = 1;
        perv = true;
        dot = false;
        percent = true;
        memRC = false;
        if (mScreenView.getText().toString().equals("")) {
            mScreenView.setText("0");
        }
    }

    public void onClickButtonDivide(View view) {
        Log.d(TAG, "± onClickButtonDivide before =" + mScreenView.getText().toString());
        fnCalc.checkCalcOper(oper, Double.parseDouble(mScreenView.getText().toString()));
        mSignDisplayView.setText("÷");
        mScreenView.setText(ifOper(mScreenView.getText().toString()));
        buffer = Double.parseDouble(mScreenView.getText().toString());
        oper = 4;
        perv = true;
        dot = false;
        percent = true;
        memRC = false;
        if (mScreenView.getText().toString().equals("")) {
            mScreenView.setText("0");
        }
    }

    public void onClickButtonPlus(View view) {
        Log.d(TAG, "± onClickButtonPlus before =" + mScreenView.getText().toString());
        fnCalc.checkCalcOper(oper, Double.parseDouble(mScreenView.getText().toString()));
        mSignDisplayView.setText("+");
        mScreenView.setText(ifOper(mScreenView.getText().toString()));
        buffer = Double.parseDouble(mScreenView.getText().toString());
        oper = 2;
        perv = true;
        dot = false;
        percent = true;
        memRC = false;
        if (mScreenView.getText().toString().equals("")) {
            mScreenView.setText("0");
        }
    }

    public void onClickButtonSQR(View view) {
        Log.d(TAG, "± onClickButtonSQR before =" + mScreenView.getText().toString());
        mScreenView.setText(ifOper(mScreenView.getText().toString()));
        double result;
        result = Double.parseDouble(mScreenView.getText().toString());
        if (result < 0) result = 0;
        else result = Math.sqrt(result);
        mScreenView.setText(completeToScreen(result));
        perv = true;
        dot = false;
        percent = true;
        memRC = false;
        if (mScreenView.getText().toString().equals("")) {
            mScreenView.setText("0");
        }
    }

    public void onClickButtonMPlus(View view) {
        perv = true;
        mScreenView.setText(ifOper(mScreenView.getText().toString()));
        dot = false;
        percent = false;
        mPercentButton.setEnabled(false);
        mPercentButton.setAlpha(0.5f);
        mem = mem + Double.parseDouble(mScreenView.getText().toString());
        mMemRCButton.setEnabled(true);
        mMemRCButton.setAlpha(1.0f);
        memRC = false;
        Log.d(TAG, "± M+ buffer=" + buffer + " mem=" + mem + " oper=" + oper + " perv=" + perv + " dot=" + dot + " percent=" + percent + " memRC=" + memRC);
    }

    public void onClickButtonMMinus(View view) {
        perv = true;
        mScreenView.setText(ifOper(mScreenView.getText().toString()));
        dot = false;
        percent = false;
        mPercentButton.setEnabled(false);
        mPercentButton.setAlpha(0.5f);
        mem = mem - Double.parseDouble(mScreenView.getText().toString());
        mMemRCButton.setEnabled(true);
        mMemRCButton.setAlpha(1.0f);
        memRC = false;
        Log.d(TAG, "± M- buffer=" + buffer + " mem=" + mem + " oper=" + oper + " perv=" + perv + " dot=" + dot + " percent=" + percent + " memRC=" + memRC);
    }

    public void onClickButtonPercent(View view) {
        mSignDisplayView.setText("");
        perv = true;
        mScreenView.setText(ifPercent(mScreenView.getText().toString()));
        dot = false;
        percent = false;
        mPercentButton.setEnabled(false);
        mPercentButton.setAlpha(0.5f);
        memRC = false;
        oper = 0;
    }

    public void onClickButtonMRC(View view) {
        if (!memRC) {
            memRC = true;
            String strTemp = String.valueOf(mem);
            int strLen = strTemp.length();
            String pointZero = strTemp.substring(strLen - 2);
            if (pointZero.equals(".0")) {
                strTemp = strTemp.substring(0, strLen - 2);
            }
            Log.d(TAG, "± M+ buffer=" + buffer + " mem=" + mem + " strTemp=" + strTemp);
            mScreenView.setText(strTemp);
        } else {
            mem = 0;
            memRC = false;
            mMemRCButton.setEnabled(false);
            mMemRCButton.setAlpha(0.5f);
        }
        perv = true;
        Log.d(TAG, "± MRC buffer=" + buffer + " mem=" + mem + " oper=" + oper + " perv=" + perv + " dot=" + dot + " percent=" + percent + " memRC=" + memRC);
    }

    public void onClickButtonEqual(View view) {
        fnCalc.checkCalcOper(oper, Double.parseDouble(mScreenView.getText().toString()));
        mSignDisplayView.setText("");
        perv = true;
        mScreenView.setText(ifOper(mScreenView.getText().toString()));
        dot = false;
        percent = false;
        mPercentButton.setEnabled(false);
        mPercentButton.setAlpha(0.5f);
        oper = 0;
        memRC = false;
        if (mScreenView.getText().toString().equals("")) {
            mScreenView.setText("0");
        }
    }

    public void onClickButtonF1(View view) {
        mScreenView.setText(fnOper(mScreenView.getText().toString(), mButtonF1.getText().toString()));
        perv = true;
        dot = false;
        memRC = false;
        if (mScreenView.getText().toString().equals("")) {
            mScreenView.setText("0");
        }
    }

    public void onClickButtonF2(View view) {
        mScreenView.setText(fnOper(mScreenView.getText().toString(), mButtonF2.getText().toString()));
        perv = true;
        dot = false;
        memRC = false;
        if (mScreenView.getText().toString().equals("")) {
            mScreenView.setText("0");
        }
    }

    public void onClickButtonF3(View view) {
        mScreenView.setText(fnOper(mScreenView.getText().toString(), mButtonF3.getText().toString()));
        perv = true;
        dot = false;
        memRC = false;
        if (mScreenView.getText().toString().equals("")) {
            mScreenView.setText("0");
        }
    }

    public void onClickButtonF4(View view) {
        mScreenView.setText(fnOper(mScreenView.getText().toString(), mButtonF4.getText().toString()));
        perv = true;
        dot = false;
        memRC = false;
        if (mScreenView.getText().toString().equals("")) {
            mScreenView.setText("0");
        }
    }

    public void onClickButtonF5(View view) {
        mScreenView.setText(fnOper(mScreenView.getText().toString(), mButtonF5.getText().toString()));
        perv = true;
        dot = false;
        memRC = false;
        if (mScreenView.getText().toString().equals("")) {
            mScreenView.setText("0");
        }
    }

    public void onClickButtonF6(View view) {
        mScreenView.setText(fnOper(mScreenView.getText().toString(), mButtonF6.getText().toString()));
        perv = true;
        dot = false;
        memRC = false;
        if (mScreenView.getText().toString().equals("")) {
            mScreenView.setText("0");
        }
    }

    public void onClickButtonF7(View view) {
        mScreenView.setText(fnOper(mScreenView.getText().toString(), mButtonF7.getText().toString()));
        perv = true;
        dot = false;
        memRC = false;
        if (mScreenView.getText().toString().equals("")) {
            mScreenView.setText("0");
        }
    }

    public void onClickButtonF8(View view) {
        mScreenView.setText(fnOper(mScreenView.getText().toString(), mButtonF8.getText().toString()));
        perv = true;
        dot = false;
        memRC = false;
        if (mScreenView.getText().toString().equals("")) {
            mScreenView.setText("0");
        }
    }


}

