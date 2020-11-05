package app.zingo.mysolite.ui.landing;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import app.zingo.mysolite.Custom.CustomFontTextView;
import app.zingo.mysolite.WebApi.EmployeeApi;
import app.zingo.mysolite.model.Employee;
import app.zingo.mysolite.model.Organization;
import app.zingo.mysolite.ui.Company.CreateFounderScreen;
import app.zingo.mysolite.ui.EmployeeSignUp;
import app.zingo.mysolite.R;
import app.zingo.mysolite.utils.PreferenceHandler;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhoneVerificationScreen extends AppCompatActivity {

    LinearLayout mNumberLay,mOtpLay;
    CountryCodePicker mCountry;
    TextInputEditText mPhone,mOtp;
    CustomFontTextView mMobileNumWithCountry;
    AppCompatTextView mResend,mTimer;

    //Firebase & Auth
    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";
    private FirebaseAuth mAuth;
    private static final String TAG = "GuestLoginScreen";
    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    //private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    //MyCountDownTimer myCountDownTimer;
    Organization organization;
    String screen,phone;
    private boolean firstSend = true, timerOn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            setContentView(R.layout.activity_phone_verification_screen);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            initViews();
            Bundle bundle = getIntent().getExtras();
            if(bundle!=null){
                organization = ( Organization )bundle.getSerializable("Company");
                screen = bundle.getString("Screen");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    private void initViews ( ) {
        FirebaseApp.initializeApp( PhoneVerificationScreen.this);
        mAuth = FirebaseAuth.getInstance();
        mNumberLay = findViewById(R.id.number_layout);
        mOtpLay = findViewById(R.id.otp_layout);
        mCountry = findViewById(R.id.ccp);
        mPhone = findViewById(R.id.phone);
        mOtp = findViewById(R.id.otp);
        mMobileNumWithCountry = findViewById(R.id.mobile_number_text);
        mResend = findViewById(R.id.resend);
        mTimer = findViewById(R.id.timer);

        findViewById ( R.id.cancel ).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOtpLay.setVisibility(View.GONE);
                mNumberLay.setVisibility(View.VISIBLE);
                mPhone.setText("");
            }
        });

        findViewById(R.id.verify_number).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startResendTimer(30);
                firstSend = false;
                String phoneNumber = mPhone.getText().toString();
                if(phoneNumber.isEmpty() || phoneNumber.length() < 10){
                    mPhone.setError("Enter a valid mobile");
                    mPhone.requestFocus();
                    return;
                }else{
                    checkUserByPhone(phoneNumber,"Verify");
                }
            }
        });

        findViewById ( R.id.verify_code ).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mOtp.getText().toString().isEmpty()){
                    mOtp.setError("Please enter otp");
                }else{
                    try {
                        verifyVerificationCode (mOtp.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText( PhoneVerificationScreen.this, "Please try again sometime", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phone = mPhone.getText().toString();
                checkUserByPhone(phone,"Resend");
            }
        });
    }

    private void sendVerificationCode(String mobile) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber( mobile, 60, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD, mCallbacks);
        mVerificationInProgress = true;
    }


    //the callback to detect the verification status
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            mVerificationInProgress = false;
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            mVerificationInProgress = false;
            Toast.makeText(PhoneVerificationScreen.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            mVerificationId = s;
        }
    };

    private void verifyVerificationCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }


    private void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks,
                token);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(PhoneVerificationScreen.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            PreferenceHandler.getInstance( PhoneVerificationScreen.this).setPhoneNumber(mPhone.getText().toString());
                            if(screen!=null&&screen.equalsIgnoreCase("Organization")){
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("Company",organization);
                                Intent main = new Intent( PhoneVerificationScreen.this, CreateFounderScreen.class);
                                main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                main.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                main.putExtras(bundle);
                                main.putExtra("PhoneNumber",phone);
                                startActivity(main);
                                PhoneVerificationScreen.this.finish();
                            }else if(screen!=null&&screen.equalsIgnoreCase("Employee")){
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("Company",organization);
                                Intent main = new Intent( PhoneVerificationScreen.this, EmployeeSignUp.class);
                                main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                main.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                main.putExtras(bundle);
                                main.putExtra("PhoneNumber",phone);
                                startActivity(main);
                                PhoneVerificationScreen.this.finish();
                            }

                        } else {
                            Log.w("LoginActivity", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException ) {
                                mOtp.setError("Invalid code.");
                            }
                        }
                    }
                });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
    }

    public void startResendTimer(int seconds) {
        mTimer.setVisibility(View.VISIBLE);
        mResend.setEnabled(false);
        new CountDownTimer(seconds*1000, 1000) {
            public void onTick(long millisUntilFinished) {
                String secondsString = Long.toString(millisUntilFinished/1000);
                if (millisUntilFinished<10000) {
                    secondsString = "0"+secondsString;
                }
                mTimer.setText("0:"+ secondsString+"");
            }
            public void onFinish() {
                mResend.setEnabled(true);
                mTimer.setVisibility(View.GONE);
                timerOn=false;
            }
        }.start();
    }

    private void checkUserByPhone(final String phones,final String type){
        EmployeeApi apiService = app.zingo.mysolite.utils.Util.getClient().create( EmployeeApi.class);
        Call < ArrayList < Employee > > call = apiService.getUserByPhone(phones);
        call.enqueue(new Callback <ArrayList< Employee >> () {
            @Override
            public void onResponse( Call<ArrayList< Employee >> call, Response <ArrayList< Employee >> response) {
                int statusCode = response.code();
                if(statusCode == 200 || statusCode == 204) {
                    ArrayList< Employee > responseProfile = response.body();
                    if(responseProfile != null && responseProfile.size()!=0 ) {
                        mPhone.setError("Number Already Exists");
                        Toast.makeText( PhoneVerificationScreen.this, "Mobile already Exists", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        try {
                            if(type.equalsIgnoreCase("Verify")){
                                mNumberLay.setVisibility(View.GONE);
                                mOtpLay.setVisibility(View.VISIBLE);
                                mResend.setVisibility(View.GONE);
                                mTimer.setVisibility(View.VISIBLE);
                                phone = mPhone.getText().toString();
                                mMobileNumWithCountry.setText(""+mCountry.getSelectedCountryCodeWithPlus()+mPhone.getText().toString());
                                sendVerificationCode(mCountry.getSelectedCountryCodeWithPlus()+mPhone.getText().toString());

                            }else if(type.equalsIgnoreCase("Resend")){
                                resendVerificationCode(mCountry.getSelectedCountryCodeWithPlus()+mPhone.getText().toString(),mResendToken);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                else {
                    Toast.makeText( PhoneVerificationScreen.this,response.message(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure( Call<ArrayList< Employee >> call, Throwable t) {
                Log.e("TAG", t.toString());
            }
        });
    }
}
