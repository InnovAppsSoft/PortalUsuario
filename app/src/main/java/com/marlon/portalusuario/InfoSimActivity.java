package com.marlon.portalusuario;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.marlon.cz.mroczis.netmonster.core.factory.NetMonsterFactory;
import com.marlon.cz.mroczis.netmonster.core.model.cell.CellCdma;
import com.marlon.cz.mroczis.netmonster.core.model.cell.CellGsm;
import com.marlon.cz.mroczis.netmonster.core.model.cell.CellLte;
import com.marlon.cz.mroczis.netmonster.core.model.cell.CellNr;
import com.marlon.cz.mroczis.netmonster.core.model.cell.CellTdscdma;
import com.marlon.cz.mroczis.netmonster.core.model.cell.CellWcdma;
import com.marlon.cz.mroczis.netmonster.core.model.cell.ICell;
import com.marlon.cz.mroczis.netmonster.core.model.cell.ICellProcessor;
import com.marlon.portalusuario.util.Connectivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.RejectedExecutionException;

import kotlin.Unit;
import mobi.gspd.segmentedbarview.Segment;
import mobi.gspd.segmentedbarview.SegmentedBarView;
import mobi.gspd.segmentedbarview.SegmentedBarViewSideStyle;
import soup.neumorphism.NeumorphImageView;

public class InfoSimActivity extends AppCompatActivity {

    private final ServiceState myServiceState = new ServiceState();
    private PhoneStateListener listener;
    PhoneStateListener listener2 = null;
    private TextView networkClass;
    private TelephonyManager telephonyManager;
    private TextView tvSignal;
    private Utils Utils;
    private SegmentedBarView bv;
    private NeumorphImageView info;
    private Connectivity Conn;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_info_sim);
        this.bv = (SegmentedBarView) findViewById(R.id.bar_view);
        this.tvSignal = (TextView) findViewById(R.id.tvSignal);
        this.networkClass = (TextView) findViewById(R.id.tvTipoRedM);
        this.Utils = new Utils(getApplicationContext());
        this.context = getApplicationContext();
        this.Conn = new Connectivity();
        this.telephonyManager = (TelephonyManager) InfoSimActivity.this.getSystemService(Context.TELEPHONY_SERVICE);
        inicializarSegmentoFuerzaSenal();
        obtenerTipoSenal();
        reinicializarSegmentoSenal();

    }

    private void reinicializarSegmentoSenal() {
        ArrayList arrayList = new ArrayList();
        if (this.networkClass.getText().toString().contains("4G")) {
            arrayList.add(new Segment(-100.0f, -95.0f, "Mala", Color.parseColor("#ffd50000")));
            arrayList.add(new Segment(-90.0f, -85.0f, "", Color.parseColor("#ffd50000")));
            arrayList.add(new Segment(-80.0f, -75.0f, "Buena", Color.parseColor("#ffffd600")));
            arrayList.add(new Segment(-70.0f, -65.0f, "", Color.parseColor("#8CC63E")));
            arrayList.add(new Segment(-60.0f, -0.0f, "Perfecta", Color.parseColor("#8CC63E")));
        } else if (this.networkClass.getText().toString().contains("3G")) {
            arrayList.add(new Segment(-100.0f, -95.0f, "Mala", Color.parseColor("#ffd50000")));
            arrayList.add(new Segment(-90.0f, -85.0f, "", Color.parseColor("#ffd50000")));
            arrayList.add(new Segment(-80.0f, -75.0f, "Buena", Color.parseColor("#ffffd600")));
            arrayList.add(new Segment(-70.0f, -65.0f, "", Color.parseColor("#8CC63E")));
            arrayList.add(new Segment(-60.0f, -0.0f, "Perfecta", Color.parseColor("#8CC63E")));
        } else {
            arrayList.add(new Segment(-100.0f, -95.0f, "Mala", Color.parseColor("#ffd50000")));
            arrayList.add(new Segment(-90.0f, -85.0f, "", Color.parseColor("#ffd50000")));
            arrayList.add(new Segment(-80.0f, -75.0f, "Buena", Color.parseColor("#ffffd600")));
            arrayList.add(new Segment(-70.0f, -65.0f, "", Color.parseColor("#8CC63E")));
            arrayList.add(new Segment(-60.0f, -0.0f, "Perfecta", Color.parseColor("#8CC63E")));

        }

        this.bv.setSegments(arrayList);
        this.bv.setUnit("dBm");
        this.bv.setShowDescriptionText(true);
        this.bv.setSideStyle(SegmentedBarViewSideStyle.ROUNDED);
        this.bv.setDescriptionTextColor(R.color.colorDes);
    }

    private void inicializarSegmentoFuerzaSenal() {
        @SuppressLint("WrongConstant") final TelephonyManager telephonyManager2 = (TelephonyManager) getSystemService("phone");
        if (Build.VERSION.SDK_INT >= 28 && !this.Utils.PermissionUELocationGPS(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "Debe habilitar el permiso de ubicación", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(InfoSimActivity.this, new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, 1);

        }
        if (Build.VERSION.SDK_INT >= 28 && this.Utils.PermissionUELocationGPS(getApplicationContext()) && !this.Utils.openLocation(getApplicationContext())) {

        }
        this.listener = new PhoneStateListener() {

            @SuppressLint("WrongConstant")
            public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                List<CellInfo> list;
                String valueOf = null;
                super.onSignalStrengthsChanged(signalStrength);
                StringBuilder sb = new StringBuilder();
                StringBuilder sb2 = new StringBuilder();
                if (!InfoSimActivity.this.Utils.PermissionUELocation(InfoSimActivity.this)) {
                    list = null;
                } else if (Build.VERSION.SDK_INT < 23 || InfoSimActivity.this.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0) {
                    list = telephonyManager2.getAllCellInfo();
                } else {
                    return;
                }
                if (Build.VERSION.SDK_INT < 23) {
                    try {
                        new getSignal(InfoSimActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
                    } catch (RejectedExecutionException e) {
                        e.printStackTrace();
                    }
                } else if (InfoSimActivity.this.checkSelfPermission("android.permission.ACCESS_FINE_LOCATION") == 0 && InfoSimActivity.this.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0 && InfoSimActivity.this.checkSelfPermission("android.permission.READ_PHONE_STATE") == 0) {
                    try {
                        new getSignal(InfoSimActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
                    } catch (RejectedExecutionException e2) {
                        e2.printStackTrace();
                    }
                } else {
                }
                String str = "0";
                if (list != null) {
                    android.util.Log.d("cellInfos", list.toString());
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i) instanceof CellInfoWcdma) {
                            android.util.Log.d("cellInfos.get(i)", list.get(i).toString());
                            CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) list.get(i);
                            CellSignalStrengthWcdma cellSignalStrength = cellInfoWcdma.getCellSignalStrength();
                            android.util.Log.d("cellInfoWcdma", String.valueOf(cellSignalStrength.getDbm()));
                            valueOf = String.valueOf(cellSignalStrength.getDbm());
                            if (list.get(i).isRegistered()) {
                                sb.append("\nCelda: ");
                                sb.append(cellInfoWcdma.getCellIdentity().getCid());
                                sb.append(" Red: 3G ");
                                sb.append(" dBm: ");
                                sb.append(valueOf);
                            } else {
                                sb2.append("\nCelda: ");
                                sb2.append(cellInfoWcdma.getCellIdentity().getCid());
                                sb2.append(" Red: 3G ");
                                sb2.append(" dBm: ");
                                sb2.append(valueOf);
                            }
                        } else if (list.get(i) instanceof CellInfoGsm) {
                            android.util.Log.d("cellInfos.get(i)", list.get(i).toString());
                            CellInfoGsm cellInfoGsm = (CellInfoGsm) list.get(i);
                            CellSignalStrengthGsm cellSignalStrength2 = cellInfoGsm.getCellSignalStrength();
                            android.util.Log.d("cellInfoGsm", String.valueOf(cellSignalStrength2.getDbm()));
                            valueOf = String.valueOf(cellSignalStrength2.getDbm());
                            if (list.get(i).isRegistered()) {
                                sb.append("\nCelda: ");
                                sb.append(cellInfoGsm.getCellIdentity().getCid());
                                sb.append(" Red: 2G ");
                                sb.append(" dBm: ");
                                sb.append(valueOf);
                            } else {
                                sb2.append("\nCelda: ");
                                sb2.append(cellInfoGsm.getCellIdentity().getCid());
                                sb2.append(" Red: 2G ");
                                sb2.append(" dBm: ");
                                sb2.append(valueOf);
                            }
                        } else if (list.get(i) instanceof CellInfoLte) {
                            android.util.Log.d("cellInfos.get(i)", list.get(i).toString());
                            CellInfoLte cellInfoLte = (CellInfoLte) list.get(i);
                            CellSignalStrengthLte cellSignalStrength3 = cellInfoLte.getCellSignalStrength();
                            android.util.Log.d("cellInfoLte", String.valueOf(cellSignalStrength3.getDbm()));
                            valueOf = String.valueOf(cellSignalStrength3.getDbm());
                            if (list.get(i).isRegistered()) {
                                sb.append("\nCelda: ");
                                sb.append(cellInfoLte.getCellIdentity().getCi());
                                sb.append(" Red: 4G ");
                                sb.append(" dBm: ");
                                sb.append(valueOf);
                            } else {
                                sb2.append("\nCelda: ");
                                sb2.append(cellInfoLte.getCellIdentity().getCi());
                                sb2.append(" Red: 4G ");
                                sb2.append(" dBm: ");
                                sb2.append(valueOf);
                            }
                        }
                        str = valueOf;
                    }
                    android.util.Log.d("RadioN:", sb.toString());
                    try {
                        if (sb.toString().equals("")) {

                        } else {

                            Log.d("RadioN2:", sb2.toString());
                        }
                    } catch (Exception e3) {
                        e3.printStackTrace();
                    }
                }
                android.util.Log.v("Signal Strenght:", str);
            }
        };
    }


    public ServiceState getServiceState() {
        return this.myServiceState;
    }

    @SuppressLint("WrongConstant")
    private void obtenerTipoSenal() {
        this.networkClass.setText(this.Conn.getNetworkClass(InfoSimActivity.this));
        if (this.Utils.PermissionUELocation(getApplicationContext())) {
            try {
                if (Build.VERSION.SDK_INT < 23 || getApplicationContext().checkSelfPermission("android.permission.ACCESS_FINE_LOCATION") == 0 || getApplicationContext().checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0) {
                    this.telephonyManager.listen(this.listener, 256);
                }
            } catch (ClassCastException e) {
                e.printStackTrace();
            }

        }
    }

    public class getSignal extends AsyncTask<Void, Void, Void> {
        String banda = "";
        Context context;
        Float rssi = Float.valueOf(0.0f);

        public getSignal(Context context2) {
            this.context = context2;
        }

        public Void doInBackground(Void... voidArr) {
            @SuppressLint("MissingPermission") List<ICell> cells = NetMonsterFactory.INSTANCE.get(this.context).getCells();
            ICellProcessor<Unit> r0 = new ICellProcessor<Unit>() {

                @Override
                public Unit processCdma(CellCdma cellCdma) {
                    try {
                        String num = ((Integer) Objects.requireNonNull(cellCdma.getBand().getNumber())).toString();
                        String name = cellCdma.getBand().getName();
                        InfoSimActivity.getSignal getsignal = InfoSimActivity.getSignal.this;
                        getsignal.banda = "B" + num + " - " + name;
                        InfoSimActivity.getSignal.this.rssi = Float.valueOf((float) cellCdma.getSignal().getCdmaRssi().intValue());
                        return null;
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                public Unit processGsm(CellGsm cellGsm) {
                    try {
                        String num = ((Integer) Objects.requireNonNull(cellGsm.getBand().getNumber())).toString();
                        String name = cellGsm.getBand().getName();
                        if (num.equals("900")) {
                            num = "8";
                        }
                        InfoSimActivity.getSignal getsignal = InfoSimActivity.getSignal.this;
                        getsignal.banda = "B" + num + " - " + name;
                        InfoSimActivity.getSignal.this.rssi = Float.valueOf((float) cellGsm.getSignal().getRssi().intValue());
                        return null;
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                public Unit processLte(CellLte cellLte) {
                    try {
                        String num = ((Integer) Objects.requireNonNull(cellLte.getBand().getNumber())).toString();
                        String name = cellLte.getBand().getName();
                        InfoSimActivity.getSignal getsignal = InfoSimActivity.getSignal.this;
                        getsignal.banda = "B" + num + " - " + name;
                        InfoSimActivity.getSignal.this.rssi = Float.valueOf((float) cellLte.getSignal().getRssi().intValue());
                        return null;
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                public Unit processNr(CellNr cellNr) {
                    try {
                        String num = ((Integer) Objects.requireNonNull(cellNr.getBand().getNumber())).toString();
                        String name = cellNr.getBand().getName();
                        InfoSimActivity.getSignal getsignal = InfoSimActivity.getSignal.this;
                        getsignal.banda = "B" + num + " - " + name;
                        return null;
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                public Unit processTdscdma(CellTdscdma cellTdscdma) {
                    try {
                        String num = ((Integer) Objects.requireNonNull(cellTdscdma.getBand().getNumber())).toString();
                        String name = cellTdscdma.getBand().getName();
                        InfoSimActivity.getSignal getsignal = InfoSimActivity.getSignal.this;
                        getsignal.banda = "B" + num + " - " + name;
                        InfoSimActivity.getSignal.this.rssi = Float.valueOf((float) cellTdscdma.getSignal().getRssi().intValue());
                        return null;
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override // com.marlon.cz.mroczis.netmonster.core.model.cell.ICellProcessor
                public Unit processWcdma(CellWcdma cellWcdma) {
                    try {
                        String num = ((Integer) Objects.requireNonNull(cellWcdma.getBand().getNumber())).toString();
                        String name = cellWcdma.getBand().getName();
                        InfoSimActivity.getSignal getsignal = InfoSimActivity.getSignal.this;
                        getsignal.banda = "B" + num + "-" + name;
                        InfoSimActivity.getSignal.this.rssi = Float.valueOf((float) cellWcdma.getSignal().getRscp().intValue());
                        return null;
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            };
            if (cells.isEmpty()) {
                return null;
            }
            cells.get(0).let(r0);
            return null;
        }

        public void onPostExecute(Void r4) {
            TextView textView = InfoSimActivity.this.tvSignal;
            textView.setText(this.banda + " MHz");
            InfoSimActivity.this.bv.setValue(this.rssi);
            super.onPostExecute((Void) r4);
        }
    }




}