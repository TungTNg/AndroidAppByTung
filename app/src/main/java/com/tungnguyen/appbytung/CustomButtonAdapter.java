package com.tungnguyen.appbytung;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CustomButtonAdapter extends ArrayAdapter<Button> {
    private boolean toastIsShowing = false;
    private Toast curToast;

    private Map<String, Class<?>> buttonNameIntentMap = new HashMap<>();

    public CustomButtonAdapter(Context context, ArrayList<Button> buttons) {
        super(context, 0, buttons);

        buttonNameIntentMap.put("Movies", MovieListActivity.class);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.button_row_layout, parent, false);
        }

        // Get the data item for this position from the input array
        Button curBtn = getItem(position);

        // Get current button in convertView
        Button btnInView = convertView.findViewById(R.id.firstBtn);

        // Set button text value
        if (curBtn.getText() != null && curBtn.getText() != "") {
            btnInView.setText(curBtn.getText());
        }

        // Set button default onClick() behavior
        btnInView.setOnClickListener(view -> {
            // Launch activities based on button's text name
            if(buttonNameIntentMap.containsKey(btnInView.getText())) {
                Intent movieListIntent = new Intent(getContext(), buttonNameIntentMap.get(btnInView.getText()));
                getContext().startActivity(movieListIntent);
            } else {
                // Make toast appear right away when being clicked
                if (toastIsShowing) {
                    curToast.cancel();
                }

                curToast = Toast.makeText(getContext(), btnInView.getText().toString(), Toast.LENGTH_LONG);
                curToast.show();
                toastIsShowing = true;
            }
        });

        return convertView;
    }
}
