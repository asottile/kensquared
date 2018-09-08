package com.anthonysottile.kenken.settings;

import android.content.SharedPreferences;

import com.anthonysottile.kenken.ui.UIConstants;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;

public final class SettingsProvider {

    private static final String GameSize = "GameSize";

    private static SharedPreferences preferences = null;

    private static int gameSize = UIConstants.MinGameSize;

    // TODO: implement dirty cache methodology

    public interface GameSizeChangedListener extends EventListener {
        void onGameSizeChanged(EventObject event);
    }

    private static final List<GameSizeChangedListener> gameSizeChangedListeners =
            new ArrayList<>();

    public static void AddGameSizeChangedListener(GameSizeChangedListener listener) {
        SettingsProvider.gameSizeChangedListeners.add(listener);
    }

    private static void triggerGameSizeChanged() {
        EventObject event = new EventObject(new Object());

        for (GameSizeChangedListener listener : SettingsProvider.gameSizeChangedListeners) {
            listener.onGameSizeChanged(event);
        }
    }

    /**
     * Returns the setting for the game size.
     *
     * @return The setting for the game size.
     */
    public static int GetGameSize() {
        return SettingsProvider.gameSize;
    }

    /**
     * Sets the game size setting.  Triggers game size changed if it changes.
     *
     * @param gameSize The size of the game to set.
     */
    public static void SetGameSize(int gameSize) {
        if (gameSize != SettingsProvider.gameSize) {
            SettingsProvider.gameSize = gameSize;
            SharedPreferences.Editor editor = SettingsProvider.preferences.edit();
            editor.putInt(SettingsProvider.GameSize, SettingsProvider.gameSize);
            editor.apply();

            SettingsProvider.triggerGameSizeChanged();
        }
    }

    /**
     * Initialization method to give the manager a reference to preferences.
     *
     * @param preferences A reference to the application preferences.
     */
    public static void Initialize(SharedPreferences preferences) {
        SettingsProvider.preferences = preferences;
        SettingsProvider.gameSize =
                SettingsProvider.preferences.getInt(
                        SettingsProvider.GameSize,
                        UIConstants.MinGameSize
                );
    }

    private SettingsProvider() {
    }
}
