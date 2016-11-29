package com.emrebaran.simpletodolist;

/**
 * Created by mree on 12.11.2016.
 */

public class TodoClass {

    //private variables
    int _id;
    String _todo;
    String _date;
    String _time;
    int _done;

    // Empty constructor
    public TodoClass(){

    }
    // constructor
    public TodoClass(int id, String todo, String date, String time){
        this._id = id;
        this._todo = todo;
        this._date = date;
        this._time = time;

    }

    public TodoClass(String todo, String date, String time, int done){
        this._todo = todo;
        this._date = date;
        this._time = time;
        this._done = done;
    }


    public TodoClass(int done){
        this._done = done;
    }


    public int getID(){
        return this._id;
    }
    public void setID(int id){
        this._id = id;
    }

    public String getTodo(){
        return this._todo;
    }
    public void setTodo(String todo){
        this._todo = todo;
    }

    public String getDate(){
        return this._date;
    }
    public void setDate(String date){
        this._date = date;
    }

    public String getTime(){
        return this._time;
    }
    public void setTime(String time){
        this._time = time;
    }

    public int getDone(){
        return this._done;
    }
    public void setDone(int done){
        this._done = done;
    }
}