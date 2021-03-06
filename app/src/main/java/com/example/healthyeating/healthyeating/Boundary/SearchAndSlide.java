package com.example.healthyeating.healthyeating.Boundary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView;

import com.example.healthyeating.healthyeating.Entity.Location;
import com.example.healthyeating.healthyeating.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchAndSlide.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchAndSlide#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchAndSlide extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    DecimalFormat f = new DecimalFormat("##.0");
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    SearchView searchView;
    Activity activity;
    private OnFragmentInteractionListener mListener;
    Spinner spinner;

    public SearchAndSlide() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchAndSlide.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchAndSlide newInstance(String param1, String param2) {
        SearchAndSlide fragment = new SearchAndSlide();
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
        final View v = inflater.inflate(R.layout.fragment_search_and_slide, container, false);

         searchView = (SearchView) v.findViewById(R.id.searchBar);
         searchView.setQuery("", false);
         searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("SearchBar", "onQueryTextChange "+newText);
                try{
                    ((OnSearchSubmitListener)getContext() ).searchSubmit(newText);
                }catch (ClassCastException cce){

                }
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {

               Log.d("SearchBar", "OnQueryTextSubmitcalled   "+ query);
                // Do your task here
                try{
                    ((OnSearchSubmitListener)getContext() ).searchSubmit(query);
                }catch (ClassCastException cce){

                }
                return false;
            }

        });

        SeekBar sk = (SeekBar) v.findViewById(R.id.seekBar2);
        sk.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                TextView t=(TextView)v.findViewById(R.id.textView4);
                //t.setText(String.valueOf(i));
                int val = (i * (seekBar.getWidth() - 2 * seekBar.getThumbOffset())) / seekBar.getMax();
                double dis = (double)i/1000.0;
                if(dis<1.0)
                    t.setText(i+"m");
                else
                    t.setText(f.format((double)i/1000.0)+"km");
                t.setX(seekBar.getX() + val + seekBar.getThumbOffset() / 2);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        spinner = (Spinner)v.findViewById(R.id.sortSpinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity().getApplicationContext(),
                R.array.sort_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setSelection(0,false);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Toast.makeText(parent.getContext(),
                        "OnItemSelectedListener : " + "Check " + pos + "Check " + parent.getItemAtPosition(pos).toString(), Toast.LENGTH_SHORT).show();
                        // For passing sortFilter value when choose the sort condition
                        Intent i = new Intent(getActivity(), EateriesListView.class);
                        if (pos == 2) {
                            i.putExtra("key", "1");
                            startActivity(i);
                        }
                        if (pos == 1) {
                            i.putExtra("key", "0");
                            startActivity(i);
                        }

                         // Remove animation when switch activity
                         ((Activity) getActivity()).overridePendingTransition(0,0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Intent i = new Intent(getActivity(), MainActivity.class);
                startActivity(i);
            }
        });

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    public interface OnSearchSubmitListener{
        public void searchSubmit(String query);
    }

}
