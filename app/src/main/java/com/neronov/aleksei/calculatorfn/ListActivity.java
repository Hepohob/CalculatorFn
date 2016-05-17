package com.neronov.aleksei.calculatorfn;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by newuser on 21.06.15.
 */

public class ListActivity extends android.app.ListActivity {
    int fButton;
    double buffer;
    double mem;
    int oper;
    boolean perv;
    boolean dot;
    boolean percent;
    boolean memRC;

    public final String[] mItemsArray = new String[] { "cos", "sin", "tan",
            "sinh", "cosh", "tanh", "acos", "asin", "atan",
            "π", "e", "x!", "log2", "log10", "ln",
            "1/x", "10^x", "x^2", "x^3", "exp(x)", "expm1(x)",
            "x^y", "round"
    };

    private ArrayAdapter<String> mItemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent1 = getIntent();
        fButton = intent1.getIntExtra("fButton",0);
        buffer = intent1.getDoubleExtra("buffer", 0);
        mem = intent1.getDoubleExtra("mem", 0);
        oper = intent1.getIntExtra("oper", 0);
        perv = intent1.getBooleanExtra("perv", false);
        dot = intent1.getBooleanExtra("dot", false);
        percent = intent1.getBooleanExtra("percent", false);
        memRC = intent1.getBooleanExtra("memRC", false);
//        Toast.makeText(getApplicationContext(),"Button F"+fButton, Toast.LENGTH_SHORT).show();
        Log.d("myTag", "(list)buffer=" + buffer + " mem=" + mem + " oper=" + oper + " perv=" + perv + " dot=" + dot + " percent=" + percent + " memRC=" + memRC);

        mItemsAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,mItemsArray);
        setListAdapter(mItemsAdapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Intent intent = new Intent(ListActivity.this,MainActivity.class);
        intent.putExtra("item",l.getItemAtPosition(position).toString());
        intent.putExtra("fButton", fButton);
        intent.putExtra("buffer", buffer);
        intent.putExtra("mem", mem);
        intent.putExtra("oper", oper);
        intent.putExtra("perv", perv);
        intent.putExtra("dot", dot);
        intent.putExtra("percent", percent);
        intent.putExtra("memRC", memRC);
        startActivity(intent);
    }
}
