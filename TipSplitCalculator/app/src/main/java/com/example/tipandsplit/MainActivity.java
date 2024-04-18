package com.example.tipandsplit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    double totalAmt, billAmount, tempNumber, bill;
    EditText amount, numberOfPerson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setId();
        radioGroupFun();
        goButtonFun();
        clearButtonFun();
    }
    Button goButton,clearButton;
    TextView tip_Amount,total,totalPerPersonAmount;
    RadioGroup radioGroup;
    RadioButton r12, r15, r18, r20, temp;

    private void clearButtonFun() {
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearText(amount);
                clearText(tip_Amount);
                clearText(total);
                clearText(totalPerPersonAmount);
                clearText(numberOfPerson);
                totalAmt = 0.0;
                billAmount = 0.0;
                bill = 0.0;
                setRadioButton(r12);
                setRadioButton(r15);
                setRadioButton(r18);
                setRadioButton(r20);
            }
        });
    }

    private void goButtonFun() {
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amountString = numberOfPerson.getText().toString();
                boolean checkNumber = TextUtils.isEmpty(amountString);
                if(checkNumber) {
                    numberOfPerson.setError("Please Enter Valid Input");
                }else {
                    InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(),0);
                    int num = Integer.parseInt(amountString);
                    if(num<1){
                        numberOfPerson.setError("Please Enter minimum 1");
                        return;
                    }

                    totalPerPersonAmount.setText("$" + String.format("%.2f" , (double)Math.ceil(totalAmt*100/num)/100));
                }
            }
        });
    }

    public void clearText(TextView textView) {
        textView.setText("");
    }

    public void radioGroupFun() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                temp = group.findViewById(checkedId);
                setTextValues();
                if(temp.isChecked()) {
                    String amountString = amount.getText().toString();
                    boolean checkNumber = TextUtils.isEmpty(amountString);
                    if(checkNumber) {
                        setRadioButton(r12);
                        setRadioButton(r15);
                        setRadioButton(r18);
                        setRadioButton(r20);
                        amount.setError("Please Enter Value");
                    }
                    else {
                        try {
                            billAmount = Double.parseDouble(amountString);
                        }catch (NumberFormatException e){
                            return;
                        }
                        tempNumber = billAmount;
                        calculation(checkedId);
                    }
                }
            }
        });
    }

    public void setTextValues() {
        totalAmt = 0.0;
        tip_Amount.setText("");
        total.setText("");
        totalPerPersonAmount.setText("");
    }

    public void setRadioButton(RadioButton button) {
        button.setChecked(false);
    }

    public void calculation(int checkedId) {
        switch (checkedId) {
            case R.id.r12:
                billAmount = billAmount * 0.12;
                break;
            case R.id.r15:
                billAmount = billAmount* 0.15;
                break;
            case R.id.r18:
                billAmount = billAmount * 0.18;
                break;
            case R.id.r20:
                billAmount = billAmount * 0.2;
                break;
        }
        billAmount = Double.parseDouble(String.format("%.2f",billAmount));
        totalAmt = tempNumber + billAmount;
        tip_Amount.setText("$"+ String.format("%.2f",billAmount));
        total.setText("$" + String.format("%.2f",totalAmt));
    }


    public void setId() {
        tip_Amount = findViewById(R.id.tip_Amount);
        goButton  = findViewById(R.id.goButton);
        clearButton = findViewById(R.id.clearButton);
        total = findViewById(R.id.total);
        radioGroup = findViewById(R.id.radioGroup);
        numberOfPerson = findViewById(R.id.numberOfPeople);
        r12 = findViewById(R.id.r12);
        r15 = findViewById(R.id.r15);
        totalPerPersonAmount = findViewById(R.id.totalPerPersonAmount);
        r18 = findViewById(R.id.r18);
        r20 = findViewById(R.id.r20);
        amount = findViewById(R.id.amount);
    }
}