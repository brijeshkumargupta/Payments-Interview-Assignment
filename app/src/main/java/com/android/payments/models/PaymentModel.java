package com.android.payments.models;

public class PaymentModel {
    private String payment_type;
    private String payment_method;
    private String provider;
    private String reference_id;
    private int amount;

    public PaymentModel(String payment_type, String payment_method, String provider, String reference_id, int amount) {
        this.payment_type = payment_type;
        this.payment_method = payment_method;
        this.provider = provider;
        this.reference_id = reference_id;
        this.amount = amount;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getReference_id() {
        return reference_id;
    }

    public void setReference_id(String reference_id) {
        this.reference_id = reference_id;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
