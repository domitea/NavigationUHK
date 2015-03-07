package uhk.kikm.navigationuhk;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uhk.kikm.navigationuhk.model.CouchDBManager;
import uhk.kikm.navigationuhk.model.Position;


public class ListPositionsActivity extends ActionBarActivity {

    CouchDBManager dbManager;
    List<Position> positions;
    Map<String, String> positionsMap;
    ArrayList<String> positionsStrings;
    ListView lv;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_positions);

        dbManager = new CouchDBManager(this);
        System.out.println("Open db connection in ListPositionsActivity");

        positionsMap = new HashMap<>();

        makeDataForView();

        lv = (ListView) findViewById(R.id.listView);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, positionsStrings);

        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(positionsStrings.get(position));
                buildDialogForRemove(positionsStrings.get(position));

            }
        });
    }

    private void makeDataForView() {
        positionsMap.clear();

        positions = dbManager.getAllPositions();

        for (Position p : positions)
        {
              positionsMap.put(String.valueOf(p.getX()) + " " + String.valueOf(p.getY()) + " " + p.getId(), p.getId());
        }

        positionsStrings = new ArrayList<String>(positionsMap.keySet());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_positions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.action_upload)
        {
            dbManager.uploadDBToServer(this);
        }
        else if (id == R.id.action_download)
        {
            dbManager.downloadDBFromServer(this);
            makeDataForView();
            adapter.notifyDataSetChanged();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();

        dbManager.closeConnection();
        System.out.println("Close db connection in ListPositionsActivity");
    }

    private void buildDialogForRemove(final String position)
    {

        Intent intent = new Intent(this, PositionInfoActivity.class);
        intent.putExtra("id", positionsMap.get(position));
        startActivity(intent);

        /*AlertDialog.Builder removeDialog = new AlertDialog.Builder(this);

        removeDialog.setTitle("Odstranění Pozice");
        removeDialog.setMessage("Chce odstranit vybranou pozici?");

        removeDialog.setPositiveButton("Odstranit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("Destroy!!! " + positionsMap.get(position));
                removePosition(position);
            }
        });

        removeDialog.setNegativeButton("Zrušit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("not yet...");
            }
        });

        removeDialog.create().show();*/
    }

    private void removePosition(String position)
    {
        dbManager.removePosition(positionsMap.get(position));

        positionsStrings.remove(position);
        positionsMap.remove(position);

        adapter.notifyDataSetChanged();
    }

}
