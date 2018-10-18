package com.easyfitness.machines;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.easyfitness.DAO.DAOFonte;
import com.easyfitness.DAO.DAOMachine;
import com.easyfitness.DAO.Machine;
import com.easyfitness.DAO.Profile;
import com.easyfitness.MainActivity;
import com.easyfitness.R;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MachineFragment extends Fragment {
	private String name;
	private int id;

	Spinner typeList = null;
	Spinner musclesList = null;
	EditText description = null;
	ImageButton renameMachineButton = null;
	ListView machineList = null;
	Button addButton = null;

	private DAOFonte mDbFonte = null;

	private DAOMachine mDbMachine = null;
	
	/**
	 * Create a new instance of DetailsFragment, initialized to
	 * show the text at 'index'.
	 */
	public static MachineFragment newInstance(String name, int id) {
		MachineFragment f = new MachineFragment();

		// Supply index input as an argument.
		Bundle args = new Bundle();
		args.putString("name", name);
		args.putInt("id", id);
		f.setArguments(args);

		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.tab_machine, container, false);

		addButton = view.findViewById(R.id.addExercise);
		addButton.setOnClickListener(clickAddButton);

		//typeList = (Spinner) view.findViewById(R.id.filterDate);
		//machineList = (Spinner) view.findViewById(R.id.filterMachine);
        //renameMachineButton = (ImageButton) view.findViewById(R.id.imageMachineRename);
        machineList = view.findViewById(R.id.listMachine);
		//musclesList = (Spinner) view.findViewById(R.id.listFilterRecord);
		
		machineList.setOnItemClickListener(onClickListItem);

		// Initialisation de l'historique
		mDbFonte = new DAOFonte(view.getContext());
		mDbMachine = new DAOMachine(view.getContext());

		return view;
	}

	@Override
	public void onStart() {
		super.onStart();

        mDbMachine.deleteAllEmptyExercises();
        refreshData();

		// Initialisation des evenements
		machineList.setOnItemSelectedListener(onItemSelectedList);
	}

	private static String[] prepend(String[] a, String el) {
		String[] c = new String[a.length + 1];
		c[0] = el;
		System.arraycopy(a, 0, c, 1, a.length);
		return c;
	}

	public String getName() { 
		return getArguments().getString("name");
	}

	public int getFragmentId() { 
		return getArguments().getInt("id", 0);
	}
	
	private OnItemClickListener onClickListItem = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			// Get Machine Name selected
			TextView textViewID = view.findViewById(R.id.LIST_MACHINE_ID);
			long machineId = Long.valueOf(textViewID.getText().toString());

			MachineDetailsFragment machineDetailsFragment = MachineDetailsFragment.newInstance(machineId, ((MainActivity)getActivity()).getCurrentProfil().getId());
			FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
			// Replace whatever is in the fragment_container view with this fragment,
			// and add the transaction to the back stack so the user can navigate back
			transaction.replace(R.id.fragment_container, machineDetailsFragment, MainActivity.MACHINESDETAILS);
            transaction.addToBackStack(null);
			// Commit the transaction
			transaction.commit();
		}
	};


	private View.OnClickListener clickAddButton = new View.OnClickListener() {
		@Override
		public void onClick(View v) {

            // create a temporarily exercise with name="" and open it like any other existing exercises
            long new_id = -1;


            SweetAlertDialog dlg = new SweetAlertDialog(getContext(), SweetAlertDialog.NORMAL_TYPE)
                    .setTitleText("What type of exercise ?")
                    .setContentText("")
                    .setCancelText(getResources().getText(R.string.CardioLabel).toString())
                    .setConfirmText(getResources().getText(R.string.FonteLabel).toString())
                    .showCancelButton(true)
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            long temp_machine_key = -1;
                            String pMachine = "";
                            DAOMachine lDAOMachine = new DAOMachine(getContext());
                            temp_machine_key = lDAOMachine.addMachine(pMachine, "", DAOMachine.TYPE_FONTE, "");
                            sDialog.dismissWithAnimation();

                            MachineDetailsFragment machineDetailsFragment = MachineDetailsFragment.newInstance(temp_machine_key, ((MainActivity) getActivity()).getCurrentProfil().getId());
                            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                            // Replace whatever is in the fragment_container view with this fragment,
                            // and add the transaction to the back stack so the user can navigate back
                            transaction.replace(R.id.fragment_container, machineDetailsFragment, MainActivity.MACHINESDETAILS);
                            transaction.addToBackStack(null);
                            // Commit the transaction
                            transaction.commit();
                        }
                    })
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            long temp_machine_key = -1;
                            String pMachine = "";
                            DAOMachine lDAOMachine = new DAOMachine(getContext());
                            temp_machine_key = lDAOMachine.addMachine(pMachine, "", DAOMachine.TYPE_CARDIO, "");
                            sDialog.dismissWithAnimation();

                            MachineDetailsFragment machineDetailsFragment = MachineDetailsFragment.newInstance(temp_machine_key, ((MainActivity) getActivity()).getCurrentProfil().getId());
                            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                            // Replace whatever is in the fragment_container view with this fragment,
                            // and add the transaction to the back stack so the user can navigate back
                            transaction.replace(R.id.fragment_container, machineDetailsFragment, MainActivity.MACHINESDETAILS);
                            transaction.addToBackStack(null);
                            // Commit the transaction
                            transaction.commit();
                        }
                    });

            dlg.show();

            dlg.getButton(SweetAlertDialog.BUTTON_CONFIRM).setBackgroundResource(R.color.background_odd);
            dlg.getButton(SweetAlertDialog.BUTTON_CONFIRM).setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
            dlg.getButton(SweetAlertDialog.BUTTON_CANCEL).setBackgroundResource(R.color.background_odd);
            dlg.getButton(SweetAlertDialog.BUTTON_CANCEL).setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);

		}
	};


	private OnItemSelectedListener onItemSelectedList = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			//refreshData();
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}
	};


	public DAOFonte getDB() {
		return mDbFonte;
	}
	
	public MachineFragment getThis() {
		return this;
	}

	private void refreshData(){
        Cursor c = null;
		Cursor oldCursor = null;
        ArrayList<Machine> records = null;
		
		View fragmentView = getView();
		if(fragmentView != null) {
			if (getProfil() != null) {
			
				// Version avec table Machine
                c = mDbMachine.getAllMachines();
                if (c == null || c.getCount() == 0) {
					//Toast.makeText(getActivity(), "No records", Toast.LENGTH_SHORT).show();    
					machineList.setAdapter(null);
				} else {
					if ( machineList.getAdapter() == null ) {
                        MachineCursorAdapter mTableAdapter = new MachineCursorAdapter(this.getView().getContext(), c, 0, mDbMachine);
						machineList.setAdapter(mTableAdapter);
					} else {				
						MachineCursorAdapter mTableAdapter = ((MachineCursorAdapter)machineList.getAdapter());
                        oldCursor = mTableAdapter.swapCursor(c);
						if (oldCursor!=null) oldCursor.close();
					}
				}
			}
		}
	}

	@Override
	public void onHiddenChanged (boolean hidden) {
		if (!hidden) refreshData();
	}

	private Profile getProfil()	{
		return ((MainActivity)getActivity()).getCurrentProfil();
	}

}
