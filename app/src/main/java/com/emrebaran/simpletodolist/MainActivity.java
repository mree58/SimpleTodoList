package com.emrebaran.simpletodolist;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.wdullaer.swipeactionadapter.SwipeActionAdapter;
import com.wdullaer.swipeactionadapter.SwipeDirection;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import static com.wdullaer.swipeactionadapter.SwipeDirection.DIRECTION_NORMAL_RIGHT;

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

    //initialize date and time
    int year = 2016,month = 11,day = 5,hour = 13,minute= 30;

    int nowHour,nowMin,nowDay,nowMonth,nowYear;

    String updateDate;

    private boolean doubleBackToExitPressedOnce;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = (ListView)findViewById(R.id.list);

        load();


    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

            final Dialog infoDialog = new Dialog(MainActivity.this);
            infoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            infoDialog.setContentView(R.layout.layout_dialog_buttons);


            infoDialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);



            ImageButton editPerson = (ImageButton) infoDialog.findViewById(R.id.popup_btn_edit);
            editPerson.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {



                    showPopupEdit(MainActivity.this,array_ids[position],array_todos[position],array_dates[position],array_times[position]);

                    infoDialog.dismiss();


                }
            });


            ImageButton cancelAlarm = (ImageButton) infoDialog.findViewById(R.id.popup_btn_cancel_alarm);
            cancelAlarm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(array_dates[position].length()>5)
                        cancelExactAlarm(array_ids[position]);
                    else
                        Toast.makeText(getApplicationContext(), getString(R.string.alarm_not_found), Toast.LENGTH_SHORT).show();


                    infoDialog.dismiss();

                }
            });

            ImageButton setAlarm = (ImageButton) infoDialog.findViewById(R.id.popup_btn_set_alarm);
            setAlarm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    final Calendar c = Calendar.getInstance();

                    nowHour = c.get(Calendar.HOUR_OF_DAY);
                    nowMin = c.get(Calendar.MINUTE);
                    nowDay = c.get(Calendar.DAY_OF_MONTH);
                    nowMonth = c.get(Calendar.MONTH);
                    nowYear = c.get(Calendar.YEAR);


                    updateDate = String.valueOf(nowDay)+"/"+String.valueOf(nowMonth+1)+"/"+String.valueOf(nowYear);

                    DatePickerDialog dpd = new DatePickerDialog(MainActivity.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int _year, int _month, int _day) {

                                    c.set(_year, _month, _day);
                                    String date = new SimpleDateFormat("dd/MM/yyyy").format(c.getTime());

                                    year = c.get(Calendar.YEAR);
                                    month = c.get(Calendar.MONTH);
                                    day = c.get(Calendar.DAY_OF_MONTH);


                                    nowDay = c.get(Calendar.DAY_OF_MONTH);
                                    nowMonth = c.get(Calendar.MONTH);
                                    nowYear = c.get(Calendar.YEAR);

                                    if (_day<10 && _month<10)
                                        updateDate = "0"+_day + "/" + "0"+String.valueOf(_month+1) + "/" + _year;
                                    else if (_day<10 && _month>10)
                                        updateDate = "0"+_day + "/" + String.valueOf(_month+1) + "/" + _year;
                                    else if (_day>10 && _month<10)
                                        updateDate = _day + "/" + "0" + String.valueOf(_month+1) + "/" + _year;
                                    else if (_day>10 && _month>10)
                                        updateDate = _day + "/" + String.valueOf(_month+1) + "/" + _year;

                                }
                            }, nowYear, nowMonth, nowDay);

                    Calendar d = Calendar.getInstance();
                    d.add(Calendar.MONTH, 1);



                    TimePickerDialog tpd = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int _hour, int _minute) {

                            hour = _hour;
                            minute = _minute;

                            nowHour = _hour;
                            nowMin = _minute;


                            String updateTime="";

                            if (_hour<10 && _minute<10)
                                updateTime = "0"+_hour + ":" + "0"+_minute;
                            else if (_hour<10 && _minute>10)
                                updateTime = "0"+_hour + ":" + _minute;
                            else if (_hour>10 && _minute<10)
                                updateTime = _hour + ":" + "0"+_minute;
                            else if (_hour>10 && _minute>10)
                                updateTime = _hour + ":" +_minute;


                            long updated_id = db.updateDate(new TodoClass(updateDate, updateTime),array_ids[position]);

                            if(updated_id!=0)
                            {
                                load();
                                setAlarm(array_dates[position],array_times[position],array_ids[position]);
                                infoDialog.dismiss();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),getResources().getString(R.string.edit_update_warning),Toast.LENGTH_SHORT).show();
                            }

                            infoDialog.dismiss();

                        }
                    }, nowHour, nowMin, true);


                    tpd.show();


                    dpd.show();


                }
            });


            infoDialog.show();


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
                float popupHeight = 280*metrics.scaledDensity;

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
                final TextView txtDate= (TextView) layout.findViewById(R.id.popup_txt_date);
                final TextView txtTime= (TextView) layout.findViewById(R.id.popup_txt_time);
                final Switch swAlarm  = (Switch) layout.findViewById(R.id.popup_switch_alarm);

                txtDate.setEnabled(false);
                txtTime.setEnabled(false);


                swAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        if(isChecked){

                            txtDate.setEnabled(true);
                            txtDate.setTextColor(getResources().getColor(R.color.black));

                            txtTime.setEnabled(true);
                            txtTime.setTextColor(getResources().getColor(R.color.black));

                        }else{

                            txtDate.setEnabled(false);
                            txtDate.setTextColor(getResources().getColor(R.color.gray));

                            txtTime.setEnabled(false);
                            txtTime.setTextColor(getResources().getColor(R.color.gray));

                        }

                    }
                });


                final Calendar c = Calendar.getInstance();

                nowHour = c.get(Calendar.HOUR_OF_DAY);
                nowMin = c.get(Calendar.MINUTE);
                nowDay = c.get(Calendar.DAY_OF_MONTH);
                nowMonth = c.get(Calendar.MONTH);
                nowYear = c.get(Calendar.YEAR);

                String date = new SimpleDateFormat("dd/MM/yyyy").format(c.getTime());
                txtDate.setText(date);
                txtTime.setText( nowHour + ":" + nowMin);

                txtDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        DatePickerDialog dpd = new DatePickerDialog(MainActivity.this,
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int _year, int _month, int _day) {

                                        c.set(_year, _month, _day);
                                        String date = new SimpleDateFormat("dd/MM/yyyy").format(c.getTime());
                                        txtDate.setText(date);


                                        year = c.get(Calendar.YEAR);
                                        month = c.get(Calendar.MONTH);
                                        day = c.get(Calendar.DAY_OF_MONTH);


                                        nowDay = c.get(Calendar.DAY_OF_MONTH);
                                        nowMonth = c.get(Calendar.MONTH);
                                        nowYear = c.get(Calendar.YEAR);

                                    }
                                }, nowYear, nowMonth, nowDay);

                        Calendar d = Calendar.getInstance();
                        d.add(Calendar.MONTH, 1);

                        dpd.show();

                    }
                });


                txtTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (swAlarm.isChecked()) {

                            TimePickerDialog tpd = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker timePicker, int _hour, int _minute) {

                                    if (_hour<10 && _minute<10)
                                        txtTime.setText( "0"+_hour + ":" + "0"+_minute);
                                    else if (_hour<10 && _minute>10)
                                        txtTime.setText( "0"+_hour + ":" + _minute);
                                    else if (_hour>10 && _minute<10)
                                        txtTime.setText(+_hour + ":" + "0"+_minute);
                                    else if (_hour>10 && _minute>10)
                                        txtTime.setText( +_hour + ":" +_minute);

                                    hour = _hour;
                                    minute = _minute;

                                    nowHour = _hour;
                                    nowMin = _minute;

                                }
                            }, nowHour, nowMin, true);


                            tpd.show();
                        }

                    }
                });


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

                        if (swAlarm.isChecked())
                        {
                            inserted_id = db.addTodo(new TodoClass(edtTodo.getText().toString(), txtDate.getText().toString(), txtTime.getText().toString(), 0));

                            setAlarm(txtDate.getText().toString(),txtTime.getText().toString(),inserted_id);

                        }
                        else
                        {
                            inserted_id = db.addTodo(new TodoClass(edtTodo.getText().toString(), "", "", 0));

                        }

                        load();

                        pw.dismiss();
                        }
                });

        } catch (Exception e) {
            e.printStackTrace();
        }
}


    private PopupWindow pwEdit;
    private void showPopupEdit(final Activity context, final int _id, String _todo, String _date, String _time) {
        try {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View layout = inflater.inflate(R.layout.layout_popup_add_new, (ViewGroup) findViewById(R.id.popup));

            float popupWidth = 350*metrics.scaledDensity;
            float popupHeight = 280*metrics.scaledDensity;

            pwEdit = new PopupWindow(context);
            pwEdit.setContentView(layout);
            pwEdit.setWidth((int)popupWidth);
            pwEdit.setHeight((int)popupHeight);
            pwEdit.setFocusable(true);

            Point p = new Point();
            p.x = 50;
            p.y = 50;

            int OFFSET_X = -50;
            int OFFSET_Y = (int)(90*metrics.scaledDensity);


            pwEdit.showAtLocation(layout, Gravity.TOP, p.x + OFFSET_X, p.y + OFFSET_Y);


            final EditText edtTodo= (EditText) layout.findViewById(R.id.popup_edt_todo);
            final TextView txtDate= (TextView) layout.findViewById(R.id.popup_txt_date);
            final TextView txtTime= (TextView) layout.findViewById(R.id.popup_txt_time);
            final Switch swAlarm  = (Switch) layout.findViewById(R.id.popup_switch_alarm);

            txtDate.setEnabled(false);
            txtTime.setEnabled(false);


            edtTodo.setText(_todo);

            if(txtDate.length()>5) {
                swAlarm.setChecked(true);

                txtDate.setText(_date);
                txtTime.setText(_time);

                txtDate.setEnabled(true);
                txtTime.setEnabled(true);

                txtDate.setTextColor(getResources().getColor(R.color.black));
                txtTime.setTextColor(getResources().getColor(R.color.black));

            }


            swAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if(isChecked){

                        txtDate.setEnabled(true);
                        txtDate.setTextColor(getResources().getColor(R.color.black));

                        txtTime.setEnabled(true);
                        txtTime.setTextColor(getResources().getColor(R.color.black));

                    }else{

                        txtDate.setEnabled(false);
                        txtDate.setTextColor(getResources().getColor(R.color.gray));

                        txtTime.setEnabled(false);
                        txtTime.setTextColor(getResources().getColor(R.color.gray));

                    }

                }
            });


            final Calendar c = Calendar.getInstance();

            nowHour = c.get(Calendar.HOUR_OF_DAY);
            nowMin = c.get(Calendar.MINUTE);
            nowDay = c.get(Calendar.DAY_OF_MONTH);
            nowMonth = c.get(Calendar.MONTH);
            nowYear = c.get(Calendar.YEAR);

          //  String date = new SimpleDateFormat("dd/MM/yyyy").format(c.getTime());
          //  txtDate.setText(date);
          // txtTime.setText( nowHour + ":" + nowMin);

            txtDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DatePickerDialog dpd = new DatePickerDialog(MainActivity.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int _year, int _month, int _day) {

                                    c.set(_year, _month, _day);
                                    String date = new SimpleDateFormat("dd/MM/yyyy").format(c.getTime());
                                    txtDate.setText(date);


                                    year = c.get(Calendar.YEAR);
                                    month = c.get(Calendar.MONTH);
                                    day = c.get(Calendar.DAY_OF_MONTH);


                                    nowDay = c.get(Calendar.DAY_OF_MONTH);
                                    nowMonth = c.get(Calendar.MONTH);
                                    nowYear = c.get(Calendar.YEAR);

                                }
                            }, nowYear, nowMonth, nowDay);

                    Calendar d = Calendar.getInstance();
                    d.add(Calendar.MONTH, 1);

                    dpd.show();

                }
            });


            txtTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (swAlarm.isChecked()) {

                        TimePickerDialog tpd = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int _hour, int _minute) {

                                if (_hour<10 && _minute<10)
                                    txtTime.setText( "0"+_hour + ":" + "0"+_minute);
                                else if (_hour<10 && _minute>10)
                                    txtTime.setText( "0"+_hour + ":" + _minute);
                                else if (_hour>10 && _minute<10)
                                    txtTime.setText(+_hour + ":" + "0"+_minute);
                                else if (_hour>10 && _minute>10)
                                    txtTime.setText( +_hour + ":" +_minute);

                                hour = _hour;
                                minute = _minute;

                                nowHour = _hour;
                                nowMin = _minute;

                            }
                        }, nowHour, nowMin, true);


                        tpd.show();
                    }

                }
            });


            ImageButton close= (ImageButton) layout.findViewById(R.id.popup_btn_close);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pwEdit.dismiss();

                }
            });

            ImageButton save= (ImageButton) layout.findViewById(R.id.popup_btn_save);
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    long updated_id;

                    if (swAlarm.isChecked())
                    {

                        updated_id = db.updateTodo(new TodoClass(edtTodo.getText().toString(), txtDate.getText().toString(), txtTime.getText().toString(), 0),_id);

                        if(updated_id!=0)
                        {
                            setAlarm(txtDate.getText().toString(),txtTime.getText().toString(),updated_id);
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),getResources().getString(R.string.edit_update_warning),Toast.LENGTH_SHORT).show();
                        }


                    }
                    else
                    {
                        updated_id = db.updateTodo(new TodoClass(edtTodo.getText().toString(), "", "", 0),_id);

                    }

                    load();

                    pwEdit.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private PopupWindow pwAbout;
    private void showPopupAbout(final Activity context) {
        try {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.layout_about, (ViewGroup) findViewById(R.id.popup_1));

            float popupWidth = 330*metrics.scaledDensity;
            float popupHeight = 460*metrics.scaledDensity;

            pwAbout = new PopupWindow(context);
            pwAbout.setContentView(layout);
            pwAbout.setWidth((int)popupWidth);
            pwAbout.setHeight((int)popupHeight);
            pwAbout.setFocusable(true);

            Point p = new Point();
            p.x = 50;
            p.y = 50;

            int OFFSET_X = -50;
            int OFFSET_Y = (int)(80*metrics.scaledDensity);


            pwAbout.showAtLocation(layout, Gravity.TOP, p.x + OFFSET_X, p.y + OFFSET_Y);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean setAlarm(String my_date, String my_time, long alarm_id){
        String[] dates = my_date.split("/");
        int my_day = Integer.parseInt(dates[0]);
        int my_month = Integer.parseInt(dates[1])-1;
        int my_year = Integer.parseInt(dates[2]);


        String[] hours = my_time.toString().split(":");
        int my_hour = Integer.parseInt(hours[0]);
        int my_min = Integer.parseInt(hours[1]);

        Calendar current = Calendar.getInstance();

        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.DAY_OF_MONTH, my_day);
        cal.set(Calendar.MONTH, my_month);
        cal.set(Calendar.YEAR,my_year);
        cal.set(Calendar.HOUR_OF_DAY, my_hour);
        cal.set(Calendar.MINUTE, my_min);
        cal.set(Calendar.SECOND, 00);
        cal.set(Calendar.MILLISECOND, 00);


        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MM yyyy hh:mm:ss:ss ");

        if(cal.compareTo(current) <= 0){

            Toast.makeText(getApplicationContext(), getString(R.string.alarm_wrong_date), Toast.LENGTH_SHORT).show();

            return false;

        }else{
            setExactAlarm(cal,alarm_id);
            return true;
        }
    }



    private void setExactAlarm(Calendar targetCal, long alarm_id){

        Toast.makeText(getApplicationContext(),getString(R.string.alarm_set),Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), (int)alarm_id, intent, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);

    }


    public void cancelExactAlarm(int alarm_id){
        Context context = this.getApplicationContext();
        AlarmReceiver alarm = new AlarmReceiver();

        if(alarm != null){
            alarm.CancelAlarm(context,alarm_id);
            Toast.makeText(context, getString(R.string.alarm_cancelled), Toast.LENGTH_SHORT).show();

        }else{
            Toast.makeText(context, getString(R.string.alarm_error), Toast.LENGTH_SHORT).show();
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
                    .addBackground(DIRECTION_NORMAL_RIGHT,R.layout.row_bg_right_far);

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
                    .addBackground(DIRECTION_NORMAL_RIGHT,R.layout.row_bg_right_far);

        }
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

            switch (direction) {
                case DIRECTION_FAR_LEFT:

                    AlertDialog.Builder deleteDialog = new AlertDialog.Builder(this);
                    deleteDialog.setMessage(getString(R.string.delete_question));
                    deleteDialog.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int which){
                            dialog.dismiss();

                            db.deleteTodo(array_ids[position]);

                            load();

                        }
                    });
                    deleteDialog.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int which){
                            dialog.cancel();
                        }
                    });
                    deleteDialog.show();


                    break;
                case DIRECTION_NORMAL_LEFT:
                    break;
                case DIRECTION_FAR_RIGHT:
                    if(array_dones[position]==0) {
                        db.updateDone(new TodoClass(1), array_ids[position]);

                        if(array_dates[position].length()>5)
                            cancelExactAlarm(array_ids[position]);
                    }
                    else
                        db.updateDone(new TodoClass(0),array_ids[position]);

                    load();


                    break;
                case DIRECTION_NORMAL_RIGHT:
                    break;
            }

            mAdapter.notifyDataSetChanged();
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_help) {

            Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.action_help_message), Toast.LENGTH_LONG);
            TextView vv = (TextView) toast.getView().findViewById(android.R.id.message);
            if( vv != null) vv.setGravity(Gravity.CENTER);
            toast.show();

            return true;
        }

        if (id == R.id.action_about) {
            showPopupAbout(MainActivity.this);

            return true;
        }
        if (id == R.id.action_rate) {

            Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            // To count with Play market backstack, After pressing back button,
            // to taken back to our application, we need to add following flags to intent.
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName())));
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    //double click to exit
    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            doubleBackToExitPressedOnce = false;
        }
    };
    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if (mHandler != null) { mHandler.removeCallbacks(mRunnable); }
    }
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.app_exit, Toast.LENGTH_SHORT).show();

        mHandler.postDelayed(mRunnable, 1500);
    }
}