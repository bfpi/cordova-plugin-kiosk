package jk.cordova.plugin.kiosk;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import org.apache.cordova.*;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONException;
import jk.cordova.plugin.kiosk.KioskActivity;
import java.lang.Integer;
import java.util.HashSet;

public class KioskPlugin extends CordovaPlugin {

  public static final String EXIT_KIOSK = "exitKiosk";
  public static final String IS_IN_KIOSK = "isInKiosk";
  public static final String IS_SET_AS_LAUNCHER = "isSetAsLauncher";
  public static final String SET_ALLOWED_KEYS = "setAllowedKeys";

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    try {
      if (IS_IN_KIOSK.equals(action)) {

        callbackContext.success(Boolean.toString(KioskActivity.running));
        return true;

      } else if (IS_SET_AS_LAUNCHER.equals(action)) {

        String myPackage = cordova.getActivity().getApplicationContext().getPackageName();
        callbackContext.success(Boolean.toString(myPackage.equals(findLauncherPackageName())));
        return true;

      } else if (EXIT_KIOSK.equals(action)) {

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Intent chooser = Intent.createChooser(intent, "Select destination...");
        if (intent.resolveActivity(cordova.getActivity().getPackageManager()) != null) {
          cordova.getActivity().startActivity(chooser);
        }

        callbackContext.success();
        return true;

      } else if (SET_ALLOWED_KEYS.equals(action)) {

        System.out.println("setAllowedKeys: " + args.toString());
        HashSet<Integer> allowedKeys = new HashSet<Integer>();
        for (int i = 0; i < args.length(); i++) {
          allowedKeys.add(args.optInt(i));
        }
        KioskActivity.allowedKeys = allowedKeys;

        callbackContext.success();
        return true;
      }
      callbackContext.error("Invalid action");
      return false;
    } catch (Exception e) {
      System.err.println("Exception: " + e.getMessage());
      callbackContext.error(e.getMessage());
      return false;
    }
  }

  private String findLauncherPackageName() {
    final Intent intent = new Intent(Intent.ACTION_MAIN);
    intent.addCategory(Intent.CATEGORY_HOME);
    final ResolveInfo res = this.cordova.getActivity().getPackageManager().resolveActivity(intent, 0);
    return res.activityInfo.packageName;
  }
}
