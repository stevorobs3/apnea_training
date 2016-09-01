package ru.megazlo.apnea;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
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
import ru.megazlo.apnea.frag.TableDetailFragment;
import ru.megazlo.apnea.service.AlertService;
import ru.megazlo.apnea.service.ApneaService;

@EService
public class ApneaForeService extends Service {

    private final static int ONGOING_NOTIFICATION_ID = 251665161;

    public static boolean RUNNING = false;

    @Bean
    AlertService alertService;
    @Bean
    ApneaService apneaService;
    @SystemService
    NotificationManager notificationManager;

    private int progress;
    private Notification.Builder builder;
    private Timer timer;
    private TableApnea table;
    private TableApneaRow currentItem;
    private List<TableApneaRow> items;

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

        builder = new Notification.Builder(getApplication())
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_freediving))
                .setSmallIcon(R.drawable.ic_lungs)
                .setContentTitle(getText(R.string.app_name))
                .setContentIntent(pi)
                .setOngoing(true)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(false);
        startForeground(ONGOING_NOTIFICATION_ID, builder.getNotification());

        if (table == null) {
            return -1;
            //throw new RuntimeException("not found table id");
        }
        items = apneaService.getRowsForTable(table);
        if (items == null || items.size() == 0) {
            throw new RuntimeException("table has no items");
        }
        timer = new Timer();
        currentItem = items.get(0);
        currentItem.setState(RowState.BREATHE);
        timer.scheduleAtFixedRate(new ApneaTimerTask(), 0, 1000);
        RUNNING = true;
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        RUNNING = false;
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
                Log.w("Error", "updateProgress");
                alertService.sayImOk();
                stopForeground(true);
                stopSelf();
                return;
            }
            alertService.sayState(currentItem.getState());
        }
        alertService.checkNotifications(currentMax - progress);
        updateNotificationUi();
        updateFragmentUi();
        progress++;
    }

    private void updateNotificationUi() {
        PendingIntent pi = getPendingIntent(this.getClass());
        final String arg = Utils.formatMS(getCurrentMax() - progress);
        if (currentItem.getState() == RowState.BREATHE) {
            builder.setContentText(getString(R.string.notif_breathe, arg)).setContentIntent(pi);
        } else if (currentItem.getState() == RowState.HOLD) {
            builder.setContentText(getString(R.string.notif_hold, arg)).setContentIntent(pi);
        }
        notificationManager.notify(ONGOING_NOTIFICATION_ID, builder.build());
    }

    private void updateFragmentUi() {
        Intent data = new Intent(TableDetailFragment.DetailFragmentReceiver.KEY_UPDATER);
        data.putExtra("key_max", getCurrentMax());
        data.putExtra("key_progress", progress);
        data.putExtra("key_row", items.indexOf(currentItem));
        data.putExtra("key_row_type", currentItem.getState());
        getApplication().sendBroadcast(data);
    }

    private PendingIntent getPendingIntent(Class clazz) {
        final Intent intent = new Intent(getBaseContext(), clazz);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("table_restore", table);
        return PendingIntent.getActivity(getBaseContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    class ApneaTimerTask extends TimerTask {
        @Override
        public void run() {
            updateProgress();
        }
    }

    class EndCycleException extends Exception {
    }
}
