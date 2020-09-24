package app.zingo.mysolite.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

import app.zingo.mysolite.Custom.MyRegulerText;
import app.zingo.mysolite.Custom.MyTextView;
import app.zingo.mysolite.WebApi.EmployeeApi;
import app.zingo.mysolite.model.Employee;
import app.zingo.mysolite.ui.Common.CustomerCreation;
import app.zingo.mysolite.R;
import app.zingo.mysolite.utils.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Employee> customers;

    public CustomerAdapter(Context context, ArrayList< Employee > customers)
    {
        this.context = context;
        this.customers = customers;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.branch_list_adapter,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final Employee customer = customers.get(position);

        if(customer!=null){

            holder.mOrgName.setText(""+customer.getEmployeeName ());
            holder.mAddress.setText(""+customer.getAddress ());

            holder.mBranch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    alertDialog(customer);
                }
            });
        }


    }

    private void alertDialog(final Employee customer) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.dialog_title);
        builder.setCancelable(true);

        builder.setPositiveButton(
                "Update",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent customers = new Intent(context, CustomerCreation.class);
                        Bundle bun = new Bundle();
                        bun.putSerializable("Customer",customer);
                        customers.putExtras(bun);
                        ((Activity)context).startActivity(customers);
                        dialog.cancel();
                    }
                });

        builder.setNegativeButton(
                "Delete",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DeleteCustomer(customer.getEmployeeId ());
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder.create();
        alert11.show();
    }

    private void DeleteCustomer(int customerId) {
        EmployeeApi mService = Util.getClient ().create (EmployeeApi.class);
        Call <Employee> call = mService.deletEmployee (customerId);
        call.enqueue (new Callback <Employee> () {
            @Override
            public void onResponse(Call <Employee> call, Response <Employee> response) {
                int statusCode = response.code();
                if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {
                    Intent refresh = new Intent(context,context.getClass());
                    context.startActivity(refresh);
                    ((Activity)context).finish();

                }else {

                    Toast.makeText(context, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call <Employee> call, Throwable t) {
                Toast.makeText (context, "Please Check your internet Connection and try after some time", Toast.LENGTH_SHORT).show ();
            }
        });
    }

    @Override
    public int getItemCount() {
        return customers.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        MyTextView mOrgName;
        MyRegulerText mAddress;
        LinearLayout mBranch;

        public ViewHolder(View itemView) {
            super(itemView);

            mOrgName = itemView.findViewById(R.id.branch_name_adapter);
            mAddress = itemView.findViewById(R.id.branch_address);
            mBranch = itemView.findViewById(R.id.branch_click_layout);


        }
    }


}

