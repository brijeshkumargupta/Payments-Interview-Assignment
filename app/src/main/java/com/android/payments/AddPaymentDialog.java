package com.android.payments;

import static com.android.payments.Constants.paymentTypes;
import static com.android.payments.Constants.payments;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.payments.databinding.AddPaymentDialogBinding;
import com.android.payments.models.PaymentModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddPaymentDialog {
    AddPaymentDialogBinding binding;

    public void showDialog(Activity activity) {
        binding = AddPaymentDialogBinding.inflate(activity.getLayoutInflater());

        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        LinearLayout llBank = binding.llBankTransfer;

        binding.spnType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (parent.getSelectedItem().toString().equals("Cash Payment")) {
                    llBank.setVisibility(View.INVISIBLE);
                }
                if (parent.getSelectedItem().toString().equals("Bank Transfer")) {
                    llBank.setVisibility(View.VISIBLE);
                }
                if (parent.getSelectedItem().toString().equals("Credit Card")) {
                    llBank.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> spnAdapter = new ArrayAdapter<>(activity.getApplicationContext(), R.layout.spinner_item, paymentTypes);

        binding.spnType.setAdapter(spnAdapter);
        binding.btnSave.setOnClickListener(v -> {
            int amount = 0;
            if (!binding.etAmount.getText().toString().trim().equals("")) {
                amount = Integer.parseInt(binding.etAmount.getText().toString());
            }

            if (binding.spnType.getSelectedItem().toString().equals("Cash Payment")) {
                boolean cashContains = false;
                for (PaymentModel item : payments) {
                    if (item.getPayment_type().equals("cash")) {
                        cashContains = true;
                        break;
                    }
                }

                if (binding.etAmount.getText().toString().trim().length() < 1) {
                    Toast.makeText(activity, "Please enter Amount", Toast.LENGTH_SHORT).show();
                } else if (cashContains) {
                    Toast.makeText(activity, "Cash Payment already exists.", Toast.LENGTH_SHORT).show();
                } else {
                    payments.add(new PaymentModel("cash", "Cash Payment", "", "", amount));
                    List<String> paymentTypesList = new ArrayList<>(Arrays.asList(paymentTypes));
                    paymentTypesList.remove("Cash Payment");
                    paymentTypes = paymentTypesList.toArray(new String[0]);
                    dialog.dismiss();
                }
            } else if (binding.spnType.getSelectedItem().toString().equals("Bank Transfer")) {
                boolean bankContains = false;
                for (PaymentModel item : payments) {
                    if (item.getPayment_type().equals("bank")) {
                        bankContains = true;
                        break;
                    }
                }
                if (binding.etAmount.getText().toString().trim().length() < 1) {
                    Toast.makeText(activity, "Please enter Amount", Toast.LENGTH_SHORT).show();
                } else if (binding.etProvider.getText().toString().trim().length() < 1) {
                    Toast.makeText(activity, "Please enter Provider", Toast.LENGTH_SHORT).show();
                } else if (binding.etReference.getText().toString().trim().length() < 1) {
                    Toast.makeText(activity, "Please enter Transaction Reference", Toast.LENGTH_SHORT).show();
                } else if (bankContains) {
                    Toast.makeText(activity, "Bank Transfer already exists.", Toast.LENGTH_SHORT).show();
                } else {
                    payments.add(new PaymentModel("bank", "Bank Transfer", binding.etProvider.getText().toString(), binding.etReference.getText().toString(), amount));
                    List<String> paymentTypesList = new ArrayList<>(Arrays.asList(paymentTypes));
                    paymentTypesList.remove("Bank Transfer");
                    paymentTypes = paymentTypesList.toArray(new String[0]);
                    dialog.dismiss();
                }
            } else if (binding.spnType.getSelectedItem().toString().equals("Credit Card")) {
                boolean cardContains = false;
                for (PaymentModel item : payments) {
                    if (item.getPayment_type().equals("card")) {
                        cardContains = true;
                        break;
                    }
                }
                if (binding.etAmount.getText().toString().trim().length() < 1) {
                    Toast.makeText(activity, "Please enter Amount", Toast.LENGTH_SHORT).show();
                } else if (binding.etProvider.getText().toString().trim().length() < 1) {
                    Toast.makeText(activity, "Please enter Provider", Toast.LENGTH_SHORT).show();
                } else if (binding.etReference.getText().toString().trim().length() < 1) {
                    Toast.makeText(activity, "Please enter Transaction Reference", Toast.LENGTH_SHORT).show();
                } else if (cardContains) {
                    Toast.makeText(activity, "Credit Card Payment already exists.", Toast.LENGTH_SHORT).show();
                } else {
                    payments.add(new PaymentModel("card", "Credit Card", binding.etProvider.getText().toString().trim(), binding.etReference.getText().toString().trim(), amount));
                    List<String> paymentTypesList = new ArrayList<>(Arrays.asList(paymentTypes));
                    paymentTypesList.remove("Credit Card");
                    paymentTypes = paymentTypesList.toArray(new String[0]);
                    dialog.dismiss();
                }
            }
        });
        binding.tvCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, 40);
        dialog.getWindow().setBackgroundDrawable(inset);


    }

}
