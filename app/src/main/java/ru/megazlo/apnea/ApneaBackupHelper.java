package ru.megazlo.apnea;

import android.app.backup.*;
import android.content.Context;

import ru.megazlo.apnea.db.DatabaseHelper;
import ru.megazlo.apnea.frag.SettingsFragment;

/** Created by iGurkin on 05.09.2016. */
public class ApneaBackupHelper extends BackupAgentHelper {

	public static boolean NEED_BACKUP = false;

	static final String PREFS_BACKUP_KEY = SettingsFragment.PREF_NAME + "Backup";
	static final String FILES_BACKUP_KEY = PREFS_BACKUP_KEY + "File";

	@Override
	public void onCreate() {
		SharedPreferencesBackupHelper pHelper = new SharedPreferencesBackupHelper(this, SettingsFragment.PREF_NAME);
		addHelper(PREFS_BACKUP_KEY, pHelper);

		FileBackupHelper fHelper = new FileBackupHelper(this, DatabaseHelper.DATABASE_NAME);
		addHelper(FILES_BACKUP_KEY, fHelper);
	}

	/** Сызываем сервис бекапа, когда что-то меняется */
	public static void requestBackup(Context context) {
		if (NEED_BACKUP) {
			BackupManager backupManager = new BackupManager(context);
			backupManager.dataChanged();
		}
	}
}
