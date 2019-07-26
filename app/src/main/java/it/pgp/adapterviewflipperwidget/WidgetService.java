package it.pgp.adapterviewflipperwidget;


import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.Date;


public class WidgetService extends RemoteViewsService {

	private static final String TAG = WidgetService.class.getSimpleName();

	final static int[] mLayoutIds = { R.layout.page1, R.layout.page2, R.layout.page3 };

	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		Log.d(TAG, "onGetViewFactory()");

		return new ViewFactory(intent);
	}

	private class ViewFactory implements RemoteViewsService.RemoteViewsFactory {
		
		private final int mInstanceId;
		private Date mUpdateDate = new Date();
		
		public ViewFactory(Intent intent) {
			mInstanceId = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID );
		}

		@Override
		public void onCreate() {
			Log.d(TAG, "onCreate()");

		}

		@Override
		public void onDataSetChanged() {
			Log.d(TAG, "onDataSetChanged()");

			mUpdateDate = new Date();
		}

		@Override
		public void onDestroy() {
			Log.d(TAG, "onDestroy()");
		}

		@Override
		public int getCount() {
			Log.d(TAG, "getCount() " + mLayoutIds.length);

			return mLayoutIds.length;
		}

		@Override
		public RemoteViews getViewAt(int position) {
			Log.d(TAG, "getViewAt()" + position);

			RemoteViews page = new RemoteViews(getPackageName(), mLayoutIds[position]);
			SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat
					.getDateTimeInstance();
			page.setTextViewText(R.id.update_date, sdf.format(mUpdateDate));

			return page;
		}

		@Override
		public RemoteViews getLoadingView() {
			Log.d(TAG, "getLoadingView()");

			return new RemoteViews(getPackageName(), R.layout.loading);
		}

		@Override
		public int getViewTypeCount() {
			Log.d(TAG, "getViewTypeCount()");

			return mLayoutIds.length;
		}

		@Override
		public long getItemId(int position) {
			Log.d(TAG, "getItemId()");

			return position;
		}

		@Override
		public boolean hasStableIds() {
			Log.d(TAG, "hasStableIds()");

			return true;
		}

	}

}
