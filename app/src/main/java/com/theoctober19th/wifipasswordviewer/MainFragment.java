package com.theoctober19th.wifipasswordviewer;

import android.content.Context;
import com.theoctober19th.wifipasswordviewer.models.Network;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.chainfire.libsuperuser.Shell;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    ArrayList<Network> networkList;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        networkList = new ArrayList<>();


        ArrayList<Network> list = new ArrayList<>();
        for(int c = 0; c<10; c++){
            Network n = new Network("ssid", "password");
            list.add(n);
        }

        try{
            fetchNetworkListFromDevice();
        }catch(Exception e){
            e.printStackTrace();
        }

        mAdapter = new WifiListAdapter(getContext(), networkList);
        mRecyclerView.setAdapter(mAdapter);

        //new NetworkFetcherTask().execute();

    }

    public  JSONArray covertJsonObjectToJsonArray(Object InsideArray) {

        JSONArray jsonArray;

        if (InsideArray instanceof JSONArray) {
            jsonArray = (JSONArray) InsideArray;
        } else {
            jsonArray = new JSONArray();
            jsonArray.put((JSONObject) InsideArray);
        }
        return jsonArray;
    }

    private void fetchNetworkListFromDevice() throws XmlPullParserException, IOException, JSONException {
//        XmlPullParserFactory parserFactory = XmlPullParserFactory.newInstance();
//        XmlPullParser pullParser = parserFactory.newPullParser();
//
//        InputStream inputStream= getContext().getAssets().open("data.xml");
//        pullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
//        pullParser.setInput(inputStream, null);
//
//        int eventType = pullParser.getEventType();
//        Network currentNetwork = null;
//
//        while(eventType != XmlPullParser.END_DOCUMENT){
//            String eltName = null;
//
//            switch (eventType){
//                case XmlPullParser.START_TAG:
//                    eltName = pullParser.getName();
//
//                    if("WifiConfiguration".equals(eltName)){
//                        currentNetwork = new Network();
//                        networkList.add(currentNetwork);
//                    } else if(currentNetwork != null){
//                        String attrValue = pullParser.getAttributeValue(null, "name");
//                        if(attrValue.equals("SSID")){
//                            String ssid  = pullParser.nextText();
//                            currentNetwork.setSsid(ssid);
//                        } else if(attrValue.equals("PreSharedKey")){
//                            String password = pullParser.nextText();
//                            currentNetwork.setPassword(password);
//                        }
//                    }
//                    break;
//            }
//
//            eventType = pullParser.next();
//        }
//        inputStream.close();


        List<Network> networks = new ArrayList<>();
        List<String> supplicant;

        boolean suEh;
        suEh = Shell.SU.available();

        //if device is rooted
        if(/*suEh*/true){

            //if android version is Oreo or newer
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                supplicant = Shell.SU.run("cat /data/misc/wifi/WifiConfigStore.xml");
                StringBuilder flattener = new StringBuilder();
                if (supplicant != null) {
                    for (String line: supplicant) {
                        flattener.append(line);
                    }
                }
                String flattened = flattener.toString();

                JSONObject jsonObj = null;
                jsonObj = XML.toJSONObject(flattened);

                Object networkObject = jsonObj.getJSONObject("WifiConfigStoreData").getJSONObject("NetworkList").get("Network");

                JSONArray networkJSONArray = covertJsonObjectToJsonArray(networkObject);


                for(int i=0; i<networkJSONArray.length() ; i++){
                    Network network = new Network();
                    JSONObject wifiConfigObj = networkJSONArray.getJSONObject(i).getJSONObject("WifiConfiguration");
                    JSONArray stringArray = covertJsonObjectToJsonArray(wifiConfigObj.get("string"));
                    for(int j=0; j<stringArray.length(); j++){
                        JSONObject infoEntry = stringArray.getJSONObject(j);
                        String infoType = infoEntry.getString("name");
                        if(infoType.equals("SSID")){
                            String ssid = infoEntry.getString("content");
                            ssid = ssid.replaceAll("^\"|\"$", "");
                            network.setSsid(ssid);
                        }
                        if(infoType.equals("PreSharedKey")){
                            String password = infoEntry.getString("content");
                            password = password.replaceAll("^\"|\"$", "");
                            network.setPassword(password);
                        }
                    }
                    networks.add(network);
                    networkList.add(network);
                }

                //TODO extract wifi ssid and password
//                try {
//                    XmlPullParserFactory parserFactory = XmlPullParserFactory.newInstance();
//                    XmlPullParser parser = parserFactory.newPullParser();
//                    InputStream is = new ByteArrayInputStream( flattened.getBytes( Charset.defaultCharset() ) );
//                    parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
//                    parser.setInput(is, null);
//
//                    int eventType = parser.getEventType();
//                    Network currentNetwork = null;
//
//                    while(eventType != XmlPullParser.END_DOCUMENT){
//
//                        String eltName = null;
//
//                        switch (eventType){
//                            case XmlPullParser.START_TAG:
//                                eltName = parser.getName();
//                                if("Network".equals(eltName)){
//                                    Log.i("wififinder", "New Network Entry");
//                                    currentNetwork = new Network();
//                                } else if(currentNetwork != null){
//                                    if("string".equals(eltName)){
//                                        if(parser.getAttributeValue(0).equals("SSID")){
//                                            currentNetwork.setSsid(parser.nextText());
//                                        }else if(parser.getAttributeValue(0).equals("PreSharedKey")){
//                                            currentNetwork.setPassword(parser.nextText());
//                                        }
//                                    }
//                                }
//                                break;
//                        }
//                        if(currentNetwork != null){
//                            networks.add(currentNetwork);
//                        }
//                    }
//                } catch (XmlPullParserException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                //TODO FINISHED


            }else{
                supplicant = Shell.SU.run("cat /data/misc/wifi/wpa_supplicant.conf");
                StringBuilder flattener = new StringBuilder();
                if (supplicant != null) {
                    for (String line: supplicant) {
                        flattener.append(line);
                    }
                }
                String flattened = flattener.toString();
                Log.i("wififinder", "older than oreo " + flattened);

                String exp = "\\{(.*?)\\}";
                Pattern pattern = Pattern.compile(exp);
                Matcher matcher = pattern.matcher(flattened);

                while(matcher.find()){
                    Network network = new Network();
                    String el = matcher.group();
                    String[] lineAsItem = el.split("\\s+");

                    for(String line: lineAsItem) {

                        if (line.contains("=")) {
                            String[] t = line.split("=");
                            if (t[0].equals("ssid")) {
                                network.setSsid(t[1]);
                            }
                            if (t[0].equals("psk")) {
                                network.setPassword(t[1]);
                            }
                        }
                    }
                    networks.add(network);

                }

            }



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

    public class NetworkFetcherTask extends AsyncTask<Void, Void, List<Network>>{

        private boolean suEh;


        @Override
        protected List<Network> doInBackground(Void... voids) {
            List<Network> networks = new ArrayList<>();
            List<String> supplicant;

            suEh = Shell.SU.available();

            //if device is rooted
            if(suEh){

                //if android version is Oreo or newer
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    supplicant = Shell.SU.run("cat /data/misc/wifi/WifiConfigStore.xml");
                    StringBuilder flattener = new StringBuilder();
                    if (supplicant != null) {
                        for (String line: supplicant) {
                            flattener.append(line);
                        }
                    }
                    String flattened = flattener.toString();
                    Log.i("wififinder", "newer than Oreo: " + flattened);

                    //TODO extract wifi ssid and password
                    try {
                        XmlPullParserFactory parserFactory = XmlPullParserFactory.newInstance();
                        XmlPullParser parser = parserFactory.newPullParser();
                        InputStream is = new ByteArrayInputStream( flattened.getBytes( Charset.defaultCharset() ) );
                        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                        parser.setInput(is, null);

                        int eventType = parser.getEventType();
                        Network currentNetwork = null;

                        while(eventType != XmlPullParser.END_DOCUMENT){

                            String eltName = null;

                            switch (eventType){
                                case XmlPullParser.START_TAG:
                                    eltName = parser.getName();
                                    if("Network".equals(eltName)){
                                        Log.i("wififinder", "New Network Entry");
                                        currentNetwork = new Network();
                                    } else if(currentNetwork != null){
                                        if("string".equals(eltName)){
                                            if(parser.getAttributeValue(0).equals("SSID")){
                                                currentNetwork.setSsid(parser.nextText());
                                            }else if(parser.getAttributeValue(0).equals("PreSharedKey")){
                                                currentNetwork.setPassword(parser.nextText());
                                            }
                                        }
                                    }
                                    break;
                            }
                            if(currentNetwork != null){
                                networks.add(currentNetwork);
                            }
                        }
                    } catch (XmlPullParserException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //TODO FINISHED


                }else{
                    supplicant = Shell.SU.run("cat /data/misc/wifi/wpa_supplicant.conf");
                    StringBuilder flattener = new StringBuilder();
                    if (supplicant != null) {
                        for (String line: supplicant) {
                            flattener.append(line);
                        }
                    }
                    String flattened = flattener.toString();
                    Log.i("wififinder", "older than oreo " + flattened);

                    String exp = "\\{(.*?)\\}";
                    Pattern pattern = Pattern.compile(exp);
                    Matcher matcher = pattern.matcher(flattened);

                    while(matcher.find()){
                        Network network = new Network();
                        String el = matcher.group();
                        String[] lineAsItem = el.split("\\s+");

                        for(String line: lineAsItem) {

                            if (line.contains("=")) {
                                String[] t = line.split("=");
                                if (t[0].equals("ssid")) {
                                    network.setSsid(t[1]);
                                }
                                if (t[0].equals("psk")) {
                                    network.setPassword(t[1]);
                                }
                            }
                        }
                        networks.add(network);

                    }

                }



            }

            return networks;
        }

        @Override
        protected void onPostExecute(List<Network> list) {
            //super.onPostExecute(list);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }

    }
}
