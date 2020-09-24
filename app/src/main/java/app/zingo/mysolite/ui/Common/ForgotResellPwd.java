package app.zingo.mysolite.ui.Common;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import app.zingo.mysolite.model.ResellerProfiles;
import app.zingo.mysolite.ui.LandingScreen;
import app.zingo.mysolite.utils.PreferenceHandler;
import app.zingo.mysolite.utils.ThreadExecuter;
import app.zingo.mysolite.utils.Util;
import app.zingo.mysolite.WebApi.ResellerAPI;
import app.zingo.mysolite.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotResellPwd extends AppCompatActivity {

    TextInputEditText mNewPassword,mConfirm;
    AppCompatButton mSave;


    ResellerProfiles profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_forgot_resell_pwd);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Change Password");


            mNewPassword = findViewById(R.id.newpwd);
            mConfirm = findViewById(R.id.confirmpwd);
            mSave = findViewById(R.id.savePassword);


            Bundle bundle = getIntent().getExtras();

            if(bundle!=null){
                profile = (ResellerProfiles)bundle.getSerializable("Reseller");
            }else{
                Toast.makeText( ForgotResellPwd.this, "Please try again sometime", Toast.LENGTH_SHORT).show();
            }



            mSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(profile!=null){
                        validate();
                    }else{
                        Toast.makeText( ForgotResellPwd.this, "Please try again sometime", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }catch(Exception e){
            e.printStackTrace();
        }

    }



    public void validate(){


        String newPwd = mNewPassword.getText().toString();
        String cnfrmPwd = mConfirm.getText().toString();

        if(newPwd.isEmpty()){

            Toast.makeText(this, "New Password is required", Toast.LENGTH_SHORT).show();

        }else if(cnfrmPwd.isEmpty()){

            Toast.makeText(this, "Confirm password is required", Toast.LENGTH_SHORT).show();

        }else if(!newPwd.isEmpty()&&!cnfrmPwd.isEmpty()&&!newPwd.equalsIgnoreCase(cnfrmPwd)){

            Toast.makeText(this, "Password does not match", Toast.LENGTH_SHORT).show();

        }else{
            ResellerProfiles employee = profile;
            profile.setPassword(cnfrmPwd);
            updateProfile(employee);
        }
    }


    public void updateProfile(final ResellerProfiles employee){

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setTitle("Updaitng Password");
        dialog.show();

        new ThreadExecuter ().execute( new Runnable() {
            @Override
            public void run() {

                final ResellerAPI subCategoryAPI = Util.getClient().create(ResellerAPI.class);
                Call<ResellerProfiles> getProf = subCategoryAPI.updateResellerProfiles(employee.getResellerProfileId(),employee);
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback<ResellerProfiles>() {

                    @Override
                    public void onResponse(Call<ResellerProfiles> call, Response<ResellerProfiles> response) {

                        if(dialog != null)
                        {
                            dialog.dismiss();
                        }
                        if (response.code() == 200||response.code()==201||response.code()==204)
                        {
                            PreferenceHandler.getInstance( ForgotResellPwd.this).clear();

                            Intent log = new Intent( ForgotResellPwd.this, LandingScreen.class);
                            log.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            log.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            Toast.makeText( ForgotResellPwd.this,"Logout",Toast.LENGTH_SHORT).show();
                            startActivity(log);
                            finish();

                        }else{
                            Toast.makeText( ForgotResellPwd.this, "Failed due to status code"+response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResellerProfiles> call, Throwable t) {

                        if(dialog != null)
                        {
                            dialog.dismiss();
                        }
                        //Toast.makeText(ForgotResellPwd.this, "Something went wrong due to "+"Bad Internet Connection", Toast.LENGTH_SHORT).show();

                    }
                });

            }

        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:

                ForgotResellPwd.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}

