package org.octocats.sinless;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;

import com.andexert.expandablelayout.library.ExpandableLayoutListView;

import static org.octocats.sinless.R.layout.activity_timeline;

/**
 * Created by utkarsh on 21/1/17.
 */

public class Timeline extends AppCompatActivity{

    private final String[] array = {"Hello", "World", "Android", "is", "Awesome", "World", "Android", "is", "Awesome", "World", "Android", "is", "Awesome", "World", "Android", "is", "Awesome"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_timeline);

        Intent i = new Intent(Timeline.this, MainIntroActivity.class);
        startActivity(i);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.view_content, R.id.view_header, array);
        final ExpandableLayoutListView expandableLayoutListView = (ExpandableLayoutListView) findViewById(R.id.listview);

        expandableLayoutListView.setAdapter(arrayAdapter);
    }


}
