package com.bkset.vutuan.bkreslora.fragment;



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
import android.widget.TextView;

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
    private RecyclerView rvItemBieuDoThongKe;
    private ArrayList<Graph> listGraph=new ArrayList<>();
    private GraphAdapter adapter;

    private DownloadJSON downloadJSON;
    String arr_thongso[] = {"PH", "Muối", "Oxy", "Nhiệt độ", "H2S", "NH3", "NH4 Min", "NH4 Max", "N02 Min", "NO2 Max", "Sulfide Min", "Sulfide Max"};

    public String tmpNgayThangNam="";
    public String tmpSelectedDeviceId="";

    private Calendar calendar;
    private int year, month, day;

    private View v;

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

        txtTitle.setText("Thiết bị: "+title);
        txt_Time.setText("Ngày: "+ BieuDoActivity.selectedDateTime);
        rvItemBieuDoThongKe.setHasFixedSize(true);
        LinearLayoutManager manager=new LinearLayoutManager(getContext());
        rvItemBieuDoThongKe.setLayoutManager(manager);

        listGraph=new ArrayList<>();
        adapter=new GraphAdapter(listGraph);
        rvItemBieuDoThongKe.setAdapter(adapter);

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
        rvItemBieuDoThongKe= (RecyclerView) v.findViewById(R.id.rvItemBieuDoThongKe);
        txt_Time = (TextView) v.findViewById(R.id.txt_time);
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
                        System.out.println("grap size: "+ listGraph.size());
                        if (listGraph.size()<12){
                            listGraph.add(graph);
                            adapter.notifyDataSetChanged();
                            Log.i("size graph",listGraph.size()+"");
                            if (listGraph.size()>0){
                                txtItemContent.setVisibility(View.GONE);
//                                Log.i("size graph",listGraph.size()+"");
                            } else if (listGraph.size()==12){
                                adapter.notifyDataSetChanged();
                            } else
                                txtItemContent.setVisibility(View.VISIBLE);

                        }


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
