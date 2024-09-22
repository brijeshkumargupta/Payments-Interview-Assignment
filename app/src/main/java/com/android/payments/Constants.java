package com.android.payments;

import com.android.payments.models.PaymentModel;

import java.util.ArrayList;

public class Constants {
    public static ArrayList<PaymentModel> payments = new ArrayList<>();

    public static String[] paymentTypes = {"Cash Payment", "Bank Transfer", "Credit Card"};

}
