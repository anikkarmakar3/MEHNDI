package com.adretsoftware.mehndipvcinterior;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.adretsoftware.mehndipvcinterior.daos.MySharedStorage;
import com.adretsoftware.mehndipvcinterior.daos.Utilis;
import com.adretsoftware.mehndipvcinterior.databinding.ActivityMyAccountBinding;
import com.adretsoftware.mehndipvcinterior.ui.InvoicesActivity;
import com.adretsoftware.mehndipvcinterior.ui.MyChartActivity;
import com.adretsoftware.mehndipvcinterior.ui.MyTeam;
import com.adretsoftware.mehndipvcinterior.ui.NewInvoiceActivity;

public class MyAccount extends AppCompatActivity {
    ActivityMyAccountBinding binding;
    TextView headingorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        headingorder = findViewById(R.id.heading);

        binding.gridLayout.removeView(binding.btnPaymentHistory);

        MySharedStorage.INSTANCE.func(this);
        binding.invoiceSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(getApplicationContext(), webhostMyOrder.class));
                /*startActivity(new Intent(getApplicationContext(), InvoicesActivity.class));*/
                startActivity(new Intent(getApplicationContext(), NewInvoiceActivity.class));
            }
        });

        binding.btnMyTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MyTeam.class));
            }
        });

        binding.btnMyChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MyChartActivity.class));
            }
        });

        binding.btnPaymentHistory.setOnClickListener(v -> {
            startActivity(new Intent(MyAccount.this, PaymentHistory.class));
        });

        if (!Utilis.INSTANCE.isLoginAsAdmin()) {
            binding.gridLayout.removeView(binding.btnStatement);
        }

        binding.btnStatement.setOnClickListener(v -> {
            startActivity(new Intent(MyAccount.this, Statements.class));
        });

//        binding.logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MySharedStorage.INSTANCE.setUserId("");
//                startActivity(new Intent(getApplicationContext(),Login.class));
//                finish() ;
//            }
//        });


    }
}