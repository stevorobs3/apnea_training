package ru.megazlo.apnea;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.*;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.SystemService;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ru.megazlo.apnea.component.Utils;
import ru.megazlo.apnea.entity.RowState;
import ru.megazlo.apnea.entity.TableApnea;
import ru.megazlo.apnea.entity.TableApneaRow;
import ru.megazlo.apnea.receivers.DetailFragmentReceiver;
import ru.megazlo.apnea.receivers.EndCurrentReceiver;
import ru.megazlo.apnea.service.AlertService;
import ru.megazlo.apnea.service.ApneaService;
import ru.megazlo.apnea.receivers.ScreenOffReceiver;

@EService
public class ApneaForeService extends Service {

	private final static int ONGOING_NOTIFICATION_ID = 251665161;
	public static final String TABLE_RESTORE = "table_restore";
	public static final String IS_ALERT_SERIES_END = "is_alert_series_end";

	public static boolean RUNNING = false;

	@Bean
	AlertService alertService;
	@Bean
	ApneaService apneaService;
	@SystemService
	NotificationManager notificationManager;

	private int progress;
	private Notification.Builder builder;
	private Timer timer = new Timer();
	private TableApnea table;
	private TableApneaRow currentItem;
	private List<TableApneaRow> items;
	private ScreenOffReceiver receiver = new ScreenOffReceiver();

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// super.onStartCommand(intent, flags, startId);
		Log.i("Test", "Service: onCreate");
		table = (TableApnea) intent.getSerializableExtra("table");
		PendingIntent pi = getPendingIntent(MainAct_.class);

		builder = new Notification.Builder(getApplicationContext()).setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_ico))
				.setSmallIcon(R.drawable.ic_lungs).setContentTitle(getText(R.string.app_name)).setContentIntent(pi).setProgress(100, 0, false)
				.setOngoing(true).setAutoCancel(false).setWhen(System.currentTimeMillis());
		startForeground(ONGOING_NOTIFICATION_ID, builder.getNotification());

		if (table == null) {
			return -1;
			//throw new RuntimeException("not found table id");
		}
		items = apneaService.getRowsForTable(table);
		/*items = new ArrayList<>();
		final List<TableApneaRow> rowsForTable = apneaService.getRowsForTable(table);
		items.add(rowsForTable.get(rowsForTable.size() - 1));*/

		if (items == null || items.size() == 0) {
			throw new RuntimeException("table has no items");
		}

		currentItem = items.get(0);
		currentItem.setState(RowState.BREATHE);
		startTimer();
		registerReceiver(endReceiver, new IntentFilter(EndCurrentReceiver.ACTION_SKIP));
		registerReceiver(receiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
		return START_NOT_STICKY;
	}

	private void startTimer() {
		timer.scheduleAtFixedRate(new ApneaTimerTask(), 0, 1000);
		RUNNING = true;
		registerReceiver(receiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
	}

	private void stopTimer() {
		updateFragmentUi(true);
		unregisterReceiver(receiver);
		RUNNING = false;
		try {
			timer.cancel();
		} catch (Exception ignored) {
		}
	}

	@Override
	public void onDestroy() {
		stopTimer();
		unregisterReceiver(endReceiver);
		super.onDestroy();
		try {
			alertService.close();
		} catch (IOException ignored) {
		}
	}

	private int getCurrentMax() {
		return currentItem.getState() == RowState.BREATHE ? currentItem.getBreathe() : currentItem.getHold();
	}

	private TableApneaRow updateCurrentRow() throws EndCycleException {
		if (currentItem.getState() == RowState.BREATHE) {
			currentItem.setState(RowState.HOLD);
		} else if (currentItem.getState() == RowState.HOLD) {
			currentItem.setState(RowState.NONE);
			final int i = items.indexOf(currentItem);
			if (i == items.size() - 1) {
				currentItem.setState(RowState.NONE);
				throw new EndCycleException();
			} else {
				currentItem = items.get(i + 1);
				currentItem.setState(RowState.BREATHE);
			}
		}
		return currentItem;
	}

	private void updateProgress() {
		final int currentMax = getCurrentMax();
		if (progress >= currentMax) {
			progress = 0;
			try {
				updateCurrentRow();
			} catch (EndCycleException e) {
				onEndSeries();
				return;
			}
			alertService.sayState(currentItem.getState());
		}
		alertService.checkNotifications(currentMax - progress);
		updateNotificationUi();
		updateFragmentUi(false);
		progress++;
	}

	private void onEndSeries() {
		alertService.sayImOk();
		stopForeground(true);
		stopSelf();
		Intent mainInt = new Intent(getApplicationContext(), MainAct_.class);
		mainInt.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
		mainInt.putExtra(IS_ALERT_SERIES_END, true);
		startActivity(mainInt);
	}

	private void updateNotificationUi() {
		PendingIntent pi = getPendingIntent(this.getClass());
		final int currMax = getCurrentMax();
		final String arg = Utils.formatMS(currMax - progress);
		builder.setProgress(currMax, currMax - progress, false)/*.setContentIntent(pi)*/;
		if (currentItem.getState() == RowState.BREATHE) {
			builder.setContentText(getString(R.string.notif_breathe, arg));
		} else if (currentItem.getState() == RowState.HOLD) {
			builder.setContentText(getString(R.string.notif_hold, arg));
		}
		notificationManager.notify(ONGOING_NOTIFICATION_ID, builder.getNotification());
	}

	private void updateFragmentUi(boolean ended) {
		Intent tb = new Intent(DetailFragmentReceiver.ACTION_UPDATER);
		tb.putExtra(DetailFragmentReceiver.KEY_MAX, getCurrentMax());
		tb.putExtra(DetailFragmentReceiver.KEY_ENDED, ended);
		tb.putExtra(DetailFragmentReceiver.KEY_PROGRESS, progress);
		tb.putExtra(DetailFragmentReceiver.KEY_ROW, items.indexOf(currentItem));
		tb.putExtra(DetailFragmentReceiver.KEY_ROW_TYPE, currentItem.getState());
		tb.putExtra(DetailFragmentReceiver.KEY_ID, table.getId());
		getApplication().sendBroadcast(tb);
	}

	private PendingIntent getPendingIntent(Class clazz) {
		Intent intent = new Intent(getApplicationContext(), clazz);
		intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.putExtra(TABLE_RESTORE, table);
		return PendingIntent.getActivity(getApplicationContext(), 651651, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}

	private EndCurrentReceiver endReceiver = new EndCurrentReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			progress = getCurrentMax();
			updateProgress();
		}
	};

	class ApneaTimerTask extends TimerTask {
		@Override
		public void run() {
			updateProgress();
		}
	}

	class EndCycleException extends Exception {
	}
}
