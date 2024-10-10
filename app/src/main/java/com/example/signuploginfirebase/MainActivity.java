package com.example.signuploginfirebase;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.signuploginfirebase.databinding.ActivityMainBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;


public class MainActivity extends AppCompatActivity {

    TextView userName;
    GoogleSignInClient gClient;
    GoogleSignInOptions gOptions;
    ActivityMainBinding binding ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bottomNavigationView.setBackground(null);

        if (savedInstanceState == null) {
            new Handler().postDelayed(() -> {
                replaceFragment(new home()); // Open ProfileFragment after milliseconds
            }, 1);
        }

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.routine) {
                replaceFragment(new routine());
                return true;
            } else if (item.getItemId() == R.id.profile) {
                replaceFragment(new profile());
                return true;
            } else if (item.getItemId() == R.id.settings) {
                replaceFragment(new settings());
                return true;
            } else if (item.getItemId() == R.id.add) {
                ShowBottomSheet();
            } else if (item.getItemId() == R.id.home) {
                replaceFragment(new home());
                return true;
            }
            return false; // If none of the items match
        });

        //userName = findViewById(R.id.userName);

        gOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gClient = GoogleSignIn.getClient(this, gOptions);

        GoogleSignInAccount gAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (gAccount != null){
            String gName = gAccount.getDisplayName();
            userName.setText(gName);
        }
    }

    private void replaceFragment (Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }
    private void ShowBottomSheet(){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, com.google.android.material.R.style.Theme_Material3_Light_BottomSheetDialog);
        getLayoutInflater();
        View view = LayoutInflater.from(this).inflate(R.layout.add,null,false);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
        if(bottomSheetDialog.isShowing()){
            MaterialButton Button = view.findViewById(R.id.addbtn);
            TextView msg = view.findViewById(R.id.message);
            Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this, "Routine added successfully!", Toast.LENGTH_SHORT).show();
                    bottomSheetDialog.dismiss();
                }

            });
        }
    }
}

