package com.googleandroidtv;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v17.leanback.widget.VerticalGridView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.OnHierarchyChangeListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.accessibility.AccessibilityManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
//import com.googleandroidtv.gms.clearcut.ClearcutLogger;
//import com.googleandroidtv.gms.common.api.GoogleApiClient;
//import com.googleandroidtv.gms.common.api.GoogleApiClient.Builder;
import com.facebook.stetho.Stetho;
import com.google.gson.Gson;
import com.googleandroidtv.R;
import com.googleandroidtv.adapter.AllAppGridAdapter;
import com.googleandroidtv.animation.AnimatorLifecycle;
import com.googleandroidtv.animation.EditModeMassFadeAnimator;
import com.googleandroidtv.animation.ForwardingAnimatorSet;
import com.googleandroidtv.animation.LauncherDismissAnimator;
import com.googleandroidtv.animation.LauncherLaunchAnimator;
import com.googleandroidtv.animation.LauncherPauseAnimator;
import com.googleandroidtv.animation.LauncherReturnAnimator;
import com.googleandroidtv.animation.MassSlideAnimator;
import com.googleandroidtv.animation.MassSlideAnimator.Direction;
import com.googleandroidtv.animation.NotificationLaunchAnimator;
import com.googleandroidtv.animation.ParticipatesInLaunchAnimation;
import com.googleandroidtv.apps.AppsAdapter;
import com.googleandroidtv.apps.AppsRanker;
import com.googleandroidtv.apps.LaunchPoint;
import com.googleandroidtv.apps.LaunchPointListGenerator;
import com.googleandroidtv.apps.OnEditModeChangedListener;
import com.googleandroidtv.asyncloopj.AsyncLoopj;
import com.googleandroidtv.bean.CategoryBean;
import com.googleandroidtv.custom_app.VerticalGridActivity;
import com.googleandroidtv.data.ConstData;
import com.googleandroidtv.logging.LeanbackLauncherEventLogger;
import com.googleandroidtv.modle.db.AppInfoService;
import com.googleandroidtv.notifications.HomeScreenMessaging;
import com.googleandroidtv.notifications.HomeScreenView;
import com.googleandroidtv.notifications.NotificationCardView;
import com.googleandroidtv.notifications.NotificationRowView;
import com.googleandroidtv.notifications.NotificationRowView.NotificationRowListener;
import com.googleandroidtv.notifications.NotificationsAdapter;
import com.googleandroidtv.util.NetWorkUtils;
import com.googleandroidtv.util.Partner;
import com.googleandroidtv.util.ReflectUtils;
import com.googleandroidtv.util.Util;
import com.googleandroidtv.wallpaper.LauncherWallpaper;
import com.googleandroidtv.widget.AllAppGridView;
import com.googleandroidtv.widget.EditModeView;
import com.googleandroidtv.recline.util.DrawableDownloader;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends Activity implements OnEditModeChangedListener, EditModeView.OnEditModeUninstallPressedListener, AdapterView.OnItemClickListener, Runnable {
    private static final String TAG = "ManiActivity_LEANBACK";
    private AccessibilityManager mAccessibilityManager;
    private boolean mAppEditMode;
    private AppWidgetHost mAppWidgetHost;
    private AppWidgetHostView mAppWidgetHostView;
    private AppWidgetManager mAppWidgetManager;
    private AppsRanker mAppsRanker;
    private AnimatorLifecycle mEditModeAnimation;
    private EditModeView mEditModeView;
    private LeanbackLauncherEventLogger mEventLogger;
    private boolean mFadeDismissAndSummonAnimations;
    //private GoogleApiClient mGoogleApiClient;
    private Handler mHandler;
    private HomeScreenAdapter mHomeAdapter;
    private HomeScreenView mHomeScreenView;
    private ArrayList<IdleListener> mIdleListeners;
    private int mIdlePeriod;
    private boolean mIsIdle;
    private boolean mKeepUiReset;
    private AnimatorLifecycle mLaunchAnimation;
    private LaunchPointListGenerator mLaunchPointListGenerator;
    private VerticalGridView mList;
    private final Runnable mMoveTaskToBack;
    private NotificationRowListener mNotifListener;
    private NotificationRowView mNotificationsView;
    BroadcastReceiver mPackageReplacedReceiver;
    private AnimatorLifecycle mPauseAnimation;
    private NotificationsAdapter mRecommendationsAdapter;
    private final Runnable mRefreshHomeAdapter;
    private boolean mResetAfterIdleEnabled;
    private int mResetPeriod;
    private HomeScrollManager mScrollManager;
    private boolean mShyMode;
    private boolean mStartingEditMode;
    private boolean mUserInteracted;
    private LauncherWallpaper mWallpaper;
    private NetWorkChangeReceiver mNetChangeReceiver;
    private IntentFilter mNetChangeFilter;
    private ImageView mImgWifi;
    private ImageView mImgEthernet;
    private LinearLayout mLayoutNetworkContainer;
    private RelativeLayout mLayoutLading;
    private GridView mAllAppGridView;
    private AllAppGridAdapter mAllAppGridAdapter;
    private List<LaunchPoint> mAllLaunchPoints;
    /**是否是第一次加载*/
    private boolean mIsFirstLoad = true;
    /**监测Activity状态*/
    private int mActivityState;
    private Handler mTimerHandler;
    /* renamed from: MainActivity.1 */
    public Context mContext;

   // Api call
  public static CategoryBean[] CategotriesList;

    class C01581 implements Runnable {
        C01581() {
        }
        public void run() {
            if (MainActivity.this.mHomeAdapter != null) {
                MainActivity.this.mHomeAdapter.refreshAdapterData();
            }
        }
    }

    /* renamed from: MainActivity.2 */
    class C01592 implements Runnable {
        C01592() {
        }

        public void run() {
            if (!MainActivity.this.moveTaskToBack(true)) {
                MainActivity.this.mLaunchAnimation.reset();
            }
        }
    }

    /* renamed from: MainActivity.3 */
    class C01603 extends Handler {
        C01603() {
        }

        public void handleMessage(Message msg) {
            boolean z = true;
            switch (msg.what) {
                case android.support.v7.recyclerview.R.styleable.RecyclerView_android_descendantFocusability /*1*/:
                case android.support.v7.recyclerview.R.styleable.RecyclerView_layoutManager /*2*/:
                    MainActivity mainActivity = MainActivity.this;
                    if (msg.what != 1) {
                        z = false;
                    }
                    mainActivity.mIsIdle = z;
                    for (int i = 0; i < MainActivity.this.mIdleListeners.size(); i++) {
                        ((IdleListener) MainActivity.this.mIdleListeners.get(i)).onIdleStateChange(MainActivity.this.mIsIdle);
                    }
                    break;
                case android.support.v7.preference.R.styleable.Preference_android_layout /*3*/:
                    if (MainActivity.this.mResetAfterIdleEnabled) {
                        MainActivity.this.mKeepUiReset = true;
                        MainActivity.this.resetLauncherState(true);
                    }
                    break;
                case android.support.v7.preference.R.styleable.Preference_android_title /*4*/:
                    MainActivity.this.onNotificationRowStateUpdate(msg.arg1);
                    break;
                case android.support.v7.preference.R.styleable.Preference_android_key /*6*/:
                    MainActivity.this.addWidget(true);
                    break;
                case android.support.v7.preference.R.styleable.Preference_android_summary /*7*/:
                    MainActivity.this.checkLaunchPointPositions();
                default:
            }
        }
    }

    /* renamed from: MainActivity.4 */
    class C01614 implements NotificationRowListener {
        C01614() {
        }

        public void onBackgroundImageChanged(String imageUri, boolean notifActive) {
            Log.i(TAG, "onBackgroundImageChanged");
            if (MainActivity.this.mWallpaper != null) {
                MainActivity.this.mWallpaper.onBackgroundImageChanged(imageUri, notifActive);
            }
        }

        public void onSelectedRecommendationChanged(int position) {
            Log.i(TAG, "onSelectedRecommendationChanged->position:" + position);
            if (MainActivity.this.mKeepUiReset && !MainActivity.this.mAccessibilityManager.isEnabled() && position > 0) {
                MainActivity.this.mNotificationsView.setSelectedPositionSmooth(0);
            }
        }
    }

    /* renamed from: MainActivity.5 */
    class C01625 extends BroadcastReceiver {
        C01625() {
        }

        public void onReceive(Context context, Intent intent) {
            Uri packageName = null;
            if (intent != null) {
                packageName = intent.getData();
            }
            if (packageName != null && packageName.toString().contains("com.googleandroidtv.recommendations")) {
                Log.d("LeanbackLauncher", "Recommendations Service updated, reconnecting");
                if (MainActivity.this.mHomeAdapter != null) {
                    MainActivity.this.mHomeAdapter.onReconnectToRecommendationsService();
                }
            }
        }
    }

    /* renamed from: MainActivity.6 */
    class C01656 implements OnHierarchyChangeListener {
        final /* synthetic */ NotificationsAdapter val$recAdapter;

        /* renamed from: MainActivity.6.1 */
        class C01631 implements HomeScreenMessaging.ChangeListener {
            C01631() {
            }

            public void onStateChanged(int state) {
                MainActivity.this.mHandler.sendMessageDelayed(MainActivity.this.mHandler.obtainMessage(4, state, 0), 500);
            }
        }

        /* renamed from: MainActivity.6.2 */
        class C01642 implements SearchOrbView.SearchLaunchListener {
            C01642() {
            }

            public void onSearchLaunched() {
                MainActivity.this.setShyMode(true, true);
            }
        }

        C01656(NotificationsAdapter val$recAdapter) {
            this.val$recAdapter = val$recAdapter;
        }

        public void onChildViewAdded(View parent, View child) {
            int tag = 0;
            if (child.getTag() instanceof Integer) {
                tag = ((Integer) child.getTag()).intValue();
            }
            switch (tag) {
                case android.support.v7.preference.R.styleable.Preference_android_icon /*0*/:
                    if (child instanceof SearchOrbView) {
                        updateNetWorkState();
                        ((SearchOrbView) child).setLaunchListener(new C01642());
                    }
                    MainActivity.this.addWidget(false);
                    break;
                case android.support.v7.recyclerview.R.styleable.RecyclerView_android_descendantFocusability /*1*/:
                case android.support.v7.recyclerview.R.styleable.RecyclerView_layoutManager /*2*/:
                    MainActivity.this.mHomeScreenView = (HomeScreenView) child.findViewById(R.id.home_screen_messaging);
                    if (MainActivity.this.mHomeScreenView != null) {
                        HomeScreenMessaging homeScreenMessaging = MainActivity.this.mHomeScreenView.getHomeScreenMessaging();
                        if (tag == 1) {
                            this.val$recAdapter.setNotificationRowViewFlipper(homeScreenMessaging);
                            MainActivity.this.mNotificationsView = MainActivity.this.mHomeScreenView.getNotificationRow();
                            if (MainActivity.this.mNotificationsView != null) {
                                MainActivity.this.mNotificationsView.setListener(MainActivity.this.mNotifListener);
                            }
                        }
                        homeScreenMessaging.setListener(new C01631());
                        break;
                    }
                    break;
            }
            if ((child instanceof IdleListener) && !MainActivity.this.mIdleListeners.contains(child)) {
                MainActivity.this.addIdleListener((IdleListener) child);
            }
        }

        public void onChildViewRemoved(View parent, View child) {
        }
    }

    /* renamed from: MainActivity.7 */
    class C01667 extends OnScrollListener {
        C01667() {
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            MainActivity.this.mScrollManager.onScrolled(dy, MainActivity.this.getCurrentScrollPos());
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            MainActivity.this.mScrollManager.onScrollStateChanged(newState);
        }
    }

    /* renamed from: MainActivity.8 */
    class C01678 implements AnimatorLifecycle.OnAnimationFinishedListener {
        C01678() {
        }

        public void onAnimationFinished() {
            MainActivity.this.mList.setVisibility(View.GONE);
        }
    }

    /* renamed from: MainActivity.9 */
    class C01689 implements OnGlobalLayoutListener {
        C01689() {
        }

        public void onGlobalLayout() {
            MainActivity.this.mList.getRootView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
            if (MainActivity.this.mLaunchAnimation.isScheduled()) {
                MainActivity.this.mLaunchAnimation.prime();
            }
        }
    }

    public interface IdleListener {
        void onIdleStateChange(boolean z);

        void onVisibilityChange(boolean z);
    }

    public MainActivity() {

        this.mRefreshHomeAdapter = new C01581();
        this.mMoveTaskToBack = new C01592();
        this.mLaunchAnimation = new AnimatorLifecycle();
        this.mEditModeAnimation = new AnimatorLifecycle();
        this.mPauseAnimation = new AnimatorLifecycle();
        this.mIdleListeners = new ArrayList();
        this.mUserInteracted = false;
        this.mHandler = new C01603();
        this.mNotifListener = new C01614();
        this.mPackageReplacedReceiver = new C01625();
    }
    public void initializeStetho() {
        Stetho.InitializerBuilder initializerBuilder = Stetho.newInitializerBuilder(MainActivity.this);
        initializerBuilder.enableWebKitInspector(Stetho.defaultInspectorModulesProvider(MainActivity.this));
        initializerBuilder.enableDumpapp(Stetho.defaultDumperPluginsProvider(MainActivity.this));
        Stetho.Initializer initializer = initializerBuilder.build();
        Stetho.initialize(initializer);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // OnCreateStuff();
        if(NetWorkUtils.isNetworkAvailable()) {
            GetAllCategories();
        }else{
        //    OnCreateStuff();
        }
        mIsFirstLoad = true;
        mTimerHandler = new Handler();
        if (this.mRecommendationsAdapter == null) {
            this.mRecommendationsAdapter = new NotificationsAdapter(this);
        }
        Context appContext = getApplicationContext();
        DrawableDownloader.getInstance(appContext);
        setContentView(R.layout.activity_main);
        initializeStetho();
        mContext = MainActivity.this;
        this.mAccessibilityManager = (AccessibilityManager) getSystemService("accessibility");
        this.mAppsRanker = AppsRanker.getInstance(this);
        this.mLaunchPointListGenerator = new LaunchPointListGenerator(this);
        mLayoutLading = (RelativeLayout)findViewById(R.id.layout_loading);
        mAllAppGridView = (AllAppGridView)findViewById(R.id.grid_all_app);
        this.mEditModeView = (EditModeView) findViewById(R.id.edit_mode_view);
        this.mEditModeView.setUninstallListener(this);
        this.mWallpaper = (LauncherWallpaper) findViewById(R.id.background_container);
        this.mAppWidgetManager = AppWidgetManager.getInstance(appContext);
        this.mAppWidgetHost = new AppWidgetHost(this, 123);
        this.mList = (VerticalGridView) findViewById(R.id.main_list_view);
        this.mList.setHasFixedSize(true);
        this.mList.setWindowAlignment(1);
        this.mList.setWindowAlignmentOffset(getResources().getDimensionPixelOffset(R.dimen.home_screen_selected_row_alignment));
        this.mList.setWindowAlignmentOffsetPercent(-1.0f);
        this.mList.setItemAlignmentOffset(0);
        this.mList.setItemAlignmentOffsetPercent(-1.0f);
        this.mScrollManager = new HomeScrollManager(this, this.mList);
        this.mScrollManager.addHomeScrollListener(this.mWallpaper);
        this.mHomeAdapter = new HomeScreenAdapter(this, this.mScrollManager, this.mLaunchPointListGenerator, this.mRecommendationsAdapter, this.mEditModeView, this.mAppsRanker);
        this.mHomeAdapter.setOnEditModeChangedListener(this);
        Log.i(TAG, "onCreate->HomeAdapter->size:" + mHomeAdapter.getItemCount());
        this.mList.setItemViewCacheSize(this.mHomeAdapter.getItemCount());
        this.mList.setAdapter(this.mHomeAdapter);
        this.mList.setOnChildSelectedListener(this.mHomeAdapter);
        this.mList.setAnimateChildLayout(false);
        int notifIndex = this.mHomeAdapter.getRowIndex(1);
        if (notifIndex != -1) {
            this.mList.setSelectedPosition(notifIndex);
        }
        NotificationsAdapter recAdapter = this.mHomeAdapter.getRecommendationsAdapter();
        addIdleListener(recAdapter);
        this.mList.setOnHierarchyChangeListener(new C01656(recAdapter));
        this.mList.addOnScrollListener(new C01667());
        this.mShyMode = true;
        setShyMode(!this.mShyMode, true);
        this.mIdlePeriod = getResources().getInteger(R.integer.idle_period);
        this.mResetPeriod = getResources().getInteger(R.integer.reset_period);
        this.mFadeDismissAndSummonAnimations = getResources().getBoolean(R.bool.app_launch_animation_fade);
        this.mKeepUiReset = true;
        this.mHomeAdapter.onInitUi();
        //this.mGoogleApiClient = new Builder(appContext).addApi(ClearcutLogger.API).build();
        this.mEventLogger = LeanbackLauncherEventLogger.getInstance(appContext);
        //this.mEventLogger.setGoogleApiClient(this.mGoogleApiClient);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.PACKAGE_REPLACED");
        filter.addDataScheme("package");
        registerReceiver(this.mPackageReplacedReceiver, filter);
    }

    private void OnCreateStuff() {
        this.mHomeAdapter = new HomeScreenAdapter(this, this.mScrollManager, this.mLaunchPointListGenerator, this.mRecommendationsAdapter, this.mEditModeView, this.mAppsRanker);

    }

    public void startNewActivity(String CatID){
        Intent intent = new Intent(MainActivity.this,VerticalGridActivity.class);
        intent.putExtra("CatID",CatID);
        startActivity(intent);
        //startActivity(new Intent(MainActivity.this,DummyActivity.class));
    }

    private void GetAllCategories() {
        RequestParams params = new RequestParams();
        String Url ="all-category.php";
        AsyncLoopj.get(Url,params,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject jsonObj = new JSONObject(response.toString());
                    if(jsonObj.getString("status").equals("1")){
                        Log.e("AsynchRes",response.toString());
                         CategotriesList = new Gson().fromJson(jsonObj.getString("result"), CategoryBean[].class);
                    //    AddCategories();
                     //   setRowsWithCat();
                    }else{
                        //failure response

                    }
                //    OnCreateStuff();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("AsynchRes",responseString);
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

    public void onDestroy() {
        this.mHandler.removeMessages(3);
        mTimerHandler.removeCallbacks(this);
        super.onDestroy();
        if (this.mHomeAdapter != null) {
            this.mHomeAdapter.onStopUi();
            this.mHomeAdapter.unregisterReceivers();
        }
        if (this.mAppsRanker != null) {
            this.mAppsRanker.unregisterListeners();
        }
        unregisterReceiver(this.mPackageReplacedReceiver);
    }

    public void onUserInteraction() {
        this.mHandler.removeMessages(3);
        this.mKeepUiReset = false;
        if (hasWindowFocus()) {
            this.mHandler.removeMessages(1);
            this.mUserInteracted = true;
            if (this.mIsIdle) {
                this.mHandler.sendEmptyMessage(2);
            }
            this.mHandler.sendEmptyMessageDelayed(1, (long) this.mIdlePeriod);
        }
        this.mHandler.sendEmptyMessageDelayed(3, (long) this.mResetPeriod);
    }

    private void addIdleListener(IdleListener listener) {
        this.mIdleListeners.add(listener);
        listener.onVisibilityChange(true);
        listener.onIdleStateChange(this.mIsIdle);
    }

    public void onBackPressed() {
        if (this.mAppEditMode) {
            getEditModeView().onBackPressed();
        } else if (this.mLaunchAnimation.isRunning()) {
            this.mLaunchAnimation.cancel();
        } else if (this.mLaunchAnimation.isPrimed()) {
            this.mLaunchAnimation.reset();
        } else {
            if (this.mLaunchAnimation.isFinished()) {
                this.mLaunchAnimation.reset();
            }
            dismissLauncher();
        }
    }

    public void onBackgroundVisibleBehindChanged(boolean visible) {
        boolean z;
        if (visible) {
            z = false;
        } else {
            z = true;
        }
        setShyMode(z, true);
    }

    public void onEditModeChanged(boolean editMode) {
        Log.i(TAG, "onEditModeChanged->editMode:" + editMode);
        Log.i(TAG, "onEditModeChanged->mAccessibilityManager->isEnabled:" + mAccessibilityManager.isEnabled());
        if (this.mAppEditMode == editMode) {
            return;
        }
        setEditMode(editMode, false);
     /*   if (this.mAccessibilityManager.isEnabled()) {
            setEditMode(editMode, false);
        } else {
            setEditMode(editMode, true);
        }*/
    }

    public void onUninstallPressed(String packageName) {
        if (packageName != null) {
            Intent uninstallIntent = new Intent("android.intent.action.UNINSTALL_PACKAGE", Uri.parse("package:" + packageName));
            uninstallIntent.putExtra("android.intent.extra.RETURN_RESULT", true);
            startActivityForResult(uninstallIntent, 321);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 321 && resultCode != 0) {
            if (resultCode == -1) {
                this.mEditModeView.uninstallComplete();
            } else if (resultCode == 1) {
                this.mEditModeView.uninstallFailure();
            }
        }
    }

    private boolean setShyMode(boolean shy, boolean changeWallpaper) {
        boolean changed = false;
        if (this.mShyMode != shy) {
            this.mShyMode = shy;
            changed = true;
            /*
            if (this.mShyMode) {
                convertFromTranslucent();
            } else {
                convertToTranslucent(null, null);
            }*/
        }
        if (changeWallpaper && this.mWallpaper.getShynessMode() != shy) {
            this.mWallpaper.setShynessMode(this.mShyMode);
            if (this.mShyMode && this.mNotificationsView != null) {
                this.mNotificationsView.refreshSelectedBackground();
            }
        }
        return changed;
    }

    private boolean dismissLauncher() {
        if (this.mShyMode) {
            return false;
        }
        this.mLaunchAnimation.init(new LauncherDismissAnimator(this.mList, this.mFadeDismissAndSummonAnimations, this.mHomeAdapter.getRowHeaders()), this.mMoveTaskToBack, (byte) 0);
        this.mLaunchAnimation.start();
        return true;
    }

    public void onNewIntent(Intent intent) {
        boolean exitingEditMode = false;
        if (this.mAppEditMode) {
            if (Util.isInTouchExploration(getApplicationContext())) {
                setTitle(R.string.app_label);
            }
            setEditMode(false, true);
            exitingEditMode = true;
        }
        if (this.mLaunchAnimation.isRunning()) {
            this.mLaunchAnimation.cancel();
            return;
        }
        if (this.mLaunchAnimation.isPrimed()) {
            this.mLaunchAnimation.reset();
        }
        if (this.mLaunchAnimation.isFinished()) {
            this.mLaunchAnimation.reset();
        }
        if (!exitingEditMode) {
            if (intent.getExtras() != null) {
                if (intent.getExtras().getBoolean("extra_start_customize_apps")) {
                    startEditMode(3);
                } else if (intent.getExtras().getBoolean("extra_start_customize_games")) {
                    startEditMode(4);
                }
            }
            if (!this.mStartingEditMode) {
                if (!hasWindowFocus() || intent.getBooleanExtra("com.android.systemui.recents.tv.RecentsTvActivity.RECENTS_HOME_INTENT_EXTRA", false)) {
                    if (!this.mLaunchAnimation.isScheduled()) {
                        resetLauncherState(false);
                        this.mLaunchAnimation.init(new MassSlideAnimator.Builder(this.mList).setDirection(Direction.SLIDE_IN).setFade(this.mFadeDismissAndSummonAnimations).build(), this.mRefreshHomeAdapter, (byte) 32);
                    }
                } else if (!dismissLauncher()) {
                    resetLauncherState(true);
                }
            }
        } else if (!this.mLaunchAnimation.isInitialized() && !this.mLaunchAnimation.isScheduled()) {
            resetLauncherState(false);
            this.mLaunchAnimation.init(new MassSlideAnimator.Builder(this.mList).setDirection(Direction.SLIDE_IN).setFade(this.mFadeDismissAndSummonAnimations).build(), this.mRefreshHomeAdapter, (byte) 32);
        }
    }

    protected void startEditMode(int rowType) {
        if (Util.isInTouchExploration(getApplicationContext())) {
            int i;
            if (rowType == 3) {
                i = R.string.title_app_edit_mode;
            } else {
                i = R.string.title_game_edit_mode;
            }
            setTitle(i);
        }
        this.mStartingEditMode = true;
        this.mHomeAdapter.resetRowPositions(false);
        this.mLaunchAnimation.cancel();
        this.mList.setSelectedPosition(this.mHomeAdapter.getRowIndex(rowType));
        this.mHomeAdapter.prepareEditMode(rowType);
    }

    private void resetLauncherState(boolean smooth) {
        Log.i(TAG, "resetLauncherState");
        this.mScrollManager.onScrolled(0, 0);
        this.mUserInteracted = false;
        this.mHomeAdapter.resetRowPositions(smooth);
        if (this.mAppEditMode) {
            boolean z;
            if (smooth) {
                z = true;
            } else {
                z = false;
            }
            setEditMode(false, z);
        }
        int notifIndex = this.mHomeAdapter.getRowIndex(1);
        if (!(notifIndex == -1 || this.mList.getSelectedPosition() == notifIndex)) {
            if (smooth) {
                this.mList.setSelectedPositionSmooth(notifIndex);
            } else {
                this.mList.setSelectedPosition(notifIndex);
                View focusedChild = this.mList.getFocusedChild();
                int focusedPosition = this.mList.getChildAdapterPosition(focusedChild);
                if (!(focusedChild == null || focusedPosition == notifIndex)) {
                    focusedChild.clearFocus();
                }
            }
            if (!(this.mShyMode || this.mNotificationsView == null)) {
                this.mNotificationsView.setIgnoreNextActivateBackgroundChange();
            }
        }
        this.mLaunchAnimation.cancel();
    }

    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
        this.mResetAfterIdleEnabled = false;
        if (this.mAppWidgetHost != null) {
            this.mAppWidgetHost.startListening();
        }
        //setShyMode(!isBackgroundVisibleBehind(), true);
        this.mWallpaper.resetBackground();
        /*if (this.mHomeAdapter != null) {
            this.mHomeAdapter.refreshAdapterData();
        }*/
        if (this.mKeepUiReset) {
            resetLauncherState(false);
        }
        if (!this.mStartingEditMode) {
            if (!this.mLaunchAnimation.isInitialized()) {
                this.mLaunchAnimation.init(new LauncherReturnAnimator(this.mList, this.mLaunchAnimation.lastKnownEpicenter, this.mHomeAdapter.getRowHeaders(), this.mHomeScreenView), this.mRefreshHomeAdapter, (byte) 32);
            }
            this.mLaunchAnimation.schedule();
        }
        this.mStartingEditMode = false;
      /*  if (this.mGoogleApiClient != null) {
            this.mGoogleApiClient.connect();
        }*/
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    }

    protected void onSaveInstanceState(Bundle savedInstanceState) {
    }

    protected void onResume() {
        super.onResume();
        mActivityState = 1;
        Log.i(TAG, "onResume");
        startNetWorkListener();
        boolean backgroudVisible = (boolean) ReflectUtils.invokeMethod(getClass().getSuperclass(), this, "isBackgroundVisibleBehind", new Class[]{}, new Object[]{});
        boolean z = !backgroudVisible;
        boolean shyChanged = setShyMode(z, true);
        this.mHomeAdapter.sortRowsIfNeeded(this.mAppsRanker.checkIfResortingIsNeeded());
        //WallpaperInstaller.getInstance(this).installWallpaperIfNeeded();
        this.mWallpaper.setShynessMode(this.mShyMode);
        if (shyChanged) {
            this.mWallpaper.resetBackground();
        }
        if (this.mShyMode && this.mNotificationsView != null) {
            this.mNotificationsView.refreshSelectedBackground();
        }
        if (!this.mHandler.hasMessages(6)) {
            this.mHandler.sendEmptyMessage(6);
        }
        if (this.mHomeAdapter != null) {
            this.mHomeAdapter.animateSearchIn();
        }
        for (int i = 0; i < this.mIdleListeners.size(); i++) {
            Log.i(TAG, "mIdleListeners.get(i)->className:" + mIdleListeners.get(i).getClass().getName());
            ((IdleListener) this.mIdleListeners.get(i)).onVisibilityChange(true);
        }
        this.mResetAfterIdleEnabled = true;
        this.mHandler.removeMessages(3);
        this.mHandler.removeMessages(1);
        if (this.mIsIdle) {
            this.mHandler.sendEmptyMessage(2);
        } else {
            this.mHandler.sendEmptyMessageDelayed(1, (long) this.mIdlePeriod);
        }
        this.mHandler.sendEmptyMessageDelayed(3, (long) this.mResetPeriod);
        this.mHandler.sendEmptyMessageDelayed(7, 2000);
        if (this.mLaunchAnimation.isFinished()) {
            this.mLaunchAnimation.reset();
        }
        if (this.mLaunchAnimation.isInitialized()) {
            this.mLaunchAnimation.reset();
        }
        if (this.mLaunchAnimation.isScheduled()) {
            primeAnimationAfterLayout();
        }
        this.mPauseAnimation.reset();
        if (this.mAppEditMode) {
            new EditModeMassFadeAnimator(this, EditModeMassFadeAnimator.EditMode.ENTER).start();
        }
        if(mIsFirstLoad){
            mList.setVisibility(View.GONE);
            mIsFirstLoad = false;
            Log.i(TAG, "load app item start time:" + System.currentTimeMillis());
            mTimerHandler.post(this);
        }else{
            this.mList.setVisibility(View.VISIBLE);
        }
        overridePendingTransition(R.anim.home_fade_in_top, R.anim.home_fade_out_bottom);
        if (this.mHomeAdapter != null) {
            this.mHomeAdapter.onUiVisible();
        }
    }

    public void onEnterAnimationComplete() {
        if (this.mLaunchAnimation.isScheduled() || this.mLaunchAnimation.isPrimed()) {
            this.mLaunchAnimation.start();
        }
    }

    protected void onPause() {
        super.onPause();
        mActivityState = 0;
        Log.i(TAG, "onPause");
        stopNetWorkListener();
        this.mResetAfterIdleEnabled = false;
        this.mLaunchAnimation.cancel();
        this.mHandler.removeMessages(1);
        this.mHandler.removeMessages(6);
        this.mHandler.removeMessages(7);
        for (int i = 0; i < this.mIdleListeners.size(); i++) {
            ((IdleListener) this.mIdleListeners.get(i)).onVisibilityChange(false);
        }
        if (this.mAppEditMode) {
            new EditModeMassFadeAnimator(this, EditModeMassFadeAnimator.EditMode.EXIT).start();
        }
        this.mPauseAnimation.init(new LauncherPauseAnimator(this.mList), null, (byte) 0);
        this.mPauseAnimation.setOnAnimationFinishedListener(new C01678());
        this.mPauseAnimation.start();
        if (this.mHomeAdapter != null) {
            this.mHomeAdapter.onUiInvisible();
        }
    }

    protected void onStop() {
        this.mResetAfterIdleEnabled = true;
        if (this.mAppWidgetHost != null) {
            this.mAppWidgetHost.stopListening();
        }
        this.mHandler.removeCallbacksAndMessages(null);
        this.mHandler.sendEmptyMessageDelayed(3, (long) this.mResetPeriod);
        if (this.mAppEditMode) {
            setEditMode(false, false);
        }
        super.onStop();
        setShyMode(false, false);
        this.mHomeAdapter.sortRowsIfNeeded(false);
        this.mLaunchAnimation.reset();
        /*
        if (this.mGoogleApiClient != null) {
            boolean flushResult = this.mEventLogger.flush();
            this.mGoogleApiClient.disconnect();
        }*/
    }

    public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        super.dump(prefix, fd, writer, args);
        this.mAppsRanker.dump(prefix, writer);
        this.mLaunchAnimation.dump(prefix, writer, this.mList);
    }

    private int getCurrentScrollPos() {
        int pos = 0;
        int topView = -1;
        int i = 0;
        while (i < this.mList.getChildCount()) {
            View v = this.mList.getChildAt(i);
            if (v == null || v.getTop() > 0) {
                i++;
            } else {
                topView = this.mList.getChildAdapterPosition(v);
                if (v.getMeasuredHeight() > 0) {
                    pos = (int) ((((float) this.mHomeAdapter.getScrollOffset(topView)) * (((float) Math.abs(v.getTop())) / ((float) v.getMeasuredHeight()))) * -1.0f);
                }
                for (topView--; topView >= 0; topView--) {
                    pos -= this.mHomeAdapter.getScrollOffset(topView);
                }
                return pos;
            }
        }
        for (topView--; topView >= 0; topView--) {
            pos -= this.mHomeAdapter.getScrollOffset(topView);
        }
        return pos;
    }

    private void onNotificationRowStateUpdate(int state) {
        Log.i(TAG, "onNotificationRowStateUpdate");
        if (state == 1 || state == 2) {
            if (!this.mUserInteracted) {
                int searchIndex = this.mHomeAdapter.getRowIndex(0);
                if (searchIndex != -1) {
                    this.mList.setSelectedPosition(searchIndex);
                    this.mList.getChildAt(searchIndex).requestFocus();
                }
            }
        } else if (state == 0 && this.mList.getSelectedPosition() <= this.mHomeAdapter.getRowIndex(0) && this.mNotificationsView.getChildCount() > 0) {
            this.mNotificationsView.setSelectedPosition(0);
            this.mNotificationsView.getChildAt(0).requestFocus();
        }
    }

    public boolean onSearchRequested() {
        setShyMode(true, true);
        return super.onSearchRequested();
    }

    public static final boolean isMediaKey(int keyCode) {
        switch (keyCode) {
            case 79:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
            case 90:
            case 91:
            case 126:
            case 127:
            case 130:
                return true;
            default:
                return false;
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i(TAG, "onKeyDown");
        if( mAllAppGridView.getVisibility() == View.VISIBLE){
            if(keyCode == KeyEvent.KEYCODE_BACK){
                mAllAppGridView.setVisibility(View.GONE);
                int itemCount = mList.getChildCount();
                int selectedPosition = mList.getSelectedPosition();
                ArrayList<HomeScreenRow> visibleRows = mHomeAdapter.getVisRowList();
                for(int i = 0; i < itemCount; ++i){
                    View rowView = visibleRows.get(i).getRowView();
                    if(rowView instanceof ActiveFrame && i != selectedPosition){
                        rowView.setVisibility(View.VISIBLE);
                    }
                }
                //重新保存至数据库
                if(mAllLaunchPoints != null && mAllLaunchPoints.size() > 0){
                    List<LaunchPoint> selectLaunchPoints = new ArrayList<>();
                    for(LaunchPoint itemLaunchPoint : mAllLaunchPoints){
                        if(itemLaunchPoint.isRecommendApp())
                            selectLaunchPoints.add(itemLaunchPoint);
                    }
                    Log.i(TAG, "selectLaunchPoints->size:" + selectLaunchPoints.size());
                    AppInfoService appInfoService = new AppInfoService();
                    appInfoService.deleteByType(4);
                    appInfoService.saveByLaunchPoints(selectLaunchPoints, 4);
                }
                mHomeAdapter.refreshAdapterData();
                return true;
            }else if(keyCode == KeyEvent.KEYCODE_DPAD_UP){
                int numCols = mAllAppGridView.getNumColumns();
                int selectedPosition = mAllAppGridView.getSelectedItemPosition();
                if(selectedPosition / numCols == 0)
                    return  true;
            }else if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT){
                int selectedPosition = mAllAppGridView.getSelectedItemPosition();
                if(selectedPosition == mAllAppGridView.getAdapter().getCount() - 1)
                    return true;
            }

        }
        if (this.mLaunchAnimation.isPrimed() || this.mLaunchAnimation.isRunning() || this.mEditModeAnimation.isPrimed() || this.mEditModeAnimation.isRunning()) {
            Log.i(TAG, "onKeyDown 1");
            switch (keyCode) {
                case android.support.v7.preference.R.styleable.Preference_android_layout /*3*/:
                case android.support.v7.preference.R.styleable.Preference_android_title /*4*/:
                    return super.onKeyDown(keyCode, event);
                default:
                    Log.i(TAG, "return true");
                    return true;
            }
        } else if (this.mShyMode || !isMediaKey(event.getKeyCode())) {
            Log.i(TAG, "onKeyDown 2");
            return super.onKeyDown(keyCode, event);
        } else {
            Log.i(TAG, "onKeyDown 3");
            Log.i(TAG, "return true");
            return true;
        }
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (this.mShyMode || !isMediaKey(event.getKeyCode())) {
            return super.onKeyUp(keyCode, event);
        }
        switch (keyCode) {
            case 79:
            case 85:
            case 86:
            case 127:
                setShyMode(true, true);
                break;
        }
        return true;
    }

    private void addWidget(boolean refresh) {
        ViewGroup wrapper = (LinearLayout) findViewById(R.id.widget_wrapper);
        if (wrapper != null) {
            mLayoutNetworkContainer = (LinearLayout) wrapper.findViewById(R.id.layout_network_container);
            if (refresh || this.mAppWidgetHostView == null) {
                int childCount = wrapper.getChildCount();
                Log.i(TAG, "addWidget->childCount:" + childCount);
                wrapper.removeAllViews();
                wrapper.addView(mLayoutNetworkContainer);
                boolean z = false;
                int appWidgetId = Util.getWidgetId(this);
                ComponentName appWidgetComp = Partner.get(this).getWidgetComponentName();
                if (appWidgetComp != null) {
                    for (AppWidgetProviderInfo appWidgetInfo : this.mAppWidgetManager.getInstalledProviders()) {
                        if (appWidgetComp.equals(appWidgetInfo.provider)) {
                            if (appWidgetId != 0) {
                                z = true;
                            } else {
                                z = false;
                            }
                            if (z && !appWidgetComp.equals(Util.getWidgetComponentName(this))) {
                                clearWidget(appWidgetId);
                                z = false;
                            }
                            if (!z) {
                                int width = (int) getResources().getDimension(R.dimen.widget_width);
                                int height = (int) getResources().getDimension(R.dimen.widget_height);
                                Bundle options = new Bundle();
                                options.putInt("appWidgetMinWidth", width);
                                options.putInt("appWidgetMaxWidth", width);
                                options.putInt("appWidgetMinHeight", height);
                                options.putInt("appWidgetMaxHeight", height);
                                appWidgetId = this.mAppWidgetHost.allocateAppWidgetId();
                                z = this.mAppWidgetManager.bindAppWidgetIdIfAllowed(appWidgetId, appWidgetInfo.provider, options);
                            }
                            if (z) {
                                this.mAppWidgetHostView = this.mAppWidgetHost.createView(this, appWidgetId, appWidgetInfo);
                                this.mAppWidgetHostView.setAppWidget(appWidgetId, appWidgetInfo);
                                wrapper.addView(this.mAppWidgetHostView);
                                Util.setWidget(this, appWidgetId, appWidgetInfo.provider);
                            }
                        }
                    }
                }
                if (!z) {
                    clearWidget(appWidgetId);
                    wrapper.addView(LayoutInflater.from(this).inflate(R.layout.clock, null));
                }
                return;
            }
            ViewGroup parent = (ViewGroup) this.mAppWidgetHostView.getParent();
            if (parent != wrapper) {
                if (parent != null) {
                    parent.removeView(this.mAppWidgetHostView);
                }
                wrapper.removeAllViews();
                if(mLayoutNetworkContainer != null)
                    wrapper.addView(mLayoutNetworkContainer);
                wrapper.addView(this.mAppWidgetHostView);
            }
        }
    }

    private void clearWidget(int appWidgetId) {
        if (appWidgetId != 0) {
            this.mAppWidgetHost.deleteAppWidgetId(appWidgetId);
        }
        Util.clearWidget(this);
    }

    public LauncherWallpaper getWallpaperView() {
        return this.mWallpaper;
    }

    public EditModeView getEditModeView() {
        return this.mEditModeView;
    }

    public View getEditModeWallpaper() {
        return findViewById(R.id.edit_mode_background);
    }

    public boolean isInEditMode() {
        return this.mAppEditMode;
    }

    private void setEditMode(boolean editMode, boolean useAnimation) {
        float f = 1.0f;
        this.mEditModeAnimation.reset();
        if (useAnimation) {
            EditModeMassFadeAnimator.EditMode editMode2;
            if (editMode) {
                editMode2 = EditModeMassFadeAnimator.EditMode.ENTER;
            } else {
                editMode2 = EditModeMassFadeAnimator.EditMode.EXIT;
            }
            this.mEditModeAnimation.init(new EditModeMassFadeAnimator(this, editMode2), null, (byte) 0);
            this.mEditModeAnimation.start();
        } else {
            float f2;
            LauncherWallpaper launcherWallpaper = this.mWallpaper;
            if (editMode) {
                f2 = 0.0f;
            } else {
                f2 = 1.0f;
            }
            launcherWallpaper.setAlpha(f2);
            View editModeWallpaper = getEditModeWallpaper();
            if (!editMode) {
                f = 0.0f;
            }
            editModeWallpaper.setAlpha(f);
            getEditModeWallpaper().setVisibility(View.VISIBLE);
            this.mHomeAdapter.setRowAlphas(editMode ? 0 : 1);
        }
        if (!editMode && this.mAppEditMode) {
            for (int i = 0; i < this.mList.getChildCount(); i++) {
                View activeFrame = this.mList.getChildAt(i);
                if (activeFrame instanceof ActiveFrame) {
                    for (int j = 0; j < ((ActiveFrame) activeFrame).getChildCount(); j++) {
                        View activeItemsRow = ((ActiveFrame) activeFrame).getChildAt(j);
                        if (activeItemsRow instanceof ActiveItemsRowView) {
                            ((ActiveItemsRowView) activeItemsRow).setEditMode(false);
                        }
                    }
                }
            }
        }
        this.mAppEditMode = editMode;
    }

    public HomeScreenAdapter getHomeAdapter() {
        return this.mHomeAdapter;
    }

    private boolean checkFirstRunAfterBoot() {
        boolean firstRun;
        Intent dummyIntent = new Intent("android.intent.category.LEANBACK_LAUNCHER");
        dummyIntent.setClass(this, DummyActivity.class);
        if (PendingIntent.getActivity(this, 0, dummyIntent, 536870912) == null) {
            firstRun = true;
        } else {
            firstRun = false;
        }
        if (firstRun) {
            ((AlarmManager) getSystemService("alarm")).set(2, SystemClock.elapsedRealtime() + 711573504, PendingIntent.getActivity(this, 0, dummyIntent, 0));
        }
        return firstRun;
    }

    public void beginLaunchAnimation(View view, boolean translucent, int color, Runnable onCompleteCallback) {
        if (!this.mLaunchAnimation.isPrimed() && !this.mLaunchAnimation.isRunning() && !this.mLaunchAnimation.isFinished()) {
            getBoundsOnScreen(view, this.mLaunchAnimation.lastKnownEpicenter);
            if (translucent) {
                onCompleteCallback.run();
            } else {
                ForwardingAnimatorSet animation;
                if (view instanceof NotificationCardView) {
                    animation = new NotificationLaunchAnimator(this.mList, (NotificationCardView) view, this.mLaunchAnimation.lastKnownEpicenter, (ImageView) findViewById(R.id.click_circle_layer), color, this.mHomeAdapter.getRowHeaders(), this.mHomeScreenView);
                } else {
                    animation = new LauncherLaunchAnimator(this.mList, view, this.mLaunchAnimation.lastKnownEpicenter, (ImageView) findViewById(R.id.click_circle_layer), color, this.mHomeAdapter.getRowHeaders(), this.mHomeScreenView);
                }
                this.mLaunchAnimation.init(animation, onCompleteCallback, (byte) 0);
                this.mLaunchAnimation.start();
            }
        }
    }

    public boolean isLaunchAnimationInProgress() {
        return !this.mLaunchAnimation.isPrimed() ? this.mLaunchAnimation.isRunning() : true;
    }

    public boolean isEditAnimationInProgress() {
        return !this.mEditModeAnimation.isPrimed() ? this.mEditModeAnimation.isRunning() : true;
    }

    public void includeInLaunchAnimation(View target) {
        this.mLaunchAnimation.include(target);
    }

    public void includeInEditAnimation(View target) {
        this.mEditModeAnimation.include(target);
    }

    public void excludeFromLaunchAnimation(View target) {
        this.mLaunchAnimation.exclude(target);
    }

    public void excludeFromEditAnimation(View target) {
        this.mEditModeAnimation.exclude(target);
    }

    public void setOnLaunchAnimationFinishedListener(AnimatorLifecycle.OnAnimationFinishedListener l) {
        this.mLaunchAnimation.setOnAnimationFinishedListener(l);
    }

    private void primeAnimationAfterLayout() {
        this.mList.getRootView().getViewTreeObserver().addOnGlobalLayoutListener(new C01689());
        this.mList.requestLayout();
    }

    private static void getBoundsOnScreen(View v, Rect epicenter) {
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        epicenter.left = location[0];
        epicenter.top = location[1];
        epicenter.right = epicenter.left + Math.round(((float) v.getWidth()) * v.getScaleX());
        epicenter.bottom = epicenter.top + Math.round(((float) v.getHeight()) * v.getScaleY());
    }

    private void checkLaunchPointPositions() {
        if (!this.mLaunchAnimation.isRunning() && checkViewHierarchy(this.mList)) {
            StringWriter buf = new StringWriter();
            buf.append("Caught partially animated state; resetting...\n");
            this.mLaunchAnimation.dump("", new PrintWriter(buf), this.mList);
            Log.w("Animations", buf.toString());
            this.mLaunchAnimation.reset();
        }
    }

    private boolean checkViewHierarchy(View view) {
        if ((view instanceof ParticipatesInLaunchAnimation) && view.getTranslationY() != 0.0f) {
            return true;
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            int n = group.getChildCount();
            for (int i = 0; i < n; i++) {
                if (checkViewHierarchy(group.getChildAt(i))) {
                    return true;
                }
            }
        }
        return false;
    }

    private void updateNetWorkState(){
        int networkState = NetWorkUtils.getCurrentNetWrokState();
        Log.i(TAG, "updateNetWorkState->networkState");
        mImgEthernet = (ImageView) findViewById(R.id.img_ethernet);
        mImgWifi = (ImageView)findViewById(R.id.img_wifi);
        if(mImgWifi == null || mImgEthernet == null)
            return;
        if(networkState == ConstData.NetWorkState.NO){
            mImgEthernet.setVisibility(View.GONE);
            mImgWifi.setVisibility(View.GONE);
        }else if(networkState == ConstData.NetWorkState.WIFI){
            mImgWifi.setVisibility(View.VISIBLE);
            int wifiStrength = NetWorkUtils.getWifiStrength();
            int wifiImgId = getResources().getIdentifier("img_wifi_" + wifiStrength, "drawable", getPackageName());
            mImgWifi.setImageResource(wifiImgId);
            mImgEthernet.setVisibility(View.GONE);
        }else if(networkState == ConstData.NetWorkState.ETHERNET){
            mImgWifi.setVisibility(View.GONE);
            mImgEthernet.setVisibility(View.VISIBLE);
        }
    }
    private void startNetWorkListener(){
        updateNetWorkState();
        if(mNetChangeReceiver == null)
            mNetChangeReceiver = new NetWorkChangeReceiver();
        if(mNetChangeFilter == null){
            mNetChangeFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            mNetChangeFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        }

        registerReceiver(mNetChangeReceiver, mNetChangeFilter);
    }
    private void stopNetWorkListener(){
        unregisterReceiver(mNetChangeReceiver);
    }


    public void preformIconMoreClick(){
        int itemCount = mList.getChildCount();
        int selectedPosition = mList.getSelectedPosition();
        ArrayList<HomeScreenRow> visibleRows = mHomeAdapter.getVisRowList();
        for(int i = 0; i < itemCount; ++i){
            View rowView =  visibleRows.get(i).getRowView();
            if(rowView instanceof ActiveFrame && i != selectedPosition){
                rowView.setVisibility(View.GONE);

            }
        }
        mAllAppGridView.setVisibility(View.VISIBLE);
        mAllLaunchPoints = mLaunchPointListGenerator.getAllLaunchPoints();
        mAllAppGridAdapter = new AllAppGridAdapter(this, R.layout.adapter_item_allapp, mAllLaunchPoints);
        mAllAppGridView.setAdapter(mAllAppGridAdapter);
        mAllAppGridView.setOnItemClickListener(this);
        mAllAppGridView.setFocusable(true);
        mAllAppGridView.setFocusableInTouchMode(true);
        mAllAppGridView.requestFocus();

    }

    class NetWorkChangeReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "NetWorkChangeReceiver->onReceive");
            updateNetWorkState();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LaunchPoint selectItem = mAllAppGridAdapter.getItem(position);
        if(selectItem != null){
            boolean isRecommend = selectItem.isRecommendApp();
            isRecommend = !isRecommend;
            selectItem.setRecommendApp(isRecommend);
            CheckBox selectBox = (CheckBox) view.findViewById(R.id.check_select);
            selectBox.setChecked(isRecommend);
        }
    }

    @Override
    public void run() {
        if(mActivityState == 1){
            int itemCount = mHomeAdapter.getItemCount();
            Log.i(TAG, "run->itemCount:" + itemCount);
            ArrayList<HomeScreenRow> visibleRows = mHomeAdapter.getVisRowList();
            for(int i = 0; i < itemCount; ++i){
                Log.i(TAG, "run1");
                RecyclerView.Adapter<?> rowAdapter = visibleRows.get(i).getAdapter();
                if(rowAdapter instanceof AppsAdapter && ((AppsAdapter) rowAdapter).getAppType() ==0 && rowAdapter.getItemCount() > 0){
                    mLayoutLading.setVisibility(View.GONE);
                    mList.setVisibility(View.VISIBLE);
                    mTimerHandler.removeCallbacks(this);
                    Log.i(TAG, "load app item end time:" + System.currentTimeMillis());
                    Log.i(TAG, "run2");
                    return;
                }
            }
            mTimerHandler.postDelayed(this, 100);
        }else{
            mTimerHandler.postDelayed(this, 100);
        }
    }
}
