package com.android.payments;

import static com.android.payments.Constants.paymentTypes;
import static com.android.payments.Constants.payments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.payments.databinding.ActivityMainBinding;
import com.android.payments.models.PaymentModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    String filename = "";
    String filepath = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        filename = "LastPayment.txt";
        filepath = getString(R.string.app_name);

        payments.clear();
        readPayment();
        updateUI();

        if (!isExternalStorageAvailableForRW()) {
            binding.btnSave.setEnabled(false);
        }
        binding.btnSave.setOnClickListener(v -> {

            JSONArray jsonArray = new JSONArray();

            for (PaymentModel payment : payments) {

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("payment_type", payment.getPayment_type());
                    jsonObject.put("payment_method", payment.getPayment_method());
                    jsonObject.put("provider", payment.getProvider());
                    jsonObject.put("reference_id", payment.getReference_id());
                    jsonObject.put("amount", payment.getAmount());
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                jsonArray.put(jsonObject);
            }


            String jsonString = jsonArray.toString();

            if (isStoragePermissionGranted()) {
                File file = new File(getExternalFilesDir(filepath), filename);
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(jsonString.getBytes());
                    fos.close();
                    Toast.makeText(this, "Information saved to LastPayment.txt file", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });

    }

    private void readPayment() {
        File file = new File(getExternalFilesDir(filepath), filename);
        StringBuilder stringBuilder = new StringBuilder();
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            while (line != null) {
                stringBuilder.append(line).append('\n');
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            String result = stringBuilder.toString();

            try {
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    payments.add(new PaymentModel(object.getString("payment_type"), object.getString("payment_method"), object.getString("provider"), object.getString("reference_id"), object.getInt("amount")));
                    updateUI();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }


    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else {
            return true;
        }
    }

    private boolean isExternalStorageAvailableForRW() {
        String extStorageState = Environment.getExternalStorageState();
        if (extStorageState.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    private void updateUI() {
        final long[] oldTotal = {0};
        binding.tvAddPayment.setOnClickListener(v -> {
            if (paymentTypes.length > 0) {
                AddPaymentDialog dialog = new AddPaymentDialog();
                dialog.showDialog(this);
            } else {
                Toast.makeText(this, "All type payments are exists.", Toast.LENGTH_SHORT).show();
            }

        });
        binding.llPaymentList.removeAllViews();
        for (int i = 0; i < payments.size(); i++) {
            PaymentModel payment = payments.get(i);
            long amount = payment.getAmount();
            oldTotal[0] = oldTotal[0] + amount;
        }
        binding.tvAmount.setText(oldTotal[0] + "");

        for (int i = 0; i < payments.size(); i++) {
            PaymentModel payment = payments.get(i);

            View view = getLayoutInflater().inflate(R.layout.payment_card, null);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(10, 0, 10, 20);
            LinearLayout llParent = view.findViewById(R.id.llParent);
            llParent.setLayoutParams(params);

            TextView tvPayment = view.findViewById(R.id.tvPayment);
            ImageView ivClear = view.findViewById(R.id.ivClear);

            if (payment.getPayment_type().equals("cash")) {
                List<String> paymentTypesList = new ArrayList<>(Arrays.asList(paymentTypes));
                paymentTypesList.remove("Cash Payment");
                paymentTypes = paymentTypesList.toArray(new String[0]);

                tvPayment.setText(getString(R.string.cash) + payments.get(i).getAmount());
                ivClear.setOnClickListener(v -> {
                    oldTotal[0] = (oldTotal[0] - payment.getAmount());
                    binding.tvAmount.setText(oldTotal[0] + "");
                    payments.remove(payment);
                    List<String> paymentTypesListAdd = new ArrayList<>(Arrays.asList(paymentTypes));
                    paymentTypesListAdd.add("Cash Payment");
                    paymentTypes = paymentTypesListAdd.toArray(new String[0]);
                    binding.llPaymentList.removeView(view);
                });
            } else if (payment.getPayment_type().equals("bank")) {
                List<String> paymentTypesList = new ArrayList<>(Arrays.asList(paymentTypes));
                paymentTypesList.remove("Bank Transfer");
                paymentTypes = paymentTypesList.toArray(new String[0]);

                tvPayment.setText(getString(R.string.bank) + payments.get(i).getAmount());
                ivClear.setOnClickListener(v -> {
                    oldTotal[0] = (oldTotal[0] - payment.getAmount());
                    binding.tvAmount.setText(oldTotal[0] + "");
                    payments.remove(payment);
                    List<String> paymentTypesListAdd = new ArrayList<>(Arrays.asList(paymentTypes));
                    paymentTypesListAdd.add("Bank Transfer");
                    paymentTypes = paymentTypesListAdd.toArray(new String[0]);
                    binding.llPaymentList.removeView(view);
                });
            } else if (payment.getPayment_type().equals("card")) {
                List<String> paymentTypesList = new ArrayList<>(Arrays.asList(paymentTypes));
                paymentTypesList.remove("Credit Card");
                paymentTypes = paymentTypesList.toArray(new String[0]);

                tvPayment.setText(getString(R.string.card) + payments.get(i).getAmount());
                ivClear.setOnClickListener(v -> {
                    oldTotal[0] = (oldTotal[0] - payment.getAmount());
                    binding.tvAmount.setText(oldTotal[0] + "");
                    payments.remove(payment);
                    List<String> paymentTypesListAdd = new ArrayList<>(Arrays.asList(paymentTypes));
                    paymentTypesListAdd.add("Credit Card");
                    paymentTypes = paymentTypesListAdd.toArray(new String[0]);
                    binding.llPaymentList.removeView(view);
                });
            }

            binding.llPaymentList.addView(view);

        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        updateUI();
    }

}