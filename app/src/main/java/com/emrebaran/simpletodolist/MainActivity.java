package com.emrebaran.simpletodolist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.wdullaer.swipeactionadapter.SwipeActionAdapter;
import com.wdullaer.swipeactionadapter.SwipeDirection;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SwipeActionAdapter.SwipeActionListener {

    protected SwipeActionAdapter mAdapter;

    DisplayMetrics metrics;

    ListView lv;

    TodoDB db= new TodoDB(this);

    Integer[] array_ids;
    String[] array_todos;
    String[] array_dates;
    String[] array_times;
    Integer[] array_dones;

    String[] array_empty = {};

    Integer[] array_empty_int = {};

    ListAdapterTodos adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = (ListView)findViewById(R.id.list);

        load();


    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(
                    getApplicationContext(),
                    "Clicked "+mAdapter.getItem(position),
                    Toast.LENGTH_SHORT
            ).show();


            applyStrike(position);

        }
    });


        FloatingActionButton floatingAddNeww = (FloatingActionButton)findViewById(R.id.floatingAddNew);
        floatingAddNeww.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    showPopup(MainActivity.this);
            }
        });



        //for popup depending on resolution
        Display display = ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        
    }


        private PopupWindow pw;
        private void showPopup(final Activity context) {
            try {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View layout = inflater.inflate(R.layout.layout_popup_add_new, (ViewGroup) findViewById(R.id.popup));

                float popupWidth = 350*metrics.scaledDensity;
                float popupHeight = 200*metrics.scaledDensity;

                pw = new PopupWindow(context);
                pw.setContentView(layout);
                pw.setWidth((int)popupWidth);
                pw.setHeight((int)popupHeight);
                pw.setFocusable(true);

                Point p = new Point();
                p.x = 50;
                p.y = 50;

                int OFFSET_X = -50;
                int OFFSET_Y = (int)(90*metrics.scaledDensity);


                pw.showAtLocation(layout, Gravity.TOP, p.x + OFFSET_X, p.y + OFFSET_Y);


                final EditText edtTodo= (EditText) layout.findViewById(R.id.popup_edt_todo);
                final EditText edtDate= (EditText) layout.findViewById(R.id.popup_edt_date);
                final EditText edtTime= (EditText) layout.findViewById(R.id.popup_edt_time);

                ImageButton close= (ImageButton) layout.findViewById(R.id.popup_btn_close);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pw.dismiss();

                    }
                });

                ImageButton save= (ImageButton) layout.findViewById(R.id.popup_btn_save);
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                            long inserted_id;

                            inserted_id = db.addTodo(new TodoClass(edtTodo.getText().toString() , edtDate.getText().toString(), edtTime.getText().toString(),0));

                            load();

                            pw.dismiss();
                        }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
}



    private void load() {


        int j = db.getRowCount();

        if(j>0) {

            List<TodoClass> todos = db.getAllTodos();

            array_ids = new Integer[j];
            array_todos = new String[j];
            array_dates = new String[j];
            array_times = new String[j];
            array_dones = new Integer[j];
            int i = -1;
            for (TodoClass td : todos) {
                i++;

                array_ids[i] = td.getID();
                array_todos[i] = td.getTodo();
                array_dates[i] = td.getDate();
                array_times[i] = td.getTime();
                array_dones[i] = td.getDone();

                Log.d("dones",String.valueOf(array_dones[i]));

                Log.d("todos",array_todos[i]);


            }

            adapter = new ListAdapterTodos(MainActivity.this, array_todos, array_dates, array_times,array_dones);

            mAdapter = new SwipeActionAdapter(adapter);
            mAdapter.setSwipeActionListener(this)
                    .setDimBackgrounds(false)
                    .setListView(lv);


            int index = lv.getFirstVisiblePosition();
            View v = lv.getChildAt(0);
            int top = (v == null) ? 0 : (v.getTop() - lv.getPaddingTop());

            lv.setAdapter(mAdapter);

            lv.setSelectionFromTop(index, top);



            mAdapter.addBackground(SwipeDirection.DIRECTION_FAR_LEFT,R.layout.row_bg_left_far)
                    .addBackground(SwipeDirection.DIRECTION_NORMAL_LEFT, R.layout.row_bg_left_far)
                    .addBackground(SwipeDirection.DIRECTION_FAR_RIGHT, R.layout.row_bg_right_far)
                    .addBackground(SwipeDirection.DIRECTION_NORMAL_RIGHT,R.layout.row_bg_right_far);

        }
        else
        {

            adapter = new ListAdapterTodos(MainActivity.this, array_empty, array_empty, array_empty, array_empty_int);

            mAdapter = new SwipeActionAdapter(adapter);
            mAdapter.setSwipeActionListener(this)
                    .setDimBackgrounds(false)
                    .setListView(lv);

            lv.setAdapter(mAdapter);




            mAdapter.addBackground(SwipeDirection.DIRECTION_FAR_LEFT,R.layout.row_bg_left_far)
                    .addBackground(SwipeDirection.DIRECTION_NORMAL_LEFT, R.layout.row_bg_left_far)
                    .addBackground(SwipeDirection.DIRECTION_FAR_RIGHT, R.layout.row_bg_right_far)
                    .addBackground(SwipeDirection.DIRECTION_NORMAL_RIGHT,R.layout.row_bg_right_far);

        }
    }



        void applyStrike(int position){

            View view = lv.getChildAt(position);
            if (view != null) {

                Log.d("view not null","view not null");

                TextView txt = (TextView) view.findViewById(R.id.item_todo);
                if (txt != null) {

                    Log.d("txt not null","txt not null");

                    if ((txt.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) == 0)
                        txt.setPaintFlags(txt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    else
                        txt.setPaintFlags(txt.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));

                    txt.setTextColor(getResources().getColor(R.color.black));

                    load();
                }
            }
            else
                Log.d("view null","view null");



        }


    @Override
    public boolean hasActions(int position, SwipeDirection direction){
        if(direction.isLeft()) return true;
        if(direction.isRight()) return true;
        return false;
    }

    @Override
    public boolean shouldDismiss(int position, SwipeDirection direction){
        return false;

        //return direction == SwipeDirection.DIRECTION_NORMAL_LEFT
    }

    @Override
    public void onSwipe(int[] positionList, SwipeDirection[] directionList){
        for(int i=0;i<positionList.length;i++) {
            SwipeDirection direction = directionList[i];
            final int position = positionList[i];
            String dir = "";

            switch (direction) {
                case DIRECTION_FAR_LEFT:
                    dir = "Far left";

                    AlertDialog.Builder deleteDialog = new AlertDialog.Builder(this);
                    deleteDialog.setMessage("Are you sure you want to delete ?");
                    deleteDialog.setPositiveButton("YES", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int which){
                            dialog.dismiss();

                            db.deleteTodo(array_ids[position]);

                            load();

                        }
                    });
                    deleteDialog.setNegativeButton("NO", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int which){
                            dialog.cancel();
                        }
                    });
                    deleteDialog.show();


                    break;
                case DIRECTION_NORMAL_LEFT:
                    dir = "Left";
                    break;
                case DIRECTION_FAR_RIGHT:
                    dir = "Far right";

                    if(array_dones[position]==0)
                        db.updateDone(new TodoClass(1),array_ids[position]);
                    else
                        db.updateDone(new TodoClass(0),array_ids[position]);

                    load();


                    break;
                case DIRECTION_NORMAL_RIGHT:
                   // AlertDialog.Builder builder = new AlertDialog.Builder(this);
                   // builder.setTitle("Test Dialog").setMessage("You swiped right").create().show();
                    dir = "Right";
                    break;
            }
            Toast.makeText(
                    this,
                    dir + " swipe Action triggered on " + mAdapter.getItem(position),
                    Toast.LENGTH_SHORT
            ).show();
            mAdapter.notifyDataSetChanged();
        }
    }
}