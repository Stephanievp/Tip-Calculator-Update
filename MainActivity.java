package com.example.tipcalculatorupdate;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ShareCompat;

import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity
        implements TextWatcher,SeekBar.OnSeekBarChangeListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = "MainActivity";
    //declare your variables for the widgets
    private EditText editTextBillAmount;
    private TextView textViewBillAmount;
    private TextView textViewPercent;
    private TextView tipAmount;
    private TextView totalAmount;
    private TextView perPerson;
    private SeekBar seekBar;

    //declare the variables for the calculations
    private double billAmount = 0.0;
    private double percent    = .15;
    private double tip;
    private double total;
    private int roundOption;
    private int nPeople;

    //set the number formats to be used for the $ amounts , and % amounts
    private static final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
    private static final NumberFormat percentFormat = NumberFormat.getPercentInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("MainActivity", "inside onCreate method");

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        //add Listeners to Widgets
        editTextBillAmount = (EditText)findViewById(R.id.editText_BillAmount);
        editTextBillAmount.addTextChangedListener((TextWatcher) this);

        textViewBillAmount = (TextView)findViewById(R.id.textView_BillAmount);
        textViewPercent    = (TextView)findViewById(R.id.textView_TipPercentage);
        tipAmount          = (TextView)findViewById(R.id.tip_Amount);
        totalAmount        = (TextView)findViewById(R.id.total_Amount);
        perPerson          = (TextView)findViewById(R.id.amount_per_person);
        seekBar            = (SeekBar)findViewById(R.id.seekbar);
        seekBar.setOnSeekBarChangeListener(this);

        // Create ArrayAdapter using the string array
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.labels_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Create the spinner and apply adapter to spinner
        Spinner spinner = findViewById(R.id.people_spinner);
        if (spinner != null) {
            spinner.setOnItemSelectedListener(this);
            spinner.setAdapter(adapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "inside onCreateOptionsMenu of MainActivity");

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "inside onOptionsItemSelected of MainActivity");

        switch (item.getItemId()) {
            case R.id.action_share:
                String bill   = textViewBillAmount.getText().toString();
                String tip    = tipAmount.getText().toString();
                String total  = totalAmount.getText().toString();
                String person = perPerson.getText().toString();
                String msg = ("Bill: " + bill + "\nTip: " + tip + "\nTotal: " + total +
                        "\nSplit bill in " + nPeople + "\nAmount per person: " + person);

                String mimeType = "text/plain";
                ShareCompat.IntentBuilder
                        .from(this)
                        .setType(mimeType)
                        .setChooserTitle("Complete Action using:")
                        .setText(msg)
                        .startChooser();

                return true;
            case R.id.action_info:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

                // Set the dialog title and message.
                alertDialog.setTitle("Spinner Info");
                alertDialog.setMessage("The dropdown is used to split the total among friends." +
                        "\nThe amount per person displays what each person must pay.");
                // Create and show the AlertDialog.
                alertDialog.show();

                return true;
            default:
                // Do nothing
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        calculate();
    }
    /*
    Note:   int i, int i1, and int i2
            represent start, before, count respectively
            The charSequence is converted to a String and parsed to a double for you
     */

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        Log.d("MainActivity", "inside onTextChanged method: charSequence= "+charSequence);
        //surround risky calculations with try catch (what if billAmount is 0 ?
        //charSequence is converted to a String and parsed to a double for you
        billAmount = Double.parseDouble(charSequence.toString()) / 100.0;

        Log.d("MainActivity", "Bill Amount = "+billAmount);
        //setText on the textView
        textViewBillAmount.setText(currencyFormat.format(billAmount));
        //perform tip and total calculation and update UI by calling calculate
         calculate();//uncomment this line
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        percent = progress / 100.0; //calculate percent based on seeker value
        textViewPercent.setText(percentFormat.format(progress * .01));
        calculate();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        percent = seekBar.getProgress() /100.0;
        calculate();

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        percent = seekBar.getProgress() /100.0;
        calculate();

    }

    // calculate and display tip and total amounts
    private void calculate() {
        Log.d("MainActivity", "inside calculate method");

        try {
            if (billAmount == 0.0);
            throw new Exception("");
        }
        catch (Exception e){
            Log.d("MainActivity", "bill amount cannot be zero");
        }
            // format percent and display in percentTextView
            textViewPercent.setText(percentFormat.format(percent));

            if (roundOption == 2) {
                tip = Math.ceil(billAmount * percent);
                total = billAmount + tip;
            }
            else if (roundOption == 3){
                tip = billAmount * percent;
                total = Math.ceil(billAmount + tip);
            }
            else {
                // calculate the tip and total
                tip = billAmount * percent;

                //use the tip example to do the same for the Total
                total = billAmount + tip;
            }

            // display tip and total formatted as currency
            //user currencyFormat instead of percentFormat to set the textViewTip
            tipAmount.setText(currencyFormat.format(tip));

            //use the tip example to do the same for the Total
            totalAmount.setText(currencyFormat.format(total));

            double person = total/nPeople;
            perPerson.setText(currencyFormat.format(person));
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        Log.d(TAG, "inside onItemSelected method");

        switch (position){
            case 1:
                nPeople = 2;
                calculate();
                break;
            case 2:
                nPeople = 3;
                calculate();
                break;
            case 3:
                nPeople = 4;
                calculate();
                break;
            case 4:
                nPeople = 5;
                calculate();
                break;
            case 5:
                nPeople = 6;
                calculate();
                break;
            default:
                nPeople = 1;
                calculate();
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void onRadioButtonClicked(View view) {
        Log.d(TAG, "inside onRadioButtonClicked of OrderActivity");

        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()){
            case R.id.no_round:
                if (checked)
                    roundOption = 1;
                    calculate();
                break;
            case R.id.tip_round:
                if (checked)
                    roundOption = 2;
                    calculate();
                break;
            case R.id.total_round:
                if (checked)
                    roundOption = 3;
                    calculate();
                break;
            default:
                //Do nothing
                break;
        }
    }
}
