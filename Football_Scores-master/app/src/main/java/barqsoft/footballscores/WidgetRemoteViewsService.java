package barqsoft.footballscores;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Build;
import android.text.TextUtils;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WidgetRemoteViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        return new RemoteViewsFactory() {

            private Cursor mData = null;

            @Override
            public void onCreate() { /* no code */ }

            @Override
            public void onDataSetChanged() {
                if (mData != null) {
                    mData.close();
                }
                long identityToken = Binder.clearCallingIdentity();
                mData = getContentResolver().query(DatabaseContract.BASE_CONTENT_URI, null, null, null, null);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (mData != null) {
                    mData.close();
                    mData = null;
                }
            }

            @Override
            public int getCount() {
                return mData == null ? 0 : mData.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION || mData == null || !mData.moveToPosition(position)) {
                    return null;
                }

                RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_scores_list_item);
                int homeGoals = mData.getInt(ScoresDBHelper.HOME_GOALS_COL_INDEX);
                int awayGoals = mData.getInt(ScoresDBHelper.AWAY_GOALS_COL_INDEX);
                String scores = Utilies.getScores(awayGoals, homeGoals);
                String homeName = mData.getString(ScoresDBHelper.HOME_COL_INDEX);
                String awayName = mData.getString(ScoresDBHelper.AWAY_COL_INDEX);
                String timeString = mData.getString(ScoresDBHelper.TIME_COL_INDEX);
                String dateString = mData.getString(ScoresDBHelper.DATE_COL_INDEX);

                String dateTimeString = timeString;
                if (!TextUtils.isEmpty(dateString)) {
                    try {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.date_format), Locale.getDefault());
                        Date date = simpleDateFormat.parse(dateString);
                        if (date != null) {
                            String dayName = Utilies.getDayName(date.getTime(), getString(R.string.today), getString(R.string.tomorrow), getString(R.string.yesterday));
                            dateTimeString = dayName + "\n" + timeString;
                        }
                    } catch (ParseException e) {
                        dateTimeString = dateString + "\n" + timeString;
                    }
                }

                views.setTextViewText(R.id.home_name, homeName);
                views.setTextViewText(R.id.away_name, awayName);
                views.setTextViewText(R.id.data_textview, dateTimeString);
                views.setTextViewText(R.id.score_textview, scores);
                views.setImageViewResource(R.id.home_crest, Utilies.getTeamCrestByTeamName(homeName));
                views.setImageViewResource(R.id.away_crest, Utilies.getTeamCrestByTeamName(awayName));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    String description;
                    if (homeGoals < 0 || awayGoals < 0) {
                        description = homeName + " versus " + awayName + ", " + dateTimeString;
                    } else {
                        description = homeName + " " + homeGoals + ", " + awayName + " " + awayGoals + ". " + dateTimeString;
                    }

                    setRemoteContentDescription(views, description);
                    views.setContentDescription(R.id.home_crest, getString(R.string.home_crest_description));
                    views.setContentDescription(R.id.away_crest, getString(R.string.away_crest_description));
                }

                return views;
            }

            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            private void setRemoteContentDescription(RemoteViews views, String description) {
                views.setContentDescription(R.id.widget_list_item, description);
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_scores_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (mData.moveToPosition(position)) {
                    return mData.getLong(ScoresDBHelper.MATCH_ID_COL_INDEX);
                }
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}