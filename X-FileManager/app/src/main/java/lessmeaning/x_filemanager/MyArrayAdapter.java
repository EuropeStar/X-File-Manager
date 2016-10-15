package lessmeaning.x_filemanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.io.File;
import java.util.HashMap;

/**
 * Created by Максим on 11.10.2016.
 */
public class MyArrayAdapter extends ArrayAdapter<String> {
    MainActivity activity;
    String values[];
    public MyArrayAdapter(MainActivity context, String values[], ListView listView) {
        super(context, R.layout.filerow, values);
        activity = context;
        this.values = values;
        listView.setAdapter(this);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater= (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout newView = (RelativeLayout) inflater.inflate(R.layout.filerow, parent,false);
        Button content = (Button) newView.findViewById(R.id.content);
        Button opt = (Button) newView.findViewById(R.id.opt);
        FileWrapper fileWrapper = new FileWrapper(activity,new File(values[position]),opt,content);
        String path[] = values[position].split("/");
        content.setText(path[path.length - 1]);
        return newView;
    }


}