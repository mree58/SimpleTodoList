package com.emrebaran.simpletodolist;

import android.app.Activity;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ListAdapterTodos extends ArrayAdapter<String>{


	private final Activity _context;
	private final String[] _todos;
	private final String[] _dates;
	private final String[] _times;
	private final Integer[] _dones;


	public ListAdapterTodos(Activity context, String[] todos, String[] dates, String[] times, Integer[] dones) {
	super(context, R.layout.custom_list_layout,todos);
		this._context = context;
		this._todos = todos;
		this._dates = dates;
		this._times = times;
		this._dones = dones;
	}
	 
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	 
	LayoutInflater inflater = _context.getLayoutInflater();
	View rowView = inflater.inflate(R.layout.custom_list_layout, null, true);
	
	TextView txtTitle = (TextView) rowView.findViewById(R.id.item_todo);
	TextView txtTitle2 = (TextView) rowView.findViewById(R.id.item_date);
	TextView txtTitle3 = (TextView) rowView.findViewById(R.id.item_time);

	txtTitle.setText(_todos[position]);
	txtTitle2.setText(_dates[position]);
	txtTitle3.setText(_times[position]);

	if(_dones[position]==1)
	{
		txtTitle.setPaintFlags(txtTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
		txtTitle2.setPaintFlags(txtTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
		txtTitle3.setPaintFlags(txtTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

	}

	
	return rowView;
	}
	 

}
