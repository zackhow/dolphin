package org.dolphinemu.dolphinemu.features.settings.ui;

import android.support.v4.app.FragmentActivity;

import org.dolphinemu.dolphinemu.features.settings.model.Setting;
import org.dolphinemu.dolphinemu.features.settings.model.Settings;
import org.dolphinemu.dolphinemu.features.settings.model.view.SettingsItem;

import java.util.ArrayList;

/**
 * Abstraction for a screen showing a list of settings. Instances of
 * this type of view will each display a layer of the setting hierarchy.
 */
public interface SettingsFragmentView
{
  /**
   * Called by the containing Activity to notify the Fragment that an
   * asynchronous load operation completed.
   *
   * @param settings The (possibly null) result of the ini load operation.
   */
  void onSettingsFileLoaded(Settings settings);

  /**
   * Pass a settings HashMap to the containing activity, so that it can
   * share the HashMap with other SettingsFragments; useful so that rotations
   * do not require an additional load operation.
   *
   * @param settings An ArrayList containing all the settings HashMaps.
   */
  void passSettingsToActivity(Settings settings);

  /**
   * Pass an ArrayList to the View so that it can be displayed on screen.
   *
   * @param settingsList The result of converting the HashMap to an ArrayList
   */
  void showSettingsList(ArrayList<SettingsItem> settingsList);

  /**
   * Called by the containing Activity when an asynchronous load operation fails.
   * Instructs the Fragment to load the settings screen with defaults selected.
   */
  void loadDefaultSettings();

  /**
   * @return The Fragment's containing activity.
   */
  FragmentActivity getActivity();

  /**
   * Tell the Fragment to tell the containing Activity to show a new
   * Fragment containing a submenu of settings.
   *
   * @param menuKey Identifier for the settings group that should be shown.
   */
  void loadSubMenu(MenuTag menuKey);

  /**
   * Tell the Fragment to tell the containing activity to display a toast message.
   *
   * @param message Text to be shown in the Toast
   */
  void showToastMessage(String message);

  /**
   * Have the fragment add a setting to the HashMap.
   *
   * @param setting The (possibly previously missing) new setting.
   */
  void putSetting(Setting setting);

  /**
   * Have the fragment tell the containing Activity that a setting was modified.
   */
  void onSettingChanged();

  /**
   * Have the fragment tell the containing Activity that a GCPad's setting was modified.
   *
   * @param menuTag Identifier for the GCPad that was modified.
   * @param value   New setting for the GCPad.
   */
  void onGcPadSettingChanged(MenuTag menuTag, int value);

  /**
   * Have the fragment tell the containing Activity that a Wiimote's setting was modified.
   *
   * @param menuTag Identifier for Wiimote that was modified.
   * @param value   New setting for the Wiimote.
   */
  void onWiimoteSettingChanged(MenuTag menuTag, int value);

  /**
   * Have the fragment tell the containing Activity that an extension setting was modified.
   *
   * @param menuTag Identifier for the extension that was modified.
   * @param value   New setting for the extension.
   */
  void onExtensionSettingChanged(MenuTag menuTag, int value);
}
