/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 24/05/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.store;

import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBodyWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.model.v7.store.GetStoreTabs;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by neuro on 22-04-2016.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GetStoreTabsRequest extends BaseRequestWithStore<GetStoreTabs, GetStoreTabsRequest.Body> {

	public GetStoreTabsRequest(String storeName, boolean bypassCache, OkHttpClient httpClient, Converter.Factory
			converterFactory) {
		super(storeName, bypassCache, new Body(), httpClient, converterFactory);
	}

	public GetStoreTabsRequest(long storeId, boolean bypassCache, OkHttpClient httpClient, Converter.Factory
			converterFactory) {
		super(storeId, bypassCache, new Body(), httpClient, converterFactory);
	}

	public static GetStoreTabsRequest of(String storeName, boolean bypassCache) {
		return new GetStoreTabsRequest(storeName, bypassCache, WebService.getDefaultHttpClient(), WebService.getDefaultConverter());
	}

	public static GetStoreTabsRequest of(int storeId, boolean bypassCache) {
		return new GetStoreTabsRequest(storeId, bypassCache, WebService.getDefaultHttpClient(), WebService.getDefaultConverter());
	}

	@Override
	protected Observable<GetStoreTabs> loadDataFromNetwork(Interfaces interfaces) {
		return interfaces.getStoreTabs(body, bypassCache);
	}

	@Data
	@Accessors(chain = true)
	@EqualsAndHashCode(callSuper = true)
	public static class Body extends BaseBodyWithStore {

		private String lang = Api.LANG;
	}
}
