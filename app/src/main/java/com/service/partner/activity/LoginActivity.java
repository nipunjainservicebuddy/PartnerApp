package com.service.partner.activity;

import static com.service.partner.utils.SessionManager.currency;
import static com.service.partner.utils.SessionManager.login;
import static com.service.partner.utils.Utility.isvarification;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.service.partner.R;
import com.service.partner.model.LoginUser;
import com.service.partner.model.ResponseMessge;
import com.service.partner.model.User;
import com.service.partner.retrofit.APIClient;
import com.service.partner.retrofit.GetResult;
import com.service.partner.utils.CustPrograssbar;
import com.service.partner.utils.GPSTracker;
import com.service.partner.utils.SessionManager;
import com.service.partner.utils.Utility;
import com.google.firebase.FirebaseApp;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.onesignal.OneSignal;
import com.truecaller.android.sdk.ITrueCallback;
import com.truecaller.android.sdk.TrueError;
import com.truecaller.android.sdk.TrueProfile;
import com.truecaller.android.sdk.TruecallerSDK;
import com.truecaller.android.sdk.TruecallerSdkScope;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;


public class LoginActivity extends AppCompatActivity implements GetResult.MyListener {

    @BindView(R.id.startBtn)
    TextView startBtn;

    CustPrograssbar custPrograssbar;
    SessionManager sessionManager;
    public static TrueProfile trueProfile;
    String city;
    String address;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        FirebaseApp.initializeApp(this);
        requestPermissions(new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        custPrograssbar = new CustPrograssbar();
        sessionManager = new SessionManager(LoginActivity.this);
        startBtn = findViewById(R.id.startBtn);

        TruecallerSdkScope trueScope = new TruecallerSdkScope.Builder(this, sdkCallback)
                .consentMode(TruecallerSdkScope.CONSENT_MODE_BOTTOMSHEET)
                .buttonColor(Color.parseColor("#3050FF"))
                .buttonTextColor(Color.parseColor("#FFFFFF"))
                .loginTextPrefix(TruecallerSdkScope.LOGIN_TEXT_PREFIX_TO_GET_STARTED)
                .loginTextSuffix(TruecallerSdkScope.LOGIN_TEXT_SUFFIX_PLEASE_VERIFY_MOBILE_NO)
                .ctaTextPrefix(TruecallerSdkScope.CTA_TEXT_PREFIX_USE)
                .buttonShapeOptions(TruecallerSdkScope.BUTTON_SHAPE_ROUNDED)
                .privacyPolicyUrl("<<YOUR_PRIVACY_POLICY_LINK>>")
                .termsOfServiceUrl("<<YOUR_PRIVACY_POLICY_LINK>>")
                .footerType(TruecallerSdkScope.FOOTER_TYPE_NONE)
                .consentTitleOption(TruecallerSdkScope.SDK_CONSENT_TITLE_LOG_IN)
                .sdkOptions(TruecallerSdkScope.SDK_OPTION_WITHOUT_OTP)
                .build();

        TruecallerSDK.init(trueScope);
       // getCodelist();

        /*atCity.setOnFocusChangeListener((view, b) -> {
            if (!b) {
                // on focus off
                String str = atCity.getText().toString();
                ListAdapter listAdapter = atCity.getAdapter();
                for (int i = 0; i < listAdapter.getCount(); i++) {
                    String temp = listAdapter.getItem(i).toString();
                    if (str.compareTo(temp) == 0) {
                        return;
                    }
                }
                atCity.setText("");

            }
        });

        atCode.setOnFocusChangeListener((view, b) -> {
            if (!b) {
                // on focus off
                String str = atCode.getText().toString();

                ListAdapter listAdapter = atCode.getAdapter();
                for (int i = 0; i < listAdapter.getCount(); i++) {
                    String temp = listAdapter.getItem(i).toString();
                    if (str.compareTo(temp) == 0) {
                        return;
                    }
                }
                atCode.setText("");

            }
        });*/
    }

    /*private void getCodelist() {
        custPrograssbar.prograssCreate(LoginActivity.this);
        JSONObject jsonObject = new JSONObject();
        RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Call<JsonObject> call = APIClient.getInterface().getCodelist(bodyRequest);
        GetResult getResult = new GetResult();
        getResult.setMyListener(this);
        getResult.callForLogin(call, "2");

    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TruecallerSDK.SHARE_PROFILE_REQUEST_CODE) {
            TruecallerSDK.getInstance().onActivityResultObtained(LoginActivity.this, requestCode, resultCode, data);
        }
    }

    private void loginUser() {
        custPrograssbar.prograssCreate(LoginActivity.this);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mobile", trueProfile.phoneNumber.substring(3));
            jsonObject.put("password", trueProfile.phoneNumber.substring(3));
        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Call<JsonObject> call = APIClient.getInterface().loginUser(bodyRequest);
        GetResult getResult = new GetResult();
        getResult.setMyListener(this);
        getResult.callForLogin(call, "1");

    }


    ITrueCallback sdkCallback = new ITrueCallback() {

        @Override
        public void onSuccessProfileShared(@NonNull final TrueProfile trueProfile) {
            checkUser(trueProfile);

        }

        @Override
        public void onFailureProfileShared(@NonNull final TrueError trueError) {
        }

        @Override
        public void onVerificationRequired(@Nullable final TrueError trueError) {
        }

    };

    private void checkUser(TrueProfile trueProfile) {
        LoginActivity.trueProfile = trueProfile;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mobile", trueProfile.phoneNumber.substring(3));
            jsonObject.put("ccode", "+91");
            RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
            Call<JsonObject> call = APIClient.getInterface().getMobileCheck(bodyRequest);
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "3");
            custPrograssbar.prograssCreate(LoginActivity.this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void createUser(User user) {
        custPrograssbar.prograssCreate(this);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", user.getName());
            jsonObject.put("email", user.getEmail());
            jsonObject.put("ccode", user.getCcode());
            jsonObject.put("mobile", user.getMobile());
            jsonObject.put("password", user.getPassword());
            jsonObject.put("address", user.getAddress());
            jsonObject.put("city", user.getCity());
        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Call<JsonObject> call = APIClient.getInterface().createUser(bodyRequest);
        GetResult getResult = new GetResult();
        getResult.setMyListener(this);
        getResult.callForLogin(call, "2");

    }


    @OnClick({R.id.startBtn})
    public void onClick(View view) {
        switch (view.getId()) {

            /*case R.id.txt_slogin:
                txtSlogin.setBackground(getResources().getDrawable(R.drawable.tab1));
                txtSlogin.setTextColor(getResources().getColor(R.color.white));
                txtSregister.setBackground(getResources().getDrawable(R.drawable.tab2));
                txtSregister.setTextColor(getResources().getColor(R.color.black));
                lvlLogin.setVisibility(View.VISIBLE);
                lvlRegister.setVisibility(View.GONE);

                break;

            case R.id.txt_sregister:
                txtSlogin.setBackground(getResources().getDrawable(R.drawable.tab2));
                txtSlogin.setTextColor(getResources().getColor(R.color.black));
                txtSregister.setBackground(getResources().getDrawable(R.drawable.tab1));
                txtSregister.setTextColor(getResources().getColor(R.color.white));
                lvlLogin.setVisibility(View.GONE);
                lvlRegister.setVisibility(View.VISIBLE);
                break;

            case R.id.txt_register:
                if (validationCreate()) {
                    checkUser();

                }

                break;
            case R.id.txt_login:
                if (validation()) {
                    loginUser();
                }
                break;

            case R.id.txt_forgotpassword:
                startActivity(new Intent(LoginActivity.this, ForgotActivity.class));
                break;*/
            case R.id.startBtn:
                /*startActivity(new Intent(IntroActivity.this, LoginActivity.class));*/

                if (TruecallerSDK.getInstance().isUsable()) {
                    TruecallerSDK.getInstance().getUserProfile(LoginActivity.this);
                } else {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
                    dialogBuilder.setMessage("Trucaller App not installed");
                    dialogBuilder.setPositiveButton("Ok", (dialog, which) -> {
                        dialog.dismiss();
                    });
                    dialogBuilder.setIcon(R.drawable.com_truecaller_icon);
                    dialogBuilder.setTitle("");
                    AlertDialog aslertdialo = dialogBuilder.create();
                    aslertdialo.show();
                }


                break;
            default:
                break;
        }
    }

    @Override
    public void callback(JsonObject result, String callNo) {
        try {
            custPrograssbar.closePrograssBar();
            if (callNo.equalsIgnoreCase("1")) {
                /*Gson gson = new Gson();
                LoginUser loginUser = gson.fromJson(result.toString(), LoginUser.class);
                Toast.makeText(LoginActivity.this, loginUser.getResponseMsg(), Toast.LENGTH_LONG).show();
                if (loginUser.getResult().equalsIgnoreCase("true")) {
                    User user = loginUser.getUser();
                    OneSignal.deleteTag("Categoryid");
                    OneSignal.sendTag("PartnerId", user.getId());
                    user.setCategory(loginUser.getCategory());
                    sessionManager.setUserDetails("", user);
                    sessionManager.setStringData(currency, loginUser.getCurrency());
                    sessionManager.setBooleanData(login, true);
                    if (loginUser.getUser().getCategoryid().equalsIgnoreCase("0")) {
                        startActivity(new Intent(LoginActivity.this, CategoryActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    } else {
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    }

                }*/

                Utility.isvarification = -1;
                Gson gson = new Gson();

                LoginUser loginUser = gson.fromJson(result.toString(), LoginUser.class);
                Toast.makeText(this, loginUser.getResponseMsg(), Toast.LENGTH_LONG).show();
                if (loginUser.getResult().equalsIgnoreCase("true")) {
                    sessionManager.setUserDetails("", loginUser.getUser());
                    sessionManager.setBooleanData(login, true);
                    sessionManager.setStringData("id",loginUser.getUser().getId());
                    Intent intent = new Intent(this, HomeActivity.class);
                    //intent.putExtra("id",loginUser.getUser().getId());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            } else if (callNo.equalsIgnoreCase("2")) {
                isvarification = -1;
                Gson gson = new Gson();

                LoginUser loginUser = gson.fromJson(result.toString(), LoginUser.class);
                Toast.makeText(this, loginUser.getResponseMsg(), Toast.LENGTH_LONG).show();
                if (loginUser.getResult().equalsIgnoreCase("true")) {
                    User user = loginUser.getUser();

                    sessionManager.setUserDetails("", user);
                    startActivity(new Intent(this, CategoryActivity.class));
                    finish();
                }
            } /*if (callNo.equalsIgnoreCase("2")) {
                Gson gson = new Gson();
                ContryCode contryCode = gson.fromJson(result.toString(), ContryCode.class);
                ArrayList<String> countries = new ArrayList<>();
                ArrayList<String> city = new ArrayList<>();
                if (contryCode.getResult().equalsIgnoreCase("true")) {
                    countries = new ArrayList<>();
                    for (int i = 0; i < contryCode.getCountryCode().size(); i++) {
                        countries.add(contryCode.getCountryCode().get(i).getCcode());

                    }
                    for (int i = 0; i < contryCode.getCityList().size(); i++) {
                        city.add(contryCode.getCityList().get(i).getCname());
                    }
                }
                ArrayAdapter<String> citylist = new ArrayAdapter<>
                        (this, android.R.layout.simple_list_item_1, city);

                ArrayAdapter<String> adapter = new ArrayAdapter<>
                        (this, android.R.layout.simple_list_item_1, countries);
                atCode.setAdapter(adapter);
                atCode.setThreshold(1);

                atCity.setAdapter(citylist);
                atCity.setThreshold(1);
            }*/ else if (callNo.equalsIgnoreCase("3")) {
                Gson gson = new Gson();
                ResponseMessge response = gson.fromJson(result.toString(), ResponseMessge.class);
                GPSTracker gpsTracker = new GPSTracker(this);
                Geocoder gcd = new Geocoder(LoginActivity.this, Locale.getDefault());
                if(gpsTracker.getIsGPSTrackingEnabled()){
                    double latitude = gpsTracker.getLatitude();
                    double longitude = gpsTracker.getLongitude();


                    List<Address> addresses = gcd.getFromLocation(latitude,longitude,1);
                    city = addresses.get(0).getLocality();
                    address = addresses.get(0).getAddressLine(0);

                }else{
                    gpsTracker.showSettingsAlert();
                }


                if (response.getResult().equals("true")) {
                    Utility.isvarification = 1;
                    User user = new User();
                    user.setName(trueProfile.firstName + " " + trueProfile.lastName);
                    user.setEmail(trueProfile.email);
                    user.setCcode(trueProfile.countryCode);
                    user.setMobile(trueProfile.phoneNumber.substring(3));
                    user.setPassword(trueProfile.phoneNumber.substring(3));
                    user.setCity(city);
                    user.setAddress(address);
                    sessionManager.setUserDetails("", user);
                    createUser(user);

                }
                else {
                    Toast.makeText(LoginActivity.this, "" + response.getResponseMsg(), Toast.LENGTH_LONG).show();

                }
                if (response.getResponseMsg().equals("Already Exist Mobile Number!")) {
                    loginUser();
                }
            } /*if (callNo.equalsIgnoreCase("3")) {
                Gson gson = new Gson();
                ResponseMessge response = gson.fromJson(result.toString(), ResponseMessge.class);
                if (response.getResult().equals("true")) {
                    Utility.isvarification = 1;
                    User user = new User();
                    user.setName(edName.getText().toString());
                    user.setEmail(edEmailnew.getText().toString());
                    user.setCcode(atCode.getText().toString());
                    user.setMobile(edMobile.getText().toString());
                    user.setPassword(edPassswordnew.getText().toString());
                    user.setCity(atCity.getText().toString());
                    user.setAddress(edAddress.getText().toString());
                    user.setPassword(edPassswordnew.getText().toString());
                    sessionManager.setUserDetails("", user);
                    startActivity(new Intent(this, VerifyPhoneActivity.class).putExtra("code", atCode.getText().toString()).putExtra("phone", edMobile.getText().toString()));
                } else {
                    Toast.makeText(LoginActivity.this, "" + response.getResponseMsg(), Toast.LENGTH_LONG).show();

                }
            }*/
        } catch (Exception e) {
            Log.e("Errror", "==>" + e.toString());

        }
    }

   /* public boolean validation() {
        if (TextUtils.isEmpty(edEmail.getText().toString())) {
            edEmail.setError("Enter Email");
            return false;
        }
        if (TextUtils.isEmpty(edPasssword.getText().toString())) {
            edPasssword.setError("Enter Password");
            return false;
        }
        return true;
    }

    public boolean validationCreate() {
        if (TextUtils.isEmpty(edName.getText().toString())) {
            edName.setError("Enter Name");
            return false;
        }
        if (TextUtils.isEmpty(edEmailnew.getText().toString())) {
            edEmailnew.setError("Enter Email");
            return false;
        }
        if (TextUtils.isEmpty(atCode.getText().toString())) {
            atCode.setError("Enter Code");
            return false;
        }

        if (TextUtils.isEmpty(edMobile.getText().toString())) {
            edMobile.setError("Enter Mobile");
            return false;
        }
        if (TextUtils.isEmpty(edPassswordnew.getText().toString())) {
            edPassswordnew.setError("Enter Password");
            return false;
        }
        if (TextUtils.isEmpty(edAddress.getText().toString())) {
            edAddress.setError("Enter Address");
            return false;
        }
        if (TextUtils.isEmpty(atCity.getText().toString())) {
            atCity.setError("Enter City");
            return false;
        }
        if (!TextUtils.isEmpty(atCity.getText().toString())) {
            String str = atCity.getText().toString();

            ListAdapter listAdapter = atCity.getAdapter();
            for (int i = 0; i < listAdapter.getCount(); i++) {
                String temp = listAdapter.getItem(i).toString();
                if (str.compareTo(temp) == 0) {
                    return true;
                }
            }

            atCity.setText("");
            atCity.setError("Enter City");

            return false;
        }

        return true;
    }*/
}