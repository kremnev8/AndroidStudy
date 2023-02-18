package ru.myitschool.lab23;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ru.myitschool.lab23.databinding.ActivityMainBinding;
import ru.myitschool.lab23.databinding.FragmentUnitBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UnitFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UnitFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_UNIT_ID = "unitId";
    private int mUnitId;
    private FragmentUnitBinding binding;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UnitFragment.
     */
    public static UnitFragment newInstance(int unitId) {
        UnitFragment fragment = new UnitFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_UNIT_ID, unitId);
        fragment.setArguments(args);
        return fragment;
    }

    public UnitFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mUnitId = getArguments().getInt(ARG_UNIT_ID);
        }
    }

    private void setClipboard(Context context, String text) {
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUnitBinding.inflate(inflater, container, false);
        String[] captions = getResources().getStringArray(R.array.text_view_captions);
        String[] tagNames = getResources().getStringArray(R.array.tag_names);

        binding.label.setText(captions[mUnitId]);
        binding.field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!binding.field.hasFocus()) return;

                String text = charSequence.toString();

                try {
                    float value = Float.parseFloat(text);

                    IUnitListener listener = (IUnitListener) getActivity();
                    if (listener != null)
                        listener.UnitChanged(mUnitId, value);
                }catch (NumberFormatException ignored){

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        binding.label.setOnClickListener(view -> {
            setClipboard(getContext(), binding.field.getText().toString());
        });
        binding.field.setTag(tagNames[mUnitId]);
        binding.field.setContentDescription(tagNames[mUnitId]);


        return binding.getRoot();
    }

    public void UpdateValue(double valueInMeters) {
        double value = MainActivity.ConvertMetersToUnit(mUnitId, valueInMeters);

        if (binding.field.hasFocus()) {
            binding.field.clearFocus();
        }

        binding.field.setText(Double.toString(value));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}