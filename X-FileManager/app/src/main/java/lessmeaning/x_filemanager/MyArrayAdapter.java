package lessmeaning.x_filemanager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Максим on 11.10.2016.
 */
public class MyArrayAdapter extends ArrayAdapter<String> implements View.OnClickListener {
    MainActivity activity;
    String values[];
    HashMap<View,String> absPaths;
    public MyArrayAdapter(MainActivity context, String values[], ListView listView) {
        super(context, R.layout.filerow, values);
        activity = context;
        this.values = values;
        absPaths = new HashMap<>();
        listView.setAdapter(this);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater= (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout newView = (RelativeLayout) inflater.inflate(R.layout.filerow, parent,false);
        Button content = (Button) newView.findViewById(R.id.content);
        absPaths.put(content, values[position]);
        content.setOnClickListener(this);
        String path[] = values[position].split("/");
        content.setText(path[path.length - 1]);
        return newView;
    }

    @Override
    public void onClick(View view) {
        activity.openFile(absPaths.get(view));
    }

}