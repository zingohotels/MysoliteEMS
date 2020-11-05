package app.zingo.mysolite.adapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import app.zingo.mysolite.R;
import app.zingo.mysolite.WebApi.EmployeeApi;
import app.zingo.mysolite.model.Employee;
import app.zingo.mysolite.ui.NewAdminDesigns.UpdateRetailer;
import app.zingo.mysolite.utils.PreferenceHandler;
import app.zingo.mysolite.utils.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetailerAdapter extends RecyclerView.Adapter< RetailerAdapter.ViewHolder>{
    private Context context;
    private ArrayList < Employee > list;
    private String type;

    public RetailerAdapter(Context context, ArrayList<Employee> list, String type) {
        this.context = context;
        this.list = list;
        this.type = type;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder( @NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_profile_list, parent, false);
        return new ViewHolder (v);
    }

    @Override
    public void onBindViewHolder( @NonNull final ViewHolder holder, final int position) {
        final Employee dto = list.get(position);
        holder.mProfileName.setText(dto.getEmployeeName());
        holder.mProfileEmail.setText(dto.getPrimaryEmailAddress());

        holder.mProfileMain.setOnClickListener( v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Do you want to do ?");
            builder.setCancelable(true);
            builder.setPositiveButton("Update", ( dialogInterface , i ) -> {
                Intent intent = new Intent(context, UpdateRetailer.class);
                Bundle bundle = new Bundle();
                bundle.putInt("EmployeeId",list.get(position).getEmployeeId());
                bundle.putInt("OrganizationId", PreferenceHandler.getInstance(context).getBranchId());
                bundle.putSerializable("Employee",list.get(position));
                intent.putExtras(bundle);
                context.startActivity(intent);
            } );
            builder.setNegativeButton("Delete", ( dialogInterface , i ) -> {
                dialogInterface.dismiss();
                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                builder1.setTitle("Do you want to delete?");
                builder1.setCancelable(false);
                builder1.setPositiveButton("Confirm", ( dialogInterface1 , i1 ) -> deleteprofile(dto.getEmployeeId()) );
                builder1.setNegativeButton("Dismiss", ( dialogInterface12 , i12 ) -> {
                    dialogInterface12.dismiss();
                    holder.mProfileMain.setBackgroundColor( Color.parseColor("#ffffff"));
                } );

                AlertDialog dialog = builder1.create();
                dialog.show();
            } );

            AlertDialog dialog = builder.create();
            dialog.show();
        } );
    }


    private void deleteprofile(final int id){
        EmployeeApi apiService = Util.getClient().create( EmployeeApi.class);
        Call <Employee> call = apiService.deletEmployee(id);
        call.enqueue(new Callback<Employee>() {
            @Override
            public void onResponse(@NonNull Call<Employee> call, @NonNull Response<Employee> response) {
                int statusCode = response.code();
                if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {
                    Intent refresh = new Intent(context,context.getClass());
                    context.startActivity(refresh);
                    (( Activity )context).finish();

                }else {

                    Toast.makeText(context, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure( @NonNull Call<Employee> call, @NonNull Throwable t) {
                Log.e("TAG", t.toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView mProfileName,mProfileEmail;
        CardView mProfileMain;

        public ViewHolder( View itemView) {
            super(itemView);
            itemView.setClickable(true);
            mProfileName = itemView.findViewById(R.id.profile_name_adapter);
            mProfileEmail = itemView.findViewById(R.id.profile_email_adapter);
            mProfileMain = itemView.findViewById(R.id.profileLayout);
        }
    }
}
