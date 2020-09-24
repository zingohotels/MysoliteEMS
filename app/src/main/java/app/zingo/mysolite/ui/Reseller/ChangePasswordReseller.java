package app.zingo.mysolite.ui.Reseller;

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

public class ChangePasswordReseller extends AppCompatActivity {

    TextInputEditText mOldPassword,mNewPassword,mConfirm;
    AppCompatButton mSave;


    ResellerProfiles profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_change_password_reseller);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Change Password");

            mOldPassword = findViewById(R.id.oldpassword);
            mNewPassword = findViewById(R.id.newpwd);
            mConfirm = findViewById(R.id.confirmpwd);
            mSave = findViewById(R.id.savePassword);

            final int userId = PreferenceHandler.getInstance( ChangePasswordReseller.this).getResellerUserId();

            if(userId!=0){
                getProfile(userId);
            }else{

                Toast.makeText(this, "Please Try again Sometime", Toast.LENGTH_SHORT).show();
            }

            mSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(profile!=null){
                        validate();
                    }else{
                        getProfile(userId);
                    }
                }
            });


        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public void getProfile(final int id){

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setTitle("Please Wait..");
        dialog.show();

        new ThreadExecuter ().execute( new Runnable() {
            @Override
            public void run() {

                final ResellerAPI subCategoryAPI = Util.getClient().create(ResellerAPI.class);
                Call<ResellerProfiles> getProf = subCategoryAPI.getResellerProfileById(id);
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback<ResellerProfiles>() {

                    @Override
                    public void onResponse(Call<ResellerProfiles> call, Response<ResellerProfiles> response) {

                        if(dialog != null)
                        {
                            dialog.dismiss();
                        }
                        if (response.code() == 200)
                        {
                            System.out.println("Inside api");



                            if(response.body()!=null){
                                profile = response.body();
                            }

                        }else{
                            Toast.makeText( ChangePasswordReseller.this, "Failed due to status code"+response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResellerProfiles> call, Throwable t) {

                        if(dialog != null)
                        {
                            dialog.dismiss();
                        }
                        Toast.makeText( ChangePasswordReseller.this , "Something went wrong due to " + "Bad Internet Connection" , Toast.LENGTH_SHORT ).show( );

                    }
                });

            }

        });
    }

    public void validate(){

        String oldPwd = mOldPassword.getText().toString();
        String newPwd = mNewPassword.getText().toString();
        String cnfrmPwd = mConfirm.getText().toString();

        if(oldPwd.isEmpty()){

            Toast.makeText(this, "Old password is required", Toast.LENGTH_SHORT).show();

        }else if(newPwd.isEmpty()){

            Toast.makeText(this, "New Password is required", Toast.LENGTH_SHORT).show();

        }else if(cnfrmPwd.isEmpty()){

            Toast.makeText(this, "Confirm password is required", Toast.LENGTH_SHORT).show();

        }else if(!oldPwd.isEmpty()&&!oldPwd.equalsIgnoreCase(profile.getPassword())){

            Toast.makeText(this, "Please enter valid old password", Toast.LENGTH_SHORT).show();

        }else if(!newPwd.isEmpty()&&!cnfrmPwd.isEmpty()&&!newPwd.equalsIgnoreCase(cnfrmPwd)){

            Toast.makeText(this, "Password does not match", Toast.LENGTH_SHORT).show();

        }else if(!newPwd.isEmpty()&&!oldPwd.isEmpty()&&newPwd.equalsIgnoreCase(oldPwd)){

            Toast.makeText(this, "New Password could not be your old password", Toast.LENGTH_SHORT).show();

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
                            PreferenceHandler.getInstance( ChangePasswordReseller.this).clear();

                            Intent log = new Intent( ChangePasswordReseller.this, LandingScreen.class);
                            log.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            log.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            Toast.makeText( ChangePasswordReseller.this,"Logout",Toast.LENGTH_SHORT).show();
                            startActivity(log);
                            finish();

                        }else{
                            Toast.makeText( ChangePasswordReseller.this, "Failed due to status code"+response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResellerProfiles> call, Throwable t) {

                        if(dialog != null)
                        {
                            dialog.dismiss();
                        }
                        Toast.makeText( ChangePasswordReseller.this , "Something went wrong due to " + "Bad Internet Connection" , Toast.LENGTH_SHORT ).show( );

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

                ChangePasswordReseller.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}

