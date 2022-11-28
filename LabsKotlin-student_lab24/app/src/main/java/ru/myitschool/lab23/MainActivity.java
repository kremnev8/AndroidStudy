package ru.myitschool.lab23;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import ru.myitschool.lab23.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements IUnitListener {

    private ActivityMainBinding binding;
    private boolean inited;
    private int lower;
    private int upper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        if (!inited) {
            Intent intent = getIntent();
            lower = 0;
            upper = 14;
            if (intent.getExtras() != null) {
                lower = intent.getExtras().get("lower") != null ? intent.getExtras().getInt("lower") : 0;
                upper = intent.getExtras().get("upper") != null ? intent.getExtras().getInt("upper") : 14;
            }

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setReorderingAllowed(true);
            for (int i = lower; i <= upper; i++) {
                transaction.add(R.id.outer_layout, UnitFragment.newInstance(i), "unitFrag_" + i);
            }
            transaction.commit();
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        inited = savedInstanceState.getBoolean("inited");
        lower = savedInstanceState.getInt("lower");
        upper = savedInstanceState.getInt("upper");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("inited", inited);
        outState.putInt("lower", lower);
        outState.putInt("upper", upper);
    }

    @Override
    public void UnitChanged(int unitId, double value) {
        double valueInMeters = ConvertUnitToMeters(unitId, value);
        for (int i = lower; i <= upper; i++) {
            if (i == unitId) continue;

            UnitFragment fragment = (UnitFragment) getSupportFragmentManager().findFragmentByTag("unitFrag_" + i);
            if (fragment != null)
                fragment.UpdateValue(valueInMeters);
        }
    }

    public static double ConvertUnitToMeters(int unitId, double value) {
        switch (unitId) {
            case 0:
                return value * 0.0254;
            case 1:
                return value * 0.9144;
            case 2:
                return value * 0.3048;
            case 3:
                return value * 1609.344;
            default:
                int power = 14 - unitId;
                return value * Math.pow(10, power);
        }
    }

    public static double ConvertMetersToUnit(int unitId, double valueMeters){
        switch (unitId) {
            case 0:
                return valueMeters / 0.0254;
            case 1:
                return valueMeters / 0.9144;
            case 2:
                return valueMeters / 0.3048;
            case 3:
                return valueMeters / 1609.344;
            default:
                int power = unitId - 14;
                return valueMeters * Math.pow(10, power);
        }
    }
}
