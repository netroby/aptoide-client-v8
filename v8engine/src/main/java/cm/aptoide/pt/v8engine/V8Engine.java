/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/06/2016.
 */

package cm.aptoide.pt.v8engine;

import android.content.pm.PackageInfo;
import android.os.StrictMode;
import android.util.Log;

import com.squareup.leakcanary.LeakCanary;

import java.util.Collections;
import java.util.List;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.accountmanager.ws.responses.GetUserRepoSubscription;
import cm.aptoide.pt.database.Database;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.util.AptoideUtils;
import cm.aptoide.pt.dataprovider.ws.v7.listapps.StoreUtils;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.utils.SecurityUtils;
import cm.aptoide.pt.utils.SystemUtils;
import io.realm.Realm;
import lombok.Cleanup;

/**
 * Created by neuro on 14-04-2016.
 */
public abstract class V8Engine extends DataProvider {

	private static final String TAG = V8Engine.class.getName();

	public static void loadStores() {

		AptoideAccountManager.getUserRepos().subscribe(subscriptions -> {
			@Cleanup Realm realm = Database.get(getContext());
			for (GetUserRepoSubscription.Subscription subscription : subscriptions) {
				Store store = new Store();

				store.setDownloads(Long.parseLong(subscription.getDownloads()));
				store.setIconPath(subscription.getAvatarHd() != null ? subscription.getAvatarHd() : subscription
						.getAvatar());
				store.setStoreId(subscription.getId().longValue());
				store.setStoreName(subscription.getName());
				store.setTheme(subscription.getTheme());

				realm.beginTransaction();
				realm.copyToRealmOrUpdate(store);
				realm.commitTransaction();
			}
		});
	}

	public static void loadUserData() {
		loadStores();
	}

	public static void clearUserData() {
		clearStores();
	}

	private static void clearStores() {
		@Cleanup Realm realm = Database.get(V8Engine.getContext());
		realm.beginTransaction();
		realm.delete(Store.class);
		realm.commitTransaction();

		StoreUtils.subscribeStore(getConfiguration().getDefaultStore(), null, null);
	}

	@Override
	public void onCreate() {
		long l = System.currentTimeMillis();
		SystemUtils.context = this;

		if (BuildConfig.DEBUG) {
			setupStrictMode();
			Log.w(TAG, "StrictMode setup");
		}

		super.onCreate();

		if (BuildConfig.DEBUG) {
			LeakCanary.install(this);
			Log.w(TAG, "LeakCanary installed");
		}

		if (SecurePreferences.isFirstRun()) {
			loadInstalledApps();
			AptoideUtils.checkUpdates();

			if (AptoideAccountManager.isLoggedIn()) {
				if (!SecurePreferences.isUserDataLoaded()) {
					loadUserData();
					SecurePreferences.setUserDataLoaded();
				}
			}
		}

		final int validSignature = SecurityUtils.checkAppSignature(this);
		if (validSignature != SecurityUtils.VALID_APP_SIGNATURE) {
			Logger.e(TAG, "app signature is not valid!");
		}

		if (SecurityUtils.checkEmulator()) {
			Logger.w(TAG, "application is running on an emulator");
		}

		if (SecurityUtils.checkDebuggable(this)) {
			Logger.w(TAG, "application has debug flag active");
		}

		// just for curiosity...
		Logger.i(TAG, "facebook installed by: " + SecurityUtils.getInstallerPackageName(this, "com.facebook.katana"));
		Logger.i(TAG, "aptoide installed by: " + SecurityUtils.getInstallerPackageName(this, "cm.aptoide.pt"));
		Logger.i(TAG, "browser (system) installed by: " + SecurityUtils.getInstallerPackageName(this, "com.android" +
				".browser"));

		Logger.d(TAG, "onCreate took " + (System.currentTimeMillis() - l) + " millis.");
	}

	private void loadInstalledApps() {
		@Cleanup Realm realm = Database.get(this);
		Database.dropTable(Installed.class, realm);

		List<PackageInfo> installedApps = AptoideUtils.getUserInstalledApps();
		Log.d(TAG, "Found " + installedApps.size() + " user installed apps.");

		// Installed apps are inserted in database based on their firstInstallTime. Older comes first.
		Collections.sort(installedApps, (lhs, rhs) -> (int) ((lhs.firstInstallTime - rhs.firstInstallTime) / 1000));

		for (PackageInfo packageInfo : installedApps) {
			Installed installed = new Installed(packageInfo, getPackageManager());
			Database.save(installed, realm);
		}
	}

	private void setupStrictMode() {
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads()
				.detectDiskWrites()
				.detectNetwork()   // or .detectAll() for all detectable problems
				.penaltyLog()
				.build());

		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects()
				.detectLeakedClosableObjects()
				.penaltyLog()
				.penaltyDeath()
				.build());
	}
}
