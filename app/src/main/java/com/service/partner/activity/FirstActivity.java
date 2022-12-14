package com.service.partner.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.service.partner.R;
import com.service.partner.model.User;
import com.service.partner.utils.SessionManager;
import com.onesignal.OneSignal;


import static com.service.partner.utils.SessionManager.login;


public class FirstActivity extends AppCompatActivity {

    SessionManager sessionManager;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        sessionManager = new SessionManager(FirstActivity.this);
        user = sessionManager.getUserDetails("");
        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    if (sessionManager.getBooleanData(login)) {
                        if (sessionManager.getStringData("categoryid") != null) {
                            OneSignal.deleteTag("Categoryid");
                            OneSignal.sendTag("PartnerId", user.getId());
                            Intent openMainActivity = new Intent(FirstActivity.this, HomeActivity.class);
                            startActivity(openMainActivity);
                            finish();
                        } else {
                            Intent openMainActivity = new Intent(FirstActivity.this, CategoryActivity.class);
                            startActivity(openMainActivity);
                            finish();
                        }

                    } else {
                        Intent openMainActivity = new Intent(FirstActivity.this, LoginActivity.class);
                        startActivity(openMainActivity);
                        finish();
                    }

                }
            }
        };
        timer.start();
    }
}