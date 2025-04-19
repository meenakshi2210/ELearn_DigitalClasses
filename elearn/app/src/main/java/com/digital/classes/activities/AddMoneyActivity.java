package com.digital.classes.activities;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digital.classes.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.digital.classes.FirebaseViewModel;
import com.digital.classes.models.Wallet;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.paytm.pgsdk.TransactionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class AddMoneyActivity extends AppCompatActivity {

    private long deposits;
    //    private DatabaseReference depositsRef;
//    private FirebaseUser user;
    private long winnings;
    private int txnRequestCode = 110;
    private TextInputEditText editAmount;
    private TextInputLayout mobile_til;
    private TextInputEditText mobile_et;
    private String gatwayStatus;
    private ProgressDialog progressDialog;
    //    private DatabaseReference orderReference;
    private String payment_gateway;
    //    private PayUmoneySdkInitializer.PaymentParam mPaymentParams;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseViewModel viewModel;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_money);

        viewModel = new ViewModelProvider(this).get(FirebaseViewModel.class);
//        user = FirebaseAuth.getInstance().getCurrentUser();
//        depositsRef = FirebaseDatabase.getInstance().getReference("/users/"+user.getEmail()+"/Wallet/deposits");
        editAmount = findViewById(R.id.edit_amount);
        mobile_et = findViewById(R.id.edit_phone);
        mobile_til = findViewById(R.id.phoneInputLayout);
        textView = findViewById(R.id.wallet_balance_deposits);

        fetchUser();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setTitle("Please wait!");
        progressDialog.setMessage("Loading data...");

        DocumentReference gateway = FirebaseFirestore.getInstance().collection("Paytm").document("Gateway");
        gateway.addSnapshotListener((documentSnapshot, e) -> gatwayStatus = documentSnapshot.getString("status"));
        findViewById(R.id.amount_50).setOnClickListener(v -> editAmount.setText("50"));
        findViewById(R.id.amount_100).setOnClickListener(v -> editAmount.setText("100"));
        findViewById(R.id.amount_200).setOnClickListener(v -> editAmount.setText("200"));
        findViewById(R.id.btn_deposit).setOnClickListener(v -> {
            if (gatwayStatus.equals("ON")) {

                if (editAmount.getText().toString().isEmpty()) {
                    Snackbar.make(v, "Please enter valid value", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                int amount = Integer.valueOf(editAmount.getText().toString());
                if (amount < 20) {
                    Snackbar.make(v, "Minimum deposit limit is \u0B2920 ", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                // progressDialog.show();
                postDataUsingVolley(String.valueOf(amount));
            } else {
                androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(AddMoneyActivity.this);
                alertDialog.setTitle("Alert!");
                alertDialog.setMessage("Gateway is under processing. Please wait our new Update. :)");
                alertDialog.setCancelable(false);
                alertDialog.setPositiveButton("Ok", (dialog, which) -> dialog.dismiss()).show();
            }
        });
    }



    private void fetchUser() {
        viewModel.getWallet().observe(this, new Observer<Wallet>() {
            @Override
            public void onChanged(Wallet wallet) {
                textView.setText("\u20b9 " + wallet.getBalance());
                /*balance = wallet.getBalance();
                balanceTv2.setText("\u20b9 " + balance);
            */}
        });
    }
    private void postDataUsingVolley(String amount) {
        progressDialog.show();
        String order_id = UUID.randomUUID().toString(); // It should be unique
        // int amount = 10;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("paytm-getway-naveen.glitch.me")
                .appendQueryParameter("ORDER_ID", order_id)
                .appendQueryParameter("CUST_ID", FirebaseAuth.getInstance().getUid())
                .appendQueryParameter("TXN_AMOUNT", amount);

        String myUrl = builder.build().toString();

        RequestQueue queue = Volley.newRequestQueue(AddMoneyActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, myUrl, response -> {
            progressDialog.dismiss();

            try {
                JSONObject res = new JSONObject(response);
                String txnToken = res.getJSONObject("body").getString("txnToken");
                processTxn(order_id, txnToken, amount);
            } catch (JSONException e) {
                progressDialog.dismiss();
                Toast.makeText(AddMoneyActivity.this, "TXN_FAILED", Toast.LENGTH_SHORT).show();
            }


        }, error ->
                Toast.makeText(AddMoneyActivity.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show());
        {
            progressDialog.dismiss();
        }
        queue.add(request);
    }

    private void processTxn(String orderid, String txnToken, String amount) {
        String mid = "EARNIN13167495310659";

        String callbackurl = "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=" + orderid; // Production Environment:
        PaytmOrder paytmOrder = new PaytmOrder(orderid, mid, txnToken, amount, callbackurl);
        TransactionManager transactionManager = new TransactionManager(paytmOrder, new PaytmPaymentTransactionCallback() {
            @Override
            public void onTransactionResponse(@Nullable Bundle bundle) {
                if (bundle.getString("STATUS").compareTo("TXN_SUCCESS") == 0) {
                    Toast.makeText(AddMoneyActivity.this, "Transaction Successful", Toast.LENGTH_SHORT).show();
                    int amount = Float.valueOf(bundle.getString("TXNAMOUNT")).intValue();
                    progressDialog.setMessage("Updating Balance");
                    progressDialog.show();
                    viewModel.updateBalance(amount).addOnSuccessListener(aVoid -> {
                        Toast.makeText(AddMoneyActivity.this, "Update balance Successfully", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    });
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(AddMoneyActivity.this, "Transaction Failed!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void networkNotAvailable() {
                progressDialog.dismiss();
                Toast.makeText(AddMoneyActivity.this, "Check your Internet Connection!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onErrorProceed(String s) {
                progressDialog.dismiss();
                Toast.makeText(AddMoneyActivity.this, s, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void clientAuthenticationFailed(String s) {
                progressDialog.dismiss();
                Toast.makeText(AddMoneyActivity.this, "Authentication Failed!", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void someUIErrorOccurred(String s) {
                progressDialog.dismiss();
                Toast.makeText(AddMoneyActivity.this, "Ui Error!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onErrorLoadingWebPage(int i, String s, String s1) {
                progressDialog.dismiss();
                Toast.makeText(AddMoneyActivity.this, "WebPage Loding Error!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBackPressedCancelTransaction() {
                progressDialog.dismiss();
                //   Toast.makeText(MainActivity.this, "Check your Internet Connection!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTransactionCancel(String s, Bundle bundle) {
                progressDialog.dismiss();
                Toast.makeText(AddMoneyActivity.this, "Transaction Cancel!", Toast.LENGTH_SHORT).show();
            }
        });

        transactionManager.setAppInvokeEnabled(false);
        transactionManager.startTransaction(this, txnRequestCode);
    }

}