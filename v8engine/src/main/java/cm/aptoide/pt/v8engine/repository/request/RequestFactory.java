package cm.aptoide.pt.v8engine.repository.request;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.repository.IdsRepository;
import cm.aptoide.pt.dataprovider.ws.v7.ListAppsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.ListFullReviewsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.V7EndlessController;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreWidgetsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.ListStoresRequest;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.v8engine.interfaces.StoreCredentialsProvider;

/**
 * Created by neuro on 26-12-2016.
 */

public class RequestFactory {

  private final ListStoresRequestFactory listStoresRequestFactory;
  private final ListAppsRequestFactory listAppsRequestFactory;
  private final ListFullReviewsRequestFactory listFullReviewsRequestFactory;
  private final GetStoreRequestFactory getStoreRequestFactory;
  private final GetStoreWidgetsRequestFactory getStoreWidgetsRequestFactory;

  public RequestFactory(IdsRepository idsRepository, AptoideAccountManager accountManager,
      StoreCredentialsProvider storeCredentialsProvider) {
    listStoresRequestFactory = new ListStoresRequestFactory(idsRepository, accountManager);
    listAppsRequestFactory = new ListAppsRequestFactory(accountManager, idsRepository);
    listFullReviewsRequestFactory =
        new ListFullReviewsRequestFactory(idsRepository, accountManager);
    getStoreRequestFactory =
        new GetStoreRequestFactory(idsRepository, accountManager, storeCredentialsProvider);
    getStoreWidgetsRequestFactory =
        new GetStoreWidgetsRequestFactory(idsRepository, accountManager, storeCredentialsProvider);
  }

  public ListStoresRequest newListStoresRequest(int offset, int limit) {
    return this.listStoresRequestFactory.newListStoresRequest(offset, limit);
  }

  public V7EndlessController<Store> listStores(int offset, int limit) {
    return this.listStoresRequestFactory.listStores(offset, limit);
  }

  public ListStoresRequest newListStoresRequest(String url) {
    return this.listStoresRequestFactory.newListStoresRequest(url);
  }

  public ListAppsRequest newListAppsRequest(String url) {
    return this.listAppsRequestFactory.newListAppsRequest(url);
  }

  public ListFullReviewsRequest newListFullReviews(String url, boolean refresh) {
    return this.listFullReviewsRequestFactory.newListFullReviews(url, refresh);
  }

  public GetStoreRequest newStore(String url) {
    return this.getStoreRequestFactory.newStore(url);
  }

  public GetStoreWidgetsRequest newStoreWidgets(String url) {
    return this.getStoreWidgetsRequestFactory.newStoreWidgets(url);
  }
}
