package jk.cordova.plugin.kiosk;

import android.app.ActionBar;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.cordova.*;

public class KioskActivity extends CordovaActivity {

  public static volatile boolean running = false;
  public static volatile Set<Integer> allowedKeys = Collections.EMPTY_SET;
  //private StatusBarManager statusBarManager;

  @Override
  protected void onStart() {
    super.onStart();
    System.out.println("KioskActivity started");
    running = true;
  }

  @Override
  protected void onStop() {
    super.onStop();
    System.out.println("KioskActivity stopped");
    running = false;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    super.init();

    if (running) {
      finish(); // prevent more instances of kiosk activity
    }

    loadUrl(launchUrl);

    // https://github.com/apache/cordova-plugin-statusbar/blob/master/src/android/StatusBar.java
    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

    // https://github.com/hkalina/cordova-plugin-kiosk/issues/14
    View decorView = getWindow().getDecorView();
    // Hide the status bar.
    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
    // Remember that you should never show the action bar if the
    // status bar is hidden, so hide that too if necessary.
    ActionBar actionBar = getActionBar();
    if (actionBar != null) {
      actionBar.hide();
    }

    /*
    try {
      android.util.Log.i("KioskActivity", "===========================================");
      android.util.Log.i("KioskActivity", "===========================================");
      android.util.Log.i("KioskActivity", "===========================================");

      Object service = getSystemService("statusbar");
      Class<?> statusBarManager = Class.forName("android.app.StatusBarManager");
      Field disableExpand = statusBarManager.getDeclaredField("DISABLE_EXPAND");
      android.util.Log.i("KioskActivity", "Expand: " + (int) disableExpand.get(statusBarManager));

      Method disableStatusBarb = statusBarManager.getMethod("disable", new Class[]{int.class});
      disableStatusBarb.invoke(service, (int) disableExpand.get(statusBarManager)); // StatusBarManager.DISABLE_EXPAND

      android.util.Log.i("KioskActivity", "DONE");
      android.util.Log.i("KioskActivity", "===========================================");
      android.util.Log.i("KioskActivity", "===========================================");
      android.util.Log.i("KioskActivity", "===========================================");
    } catch (NoSuchMethodException ex) {
      Logger.getLogger(KioskActivity.class.getName()).log(Level.SEVERE, null, ex);
    } catch (SecurityException ex) {
      Logger.getLogger(KioskActivity.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IllegalAccessException ex) {
      Logger.getLogger(KioskActivity.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IllegalArgumentException ex) {
      Logger.getLogger(KioskActivity.class.getName()).log(Level.SEVERE, null, ex);
    } catch (InvocationTargetException ex) {
      Logger.getLogger(KioskActivity.class.getName()).log(Level.SEVERE, null, ex);
    } catch (ClassNotFoundException ex) {
      Logger.getLogger(KioskActivity.class.getName()).log(Level.SEVERE, null, ex);
    } catch (NoSuchFieldException ex) {
      Logger.getLogger(KioskActivity.class.getName()).log(Level.SEVERE, null, ex);
    }
     */
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    //statusBarManager.disable(StatusBarManager.DISABLE_NONE);
  }

  @Override
  protected void onPause() {
    super.onPause();
    ActivityManager activityManager = (ActivityManager) getApplicationContext()
            .getSystemService(Context.ACTIVITY_SERVICE);
    activityManager.moveTaskToFront(getTaskId(), 0);
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    System.out.println("onKeyDown event: keyCode = " + event.getKeyCode());
    return !allowedKeys.contains(event.getKeyCode()); // prevent event from being propagated if not allowed
  }

  @Override
  public void finish() {
    System.out.println("Never finish...");
    // super.finish();
  }

  // http://www.andreas-schrade.de/2015/02/16/android-tutorial-how-to-create-a-kiosk-mode-in-android/
  /*
  @Override
  public void onWindowFocusChanged(boolean hasFocus) {
    super.onWindowFocusChanged(hasFocus);
    if (!hasFocus) {
      System.out.println("Focus lost - closing system dialogs");

      Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
      sendBroadcast(closeDialog);

      ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
      am.moveTaskToFront(getTaskId(), ActivityManager.MOVE_TASK_WITH_HOME);

      // sometime required to close opened notification area
      Timer timer = new Timer();
      timer.schedule(new TimerTask() {
        public void run() {
          Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
          sendBroadcast(closeDialog);
        }
      }, 500); // 0.5 second
    }
  }
   */
}
