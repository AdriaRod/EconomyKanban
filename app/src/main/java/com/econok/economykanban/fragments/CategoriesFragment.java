package com.econok.economykanban.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import com.econok.economykanban.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CategoriesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoriesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //************************+ VARIABLES ************************
    private TextView currentDateTextView;
    private Button[] monthButtons;

    public CategoriesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CategoriesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CategoriesFragment newInstance(String param1, String param2) {
        CategoriesFragment fragment = new CategoriesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_categories, container, false);
        currentDateTextView = view.findViewById(R.id.currentDate);

        // Initialize the month buttons
        monthButtons = new Button[]{
                view.findViewById(R.id.january),
                view.findViewById(R.id.february),
                view.findViewById(R.id.march),
                view.findViewById(R.id.april),
                view.findViewById(R.id.may),
                view.findViewById(R.id.june),
                view.findViewById(R.id.july),
                view.findViewById(R.id.augost),
                view.findViewById(R.id.september),
                view.findViewById(R.id.october),
                view.findViewById(R.id.november),
                view.findViewById(R.id.december)

        };



        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //*********************** CURRENT DATE *******************
        // Get the current date
        Date currentDate = new Date();
        int v_date = R.string.date_format;

        // Format the date to "MM/dd/yyyy"
        SimpleDateFormat dateFormat = new SimpleDateFormat(getString(v_date), Locale.getDefault());
        String formattedDate = dateFormat.format(currentDate);

        // Capitalize the first letter of the Month
        String capitalizedDate = formattedDate.substring(0, 1).toUpperCase() + formattedDate.substring(1);

        // Set the formatted date to the TextView
        currentDateTextView.setText(capitalizedDate);

        //****************** MONTH BUTTONS *****************************
        // Set the initial selected button
        monthButtons[0].setSelected(true);

        // Add click listeners to the buttons
        for (Button button : monthButtons) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Toggle the selected state of the button
                    button.setSelected(!button.isSelected());

                    // Clear the selected state of all other buttons
                    for (Button b : monthButtons) {
                        if (b != button) {
                            b.setSelected(false);
                        }
                    }

                    // Center the HorizontalScrollView on the selected button
                    HorizontalScrollView scrollView = view.findViewById(R.id.horizontalScrollView);
                    scrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                    int buttonWidth = button.getWidth();
                    int buttonLeft = button.getLeft();
                    int scrollViewWidth = scrollView.getWidth();
                    int scrollX = buttonLeft - (scrollViewWidth - buttonWidth) / 2;
                    scrollView.smoothScrollTo(scrollX, 0);
                }
            });
        }

    }



}