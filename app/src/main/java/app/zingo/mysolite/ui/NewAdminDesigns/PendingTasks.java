package app.zingo.mysolite.ui.NewAdminDesigns;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Objects;
import app.zingo.mysolite.adapter.TaskAdminListAdapter;
import app.zingo.mysolite.adapter.TaskListAdapter;
import app.zingo.mysolite.model.TaskAdminData;
import app.zingo.mysolite.model.Tasks;
import app.zingo.mysolite.R;

public class PendingTasks extends AppCompatActivity {
    private ArrayList< TaskAdminData > pendingTasks;
    private ArrayList<Tasks> pendingTasksNormal;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            setContentView(R.layout.activity_pending_tasks);
            Objects.requireNonNull ( getSupportActionBar ( ) ).setHomeButtonEnabled(true);
            Objects.requireNonNull ( getSupportActionBar ( ) ).setDisplayHomeAsUpEnabled(true);
            Objects.requireNonNull ( getSupportActionBar ( ) ).setTitle ( "Pending Task" );
            RecyclerView mTaskList = findViewById ( R.id.task_list_dash );
            Bundle bundle = getIntent().getExtras();
            if(bundle!=null){
                pendingTasks = (ArrayList< TaskAdminData >)bundle.getSerializable("PendingTasks");
               // pendingTasksNormal = (ArrayList<Tasks>)bundle.getSerializable("PendingTasksPendingTasksNormal");
                pendingTasksNormal = (ArrayList<Tasks>)bundle.getSerializable("PendingTasksNormal");
                title =bundle.getString("Title");
            }
            if(title!=null&&!title.isEmpty()){
                setTitle(title);
            }else{
                setTitle("Tasks");
            }
            if(pendingTasks!=null&&pendingTasks.size()!=0){
                TaskAdminListAdapter mAdapter = new TaskAdminListAdapter ( PendingTasks.this,pendingTasks);
                mTaskList.setAdapter(mAdapter);
            }else if(pendingTasksNormal!=null&&pendingTasksNormal.size()!=0){
                TaskListAdapter mAdapter = new TaskListAdapter ( PendingTasks.this,pendingTasksNormal);
                mTaskList.setAdapter(mAdapter);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected( @NotNull MenuItem item) {
        int id = item.getItemId();
        if ( id == android.R.id.home ) {
            PendingTasks.this.finish ( );
        }
        return super.onOptionsItemSelected(item);
    }
}
