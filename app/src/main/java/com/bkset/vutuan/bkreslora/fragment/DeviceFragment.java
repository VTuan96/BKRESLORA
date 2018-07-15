package com.bkset.vutuan.bkreslora.fragment;



import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bkset.vutuan.bkreslora.adapter.CustomGraphAdapter;
import com.github.mikephil.charting.data.Entry;
import com.bkset.vutuan.bkreslora.R;
import com.bkset.vutuan.bkreslora.activity.BieuDoActivity;
import com.bkset.vutuan.bkreslora.adapter.GraphAdapter;
import com.bkset.vutuan.bkreslora.model.Graph;
import com.bkset.vutuan.bkreslora.task.DownloadJSON;
import com.bkset.vutuan.bkreslora.utils.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class DeviceFragment extends Fragment {
    private static final String TITLE="TITLE";
    private static final String GRAPH="GRAPH";
    private static final String TIME="TIME";
    private static final String DEVICEID="DEVICEID";
    private static final String DATE_TIME="DATE_TIME";

    private TextView txtItemContent, txt_Time;
    private TextView txtTitle;
    private ListView rvItemBieuDoThongKe;
    private ArrayList<Graph> listGraph=new ArrayList<>();
//    private GraphAdapter adapter;
    private CustomGraphAdapter adapter;

    private DownloadJSON downloadJSON;
    String arr_thongso[] = {"PH", "Muối", "Oxy", "Nhiệt độ", "H2S", "NH3", "NH4 Min", "NH4 Max", "N02 Min", "NO2 Max", "Sulfide Min", "Sulfide Max"};

    public String tmpNgayThangNam="";
    public String tmpSelectedDeviceId="";

    private Calendar calendar;
    private int year, month, day;
    ProgressDialog pDialog;

    private View v;

    //All components of all graphs
    private ArrayList<Entry> entriesPH=new ArrayList<>();
    private ArrayList labelsPH = new ArrayList<String>();

    private ArrayList<Entry> entriesSalt=new ArrayList<>();
    private ArrayList labelsSalt = new ArrayList<String>();

    private ArrayList<Entry> entriesOxy=new ArrayList<>();
    private ArrayList labelsOxy = new ArrayList<String>();

    private ArrayList<Entry> entriesTemp=new ArrayList<>();
    private ArrayList labelsTemp = new ArrayList<String>();


    private ArrayList<Entry> entriesH2S=new ArrayList<>();
    private ArrayList labelsH2S = new ArrayList<String>();

    private ArrayList<Entry> entriesNH3=new ArrayList<>();
    private ArrayList labelsNH3 = new ArrayList<String>();

    private ArrayList<Entry> entriesNH4Min=new ArrayList<>();
    private ArrayList labelsNH4Min = new ArrayList<String>();

    private ArrayList<Entry> entriesNH4Max=new ArrayList<>();
    private ArrayList labelsNH4Max = new ArrayList<String>();

    private ArrayList<Entry> entriesNO2Min=new ArrayList<>();
    private ArrayList labelsNO2Min = new ArrayList<String>();

    private ArrayList<Entry> entriesNO2Max=new ArrayList<>();
    private ArrayList labelsNO2Max = new ArrayList<String>();

    private ArrayList<Entry> entriesH2SMax=new ArrayList<>();
    private ArrayList labelsH2SMax = new ArrayList<String>();

    private ArrayList<Entry> entriesH2SMin=new ArrayList<>();
    private ArrayList labelsH2SMin = new ArrayList<String>();

    //All label of graph
    private String[] arrLabels=new String[]{"PH","Salt","Oxy", "Temp", "H2S","NH3","NH4 Max","NH4 Min",
            "NO2 Max","NO2 Min","H2S Max","H2S Min" };

    public static DeviceFragment newInstance(String title, int selectDeviceID){
        DeviceFragment df=new DeviceFragment();
        Bundle bundle=new Bundle();
        bundle.putString(TITLE,title);
        bundle.putInt(DEVICEID,selectDeviceID);
        df.setArguments(bundle);
        return df;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        v= LayoutInflater.from(getContext()).inflate(R.layout.fragment_device,container,false);
        initWidgets(v);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        Bundle bundle=getArguments();
        String title=bundle.getString(TITLE);
        tmpSelectedDeviceId= String.valueOf(bundle.getInt(DEVICEID));

        pDialog = new ProgressDialog(getContext());

        txtTitle.setText("Thiết bị: "+title);
        txt_Time.setText("Ngày: "+ BieuDoActivity.selectedDateTime);
//        rvItemBieuDoThongKe.setHasFixedSize(true);
//        LinearLayoutManager manager=new LinearLayoutManager(getContext());
//        rvItemBieuDoThongKe.setLayoutManager(manager);

//        listGraph=new ArrayList<>();
////        adapter=new GraphAdapter(listGraph);
//        adapter = new CustomGraphAdapter(getContext(), listGraph);
//        rvItemBieuDoThongKe.setAdapter(adapter);

        initGraph();

        //lay du lieu bieu do cua 12 thong so
        for (int i=1;i<=12;i++){
            getDataThongKe(i);
        }

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    private void initWidgets(View v){
        txtItemContent= (TextView) v.findViewById(R.id.txtItemContent);
        txtTitle= (TextView) v.findViewById(R.id.txtTitle);
        rvItemBieuDoThongKe= (ListView) v.findViewById(R.id.rvItemBieuDoThongKe);
        txt_Time = (TextView) v.findViewById(R.id.txt_time);
    }

    public void initGraph(){
        entriesPH=new ArrayList<>();
        labelsPH = new ArrayList<String>();

        entriesSalt=new ArrayList<>();
        labelsSalt = new ArrayList<String>();

        entriesOxy=new ArrayList<>();
        labelsOxy = new ArrayList<String>();

        entriesTemp=new ArrayList<>();
        labelsTemp = new ArrayList<String>();


        entriesH2S=new ArrayList<>();
        labelsH2S = new ArrayList<String>();

        entriesNH3=new ArrayList<>();
        labelsNH3 = new ArrayList<String>();

        entriesNH4Min=new ArrayList<>();
        labelsNH4Min = new ArrayList<String>();

        entriesNH4Max=new ArrayList<>();
        labelsNH4Max = new ArrayList<String>();

        entriesNO2Min=new ArrayList<>();
        labelsNO2Min = new ArrayList<String>();

        entriesNO2Max=new ArrayList<>();
        labelsNO2Max = new ArrayList<String>();

        entriesH2SMax=new ArrayList<>();
        labelsH2SMax = new ArrayList<String>();

        entriesH2SMin=new ArrayList<>();
        labelsH2SMin = new ArrayList<String>();

        listGraph=new ArrayList<>();
        Graph gPH=new Graph(arrLabels[0],entriesPH,labelsPH);
        Graph gSalt=new Graph(arrLabels[1],entriesSalt,labelsSalt);
        Graph gOxy=new Graph(arrLabels[2],entriesOxy,labelsOxy);
        Graph gTemp=new Graph(arrLabels[3],entriesTemp,labelsTemp);
        Graph gH2S=new Graph(arrLabels[4],entriesH2S,labelsH2S);
        Graph gNH3=new Graph(arrLabels[5],entriesNH3,labelsNH3);
        Graph gNH4Max=new Graph(arrLabels[6],entriesNH4Max,labelsNH4Max);
        Graph gNH4Min=new Graph(arrLabels[7],entriesNH4Min,labelsNH4Min);
        Graph gNO2Max=new Graph(arrLabels[8],entriesNO2Min,labelsNO2Max);
        Graph gNO2Min=new Graph(arrLabels[9],entriesNO2Min,labelsNO2Min);
        Graph gH2SMax=new Graph(arrLabels[10],entriesH2SMax,labelsH2SMax);
        Graph gH2SMin=new Graph(arrLabels[11],entriesH2SMin,labelsH2SMin);
        Graph [] arrGraph=new Graph[]{gPH,gSalt,gTemp, gH2S,gH2SMax,gH2SMin,gNH3,gNH4Max,gNH4Min,gNO2Max,gNO2Min,gOxy};
        for (Graph g:arrGraph){
            listGraph.add(g);
        }


        adapter = new CustomGraphAdapter(getContext(), listGraph);
        rvItemBieuDoThongKe.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    //lay du lieu ung voi tung thong so
    public void getDataThongKe(final int tempSelectThongSo) {
        String urlThongKe = Constant.URL + Constant.API_GET_DATA_PACKAGE + "date=" + BieuDoActivity.tmpNgayThangNam +
                "&deviceid=" + tmpSelectedDeviceId + "&paramid=" + String.valueOf(tempSelectThongSo);
        Uri builder = Uri.parse(urlThongKe)
                .buildUpon()
                .build();

        downloadJSON = new DownloadJSON(getContext());


        downloadJSON.GetJSONArray(builder, new DownloadJSON.DownloadJSONCallBack() {
            @Override
            public void onSuccess(String msgData) {
                try{
                    //JSONObject jsonObj = new JSONObject(msgData);
                    JSONArray jsonArray = new JSONArray(msgData);
                    if(jsonArray.length()==0){
//                        txtContent.setVisibility(View.VISIBLE);
                    }
                    else{
                        String nameGraph=arr_thongso[tempSelectThongSo-1];
                        ArrayList<Entry> entries = new ArrayList<>();
                        ArrayList labels = new ArrayList<String>();

                        for(int i=0; i<jsonArray.length(); i++){
                            JSONObject objTmp = jsonArray.getJSONObject(i);
                            double value = objTmp.getDouble("value");
                            String time = objTmp.getString("time");
                            String[] words = time.split("T");
                            Log.d("time",words[0]+words[1]);
                            entries.add(new Entry(i,(float)value));
                            labels.add(words[1]);
                        }
                        Graph graph=new Graph(nameGraph,entries,labels);

                        Log.i("size graph", listGraph.size() + "");
                        if (listGraph.size() > 0) {
                            txtItemContent.setVisibility(View.GONE);
                        }

                        int index = -1;

                        for (int k = 0; k < arr_thongso.length; k++) {
                            if (nameGraph == arr_thongso[k]) {
                                index = k;
                            }
                        }
                        listGraph.set(index, graph);
                        adapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(String msgError) {
//                progress.dismiss();
                Log.i("Error", msgError);
            }
        });
    }

    @Override
    public void onResume() {
        adapter.notifyDataSetChanged();
        super.onResume();
    }
}
