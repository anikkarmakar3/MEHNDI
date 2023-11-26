package com.adretsoftware.mehndipvcinterior;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.adretsoftware.mehndipvcinterior.daos.Constants;
import com.adretsoftware.mehndipvcinterior.daos.MySharedStorage;
import com.adretsoftware.mehndipvcinterior.daos.Utilis;
import com.adretsoftware.mehndipvcinterior.databinding.ActivityOrdersPageBinding;

public class OrdersActivity extends AppCompatActivity {
    ActivityOrdersPageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrdersPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Utilis.INSTANCE.isLoginAsAdmin()) {
            binding.gridLayout.removeView(binding.shop);
        }

        if (MySharedStorage.INSTANCE.getUserType().equals(Constants.INSTANCE.getRETAILER())) {
            binding.gridLayout.removeView(binding.btnManageOrders);
        }

        binding.myordercardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MyOrder.class));
            }
        });

        binding.orderManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), OrdersSeller.class));
            }
        });
    }
}