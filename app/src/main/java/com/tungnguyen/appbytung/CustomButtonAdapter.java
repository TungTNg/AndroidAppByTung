package com.tungnguyen.appbytung;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class CustomButtonAdapter extends ArrayAdapter<Button> {
    private boolean toastIsShowing = false;
    private Toast curToast;

    public CustomButtonAdapter(Context context, ArrayList<Button> buttons) {
        super(context, 0, buttons);
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
            // Make toast appear right away when being clicked
            if(toastIsShowing) {
                curToast.cancel();
            }

            curToast = Toast.makeText(getContext(), btnInView.getText().toString(), Toast.LENGTH_LONG);
            curToast.show();
            toastIsShowing = true;
        });

        return convertView;
    }
}
