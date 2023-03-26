package com.marlon.portalusuario.view.Fragments.connectivity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.marlon.portalusuario.R;
import com.marlon.portalusuario.view.Fragments.SitiosNacionalesFragment;

public class ConnectivityFragment extends Fragment {

    private TabLayout tabLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_connectivity, container, false);


        tabLayout = view.findViewById(R.id.simpleTabLayout);
        //
        TabLayout.Tab wifiEtecsaTab = tabLayout.newTab();
        wifiEtecsaTab.setText("Conectividad");
        wifiEtecsaTab.setIcon(R.drawable.round_wifi_lock_24);
        tabLayout.addTab(wifiEtecsaTab, true);
        //
//        TabLayout.Tab nautaTab = tabLayout.newTab();
//        nautaTab.setText("Portal Nauta");
//        nautaTab.setIcon(R.drawable.ic_nauta);
//        tabLayout.addTab(nautaTab);
        //
        TabLayout.Tab nationalTab = tabLayout.newTab();
        nationalTab.setText("Sitios Nacionales");
        nationalTab.setIcon(R.drawable.round_web_24);
        tabLayout.addTab(nationalTab);
        //
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment fragment = null;
                switch (tab.getPosition()){
                    case 0:
                        setFragment(new WifiEtecsaFragment());
                        break;
                    case 1:
                        setFragment(new SitiosNacionalesFragment());
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        setFragment(new WifiEtecsaFragment());
        return view;
    }

    public void setFragment(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.simpleFrameLayout, fragment)
                .commit();
    }
}
