package cm.aptoide.pt.store.view;

import android.support.v4.app.Fragment;
import cm.aptoide.pt.app.view.ListAppsFragment;
import cm.aptoide.pt.dataprovider.model.v7.Event;
import cm.aptoide.pt.reviews.ListReviewsFragment;
import cm.aptoide.pt.store.view.ads.GetAdsFragment;
import cm.aptoide.pt.store.view.my.MyStoresFragment;
import cm.aptoide.pt.store.view.my.MyStoresSubscribedFragment;
import cm.aptoide.pt.store.view.recommended.RecommendedStoresFragment;

/**
 * Created by neuro on 03-01-2017.
 */

public class StoreTabFragmentChooser {

  private static FragmentProvider fragmentProvider = AptoideApplication.getFragmentProvider();

  public static Fragment choose(Event.Name name) {
    switch (name) {
      case listApps:
        return fragmentProvider.newListAppsFragment();
      case getStore:
      case getUser:
        return fragmentProvider.newGetStoreFragment();
      case getStoresRecommended:
        return fragmentProvider.newRecommendedStoresFragment();
      case getMyStoresSubscribed:
        return fragmentProvider.newMyStoresSubscribedFragment();
      case myStores:
        return fragmentProvider.newMyStoresFragment();
      case getStoreWidgets:
        return fragmentProvider.newGetStoreWidgetsFragment();
      case listReviews:
        return fragmentProvider.newListReviewsFragment();
      case getAds:
        return fragmentProvider.newGetAdsFragment();
      case listStores:
        return fragmentProvider.newListStoresFragment();
      default:
        throw new RuntimeException("Fragment " + name + " not implemented!");
    }
  }

  public static boolean validateAcceptedName(Event.Name name) {
    if (name != null) {
      switch (name) {
        case myStores:
        case getMyStoresSubscribed:
        case getStoresRecommended:
        case listApps:
        case getStore:
        case getUser:
        case getStoreWidgets:
        case getReviews:
          //case getApkComments:
        case getAds:
        case listStores:
        case listComments:
        case listReviews:
          return true;
      }
    }

    return false;
  }
}
