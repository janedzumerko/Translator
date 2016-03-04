package com.example.jane.timskaapplication101;

/**
 * Created by Jane on 7/22/2015.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * A {@link Fragment} subclass, containing some utility methods.
 * Activities  must implement the
 * {@link BaseFragment.OnBaseFragmentInteractionListener} interface
 * to handle interaction events.
 *
 * View inflation is a bit different.
 */
public abstract class BaseFragment extends Fragment {
    private View retryView = null; // acts as overlay on main view and loading
    private Button retryButton = null;
    private ViewGroup mainLayout = null; // optional, allows use of setContentView
    public static View loadingView;


    private OnBaseFragmentInteractionListener mListener;


    public enum MsgType {
        SWITCH_SCREEN, // not in use atm
        SECTION_ATTACHED // Fragment of a certain menu section has just called its onAttach
    }


    public BaseFragment() {
        // Required empty public constructor
        super();
        getOrCreateArguments();
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        printFragUpdate("onAttach()");
        try {
            mListener = (OnBaseFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        printFragUpdate("onDetach()");
        mListener = null;
    }




    /**
     * convenience, makes there always BE args
     * cannot @Override getArguments(): it's final, so we put this in constructor
     */
    private Bundle getOrCreateArguments(){
        Bundle args = getArguments();
        if (args == null){
            args = new Bundle();
            setArguments(args);
        }
        return args;
    }


    // layout convenience stuff


    /**
     * final, use {@link #afterCreateView(LayoutInflater, ViewGroup, Bundle)} instead
     */
    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container,
                                   Bundle savedInstanceState) {
        printFragUpdate("onCreateView()");
        ViewGroup ret = createEmptyMainLayout(); // sets mainLayout for BaseFragment
        afterCreateView(inflater, ret, savedInstanceState);
        return ret;
    }

    public void afterCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        printFragUpdate("afterCreateView()");
    }


    protected ViewGroup createEmptyMainLayout(){
        mainLayout = (RelativeLayout) View.inflate(getActivity(), R.layout.fragment_base, null);
        loadingView = mainLayout.findViewById(R.id.loadingLayout);
        mainLayout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    hideSoftKeyboard(); // so you can dismiss it by clicking outside EditText
            }
        });

        Context c = getActivity();
        retryView = mainLayout.findViewById(R.id.retryLayout);
        retryButton = (Button) retryView.findViewById(R.id.retryBtn);

        retryButton.setOnClickListener(new NoFastClick.ViewOnClickListener() {
            @Override
            public void doOnClick(View v) {
                dismissRetry();
                onRetryClicked();
            }
        });

        return mainLayout;
    }

    /**
     * can use a familiar method, on a Fragment, at any time after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     */
    public void setContentView(int layoutResID) {
        View ll = View.inflate(getActivity(), layoutResID, null);
        setContentView(ll);
    }
    /**
     * can use a familiar method, on a Fragment, at any time after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     */
    public void setContentView(View view) {
        if(mainLayout == null)
            createEmptyMainLayout();
        if(isMainContentAdded(mainLayout)) {
            mainLayout.removeViewAt(contentLayoutPosition());
        }
        if(view == null) return;
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
        view.setLayoutParams(lp);
        mainLayout.addView(view, contentLayoutPosition());
    }

    protected boolean isMainContentAdded(ViewGroup mainLayout){ // override in child class if it messes with mainLayout
        return mainLayout.getChildCount() > 2; // more than just the loading and retry
    }
    protected int contentLayoutPosition(){
        return 0;
    }

    /**
     * can only be used if mainLayout is created with createEmptyMainLayout
     */
    protected View findViewById(int resId){
        return mainLayout.findViewById(resId);
    }
	/*	protected void showLoading(boolean blocking){ // note: non-blocking clears the contentView by default
		showLoading(blocking, !blocking, null);
	}
	protected void showLoading(boolean blocking, boolean clearContentView){
		showLoading(blocking, clearContentView, null);
	}*/
    /**
     * can only be used if mainLayout is created with createEmptyMainLayout
     */
    public static void showLoading(){
        //  MainActivity.drawerisEnabled = false;
        showLoading(true);
    }
    public static void showLoading(boolean blocking){
        if(loadingView != null) {
            loadingView.setClickable(blocking);
            loadingView.setVisibility(View.VISIBLE);
        }

    }



    public static void dismissLoading(){
        //  MainActivity.drawerisEnabled = true;
        if(loadingView != null) {
            loadingView.setVisibility(View.GONE);
        }

    }
    protected boolean isLoading(){
        if(loadingView == null)
            return false;
        return loadingView.getVisibility() == View.VISIBLE;
    }

    protected void hideSoftKeyboard() {
        try{
            Activity activity = getActivity();
            InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }catch(NullPointerException e){}
    }



    // marks whether a loading had been started, waiting for an external Activity to show up
    private boolean hasLaunchedExternalActivity = false;
    // Facebook can take a good 10 seconds to open...
    protected void showExternalLaunchLoading(){
        if(!isLoading()){
            hasLaunchedExternalActivity = true;
            showLoading();
        }
    }
    protected void dismissExternalLaunchLoading(){
        if(hasLaunchedExternalActivity){
            hasLaunchedExternalActivity = false;
            dismissLoading();
        }
    }


    /**
     * can only be used if mainLayout is created with createEmptyMainLayout
     */
    protected void showRetry(){
        if(retryView != null) {
            retryView.setVisibility(View.VISIBLE);
        }
    }
    protected void dismissRetry(){
        if(retryView != null) {
            retryView.setVisibility(View.GONE);
        }
    }
    protected boolean isShowingRetry(){
        if(retryView == null)
            return false;
        return retryView.getVisibility() == View.VISIBLE;
    }

    // Lifecycle callbacks.
    // TODO: use the debug/release distinction and Activity callbacks
    final protected boolean displayLifecycleCallbacks = false; // proguard will strip this
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        printFragUpdate("onActivityCreated()");
        super.onActivityCreated(savedInstanceState);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        printFragUpdate("onCreate()");
    }
    @Override
    public void onDestroy() {
        printFragUpdate("onDestroy()");
        super.onDestroy();
    }
    @Override
    public void onDestroyView() {
        printFragUpdate("onDestroyView()");
        super.onDestroyView();
    }
    @Override
    public void onPause() {
        printFragUpdate("onPause()");
        super.onPause();
    }
    @Override
    public void onResume() {
        printFragUpdate("onResume()");
        super.onResume();
        dismissExternalLaunchLoading();
    }
    @Override
    public void onStart() {
        printFragUpdate("onStart()");
        super.onStart();
    }
    @Override
    public void onStop() {
        printFragUpdate("onStop()");
        super.onStop();
    }
    // string preceding debug messages
    private String fragUpdateIdentifierString = null;
    protected void printFragUpdate(String msg){
        if (displayLifecycleCallbacks) {
            if(fragUpdateIdentifierString == null) // lazy init
                fragUpdateIdentifierString = "->" + getClass().getSimpleName() + " " + hashCode() + " ";
            sout(fragUpdateIdentifierString + msg);
        }
    }





    // for parameter storage in setArguments()
    protected Map<String,String> bundle2Map(Bundle b){
        if (b == null) return null;
        Map<String,String> ret = new HashMap<String,String>();
        for(String k : b.keySet()){
            ret.put(k, b.getString(k));
        }
        return ret;
    }
    protected Bundle map2Bundle(Map<String,String> m){
        if (m == null) return null;
        Bundle ret = new Bundle();
        for(String k : m.keySet()){
            ret.putString(k, m.get(k));
        }
        return ret;
    }






    /**
     * from: https://gist.github.com/artem-zinnatullin/6916740
     * bugfix for: https://code.google.com/p/android/issues/detail?id=40537 and others
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // notifying nested fragments (support library bug fix)
        final FragmentManager childFragmentManager = getChildFragmentManager();

        if (childFragmentManager != null) {
            final List<Fragment> nestedFragments = childFragmentManager.getFragments();

            if (nestedFragments == null || nestedFragments.size() == 0) return;

            for (Fragment childFragment : nestedFragments) {
                if (childFragment != null && !childFragment.isDetached() && !childFragment.isRemoving()) {
                    childFragment.onActivityResult(requestCode, resultCode, data);
                }
            }
        }
        printFragUpdate("onActivityResult()");
    }


    public static void sout(Object o){ // finger-saver. TODO: Log?
        try{
            System.out.println(o);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    protected abstract void onRetryClicked();

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

    public interface OnBaseFragmentInteractionListener {
        public void onBaseFragmentInteraction(MsgType type, BaseFragment caller, Bundle data);
    }

    protected void sendFragmentInteraction(MsgType type, Bundle data){
        mListener.onBaseFragmentInteraction(type, this, data);
    }
}

