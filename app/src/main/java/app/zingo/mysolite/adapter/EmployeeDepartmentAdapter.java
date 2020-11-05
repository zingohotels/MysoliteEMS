package app.zingo.mysolite.adapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import app.zingo.mysolite.model.Employee;
import app.zingo.mysolite.ui.Admin.EmployeesDashBoard;
import app.zingo.mysolite.R;

/**
 * Created by ZingoHotels Tech on 29-09-2018.
 */

public class EmployeeDepartmentAdapter extends RecyclerView.Adapter< EmployeeDepartmentAdapter.ViewHolder>{
    private Context context;
    private ArrayList< Employee > list;

    public EmployeeDepartmentAdapter(Context context, ArrayList< Employee > list) {
        this.context = context;
        this.list = list;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder( @NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_profile_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder( @NonNull final ViewHolder holder, final int position) {
        final Employee dto = list.get(position);
        holder.mProfileName.setText(dto.getEmployeeName());
        holder.mProfileEmail.setText(dto.getPrimaryEmailAddress());
        holder.mProfileMain.setOnClickListener( v -> {
            Intent intent = new Intent(context, EmployeesDashBoard.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("Profile",list.get(position));
            bundle.putInt("ProfileId",list.get(position).getEmployeeId());
            intent.putExtras(bundle);
            context.startActivity(intent);
        } );
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/ {
        private TextView mProfileName,mProfileEmail;
        private CardView mProfileMain;
        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);
            mProfileName = itemView.findViewById(R.id.profile_name_adapter);
            mProfileEmail = itemView.findViewById(R.id.profile_email_adapter);
            mProfileMain = itemView.findViewById(R.id.profileLayout);
        }
    }
}
