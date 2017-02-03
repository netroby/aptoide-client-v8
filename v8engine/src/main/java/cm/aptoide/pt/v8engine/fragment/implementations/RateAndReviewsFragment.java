/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import cm.aptoide.pt.v8engine.fragment.GridRecyclerFragment;

/**
 * Created by sithengineer on 13/05/16.
 */
public class RateAndReviewsFragment extends GridRecyclerFragment {

  /*
  private static final String TAG = RateAndReviewsFragment.class.getSimpleName();
  private static final String APP_ID = "app_id";
  private static final String PACKAGE_NAME = "package_name";
  private static final String STORE_NAME = "store_name";
  private static final String APP_NAME = "app_name";
  private static final String REVIEW_ID = "review_id";
  private static final String STORE_THEME = "store_theme";
  private long appId;
  private String packageName;
  private String storeName;
  private String storeTheme;
  private String appName;
  private long reviewId;
  private TextView emptyData;
  private Subscription subscription;
  private MenuItem installButton;
  private RatingTotalsLayout ratingTotalsLayout;
  private RatingBarsLayout ratingBarsLayout;
  private ProgressBar progressBar;
  private MenuItem installMenuItem;
  private FloatingActionButton floatingActionButton;
  private EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;

  private transient SuccessRequestListener<ListReviews> listFullReviewsSuccessRequestListener =
      listFullReviews -> {
        // TODO: 17/08/16 sithengineer paralelize this using Rx
        AptoideUtils.ThreadU.runOnIoThread(() -> {
          List<Review> reviews = listFullReviews.getDatalist().getList();
          List<Displayable> displayables = new LinkedList<>();
          CountDownLatch countDownLatch = new CountDownLatch(reviews.size());

          Observable.from(reviews)
              .forEach(fullReview -> ListCommentsRequest.of(fullReview.getComments().getView(),
                  fullReview.getId(), 3, storeName, StoreUtils.getStoreCredentials(storeName),
                  AptoideAccountManager.getInstance().getAccessToken(), AptoideAccountManager.getInstance().getUserEmail(),
                  new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
                      DataProvider.getContext()).getAptoideClientUUID()).execute(listComments -> {
                fullReview.setCommentList(listComments);
                countDownLatch.countDown();
              }, e -> countDownLatch.countDown()));

          try {
            countDownLatch.await(5, TimeUnit.SECONDS);
          } catch (InterruptedException e) {
            CrashReports.logString("ReviewID", String.valueOf(reviewId));
            CrashReport.getInstance().log(e);
            e.printStackTrace();
          }
          AptoideUtils.ThreadU.runOnUiThread(() -> {
            int index = -1;
            int count = 0;
            for (final Review review : reviews) {
              displayables.add(new RateAndReviewCommentDisplayable(
                  new RateAndReviewCommentDisplayable.ReviewWithAppName(appName, review),
                  new CommentAdder(count) {
                    @Override public void addComment(List<Comment> comments) {
                      List<Displayable> displayableList = new ArrayList<>();
                      createDisplayableComments(comments, displayableList);
                      int reviewPosition = getAdapter().getReviewPosition(reviewIndex);
                      if (comments.size() > 2) {
                        displayableList.add(createReadMoreDisplayable(reviewPosition, review));
                      }
                      getAdapter().addDisplayables(reviewPosition + 1, displayableList);
                    }

                    @Override public void collapseComments() {
                      ReviewsAndCommentsAdapter adapter = getAdapter();
                      int reviewIndex = adapter.getReviewPosition(this.reviewIndex);
                      int nextReview = adapter.getReviewPosition(this.reviewIndex + 1);
                      nextReview = nextReview == -1 ? getAdapter().getItemCount() : nextReview;
                      adapter.removeDisplayables(reviewIndex + 1, nextReview
                          - 1); // the -1 because we don't want to remove the next review,only until
                      // the
                      // comment
                      // before the review
                    }
                  }));

              if (review.getId() == reviewId) {
                index = count;
              }
              if (review.getCommentList() != null
                  && review.getCommentList().getDatalist() != null
                  && review.getCommentList().getDatalist().getLimit() != null) {
                createDisplayableComments(review.getCommentList().getDatalist().getList(),
                    displayables);
                if (review.getCommentList().getDatalist().getList().size() > 2) {
                  displayables.add(createReadMoreDisplayable(count, review));
                }
              }
              count++;
            }
            checkAndRemoveProgressBarDisplayable();
            addDisplayables(displayables);
            if (index >= 0) {
              getLayoutManager().scrollToPosition(getAdapter().getReviewPosition(index));
            }
          });
        });
      };

  public static RateAndReviewsFragment newInstance(long appId, String appName, String storeName,
      String packageName, String storeTheme) {
    RateAndReviewsFragment fragment = new RateAndReviewsFragment();
    Bundle args = new Bundle();
    args.putLong(APP_ID, appId);
    args.putString(APP_NAME, appName);
    args.putString(STORE_NAME, storeName);
    args.putString(PACKAGE_NAME, packageName);
    args.putString(STORE_THEME, storeTheme);
    fragment.setArguments(args);
    return fragment;
  }

  public static RateAndReviewsFragment newInstance(long appId, String appName, String storeName,
      String packageName, long reviewId) {
    RateAndReviewsFragment fragment = new RateAndReviewsFragment();
    Bundle args = new Bundle();
    args.putLong(APP_ID, appId);
    args.putString(APP_NAME, appName);
    args.putString(STORE_NAME, storeName);
    args.putString(PACKAGE_NAME, packageName);
    args.putLong(REVIEW_ID, reviewId);
    fragment.setArguments(args);
    return fragment;
  }

  @Override protected BaseAdapter createAdapter() {
    return new ReviewsAndCommentsAdapter();
  }

  @Override public void loadExtras(Bundle args) {
    super.loadExtras(args);
    appId = args.getLong(APP_ID);
    packageName = args.getString(PACKAGE_NAME);
    storeName = args.getString(STORE_NAME);
    appName = args.getString(APP_NAME);
    reviewId = args.getLong(REVIEW_ID, -1);
    storeTheme = args.getString(STORE_THEME);
  }

  @Override public void load(boolean create, boolean refresh, Bundle savedInstanceState) {
    Logger.d(TAG, "Other versions should refresh? " + create);
    fetchRating(refresh);
    fetchReviews();
  }

  @Override public int getContentViewId() {
    return R.layout.fragment_rate_and_reviews;
  }

  @Override public void bindViews(View view) {
    super.bindViews(view);
    floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fab);
    emptyData = (TextView) view.findViewById(R.id.empty_data);
    setHasOptionsMenu(true);

    ratingTotalsLayout = new RatingTotalsLayout(view);
    ratingBarsLayout = new RatingBarsLayout(view);
    progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);

    floatingActionButton.setOnClickListener(v -> {
      DialogUtils.showRateDialog(getActivity(), appName, packageName, storeName,
          this::fetchReviews);
    });
  }

  @Override public ReviewsAndCommentsAdapter getAdapter() {
    return (ReviewsAndCommentsAdapter) super.getAdapter();
  }

  @Override public void setupToolbar() {
    super.setupToolbar();
    if (toolbar != null) {
      ActionBar bar = ((AppCompatActivity) getActivity()).getSupportActionBar();
      bar.setDisplayHomeAsUpEnabled(true);
    }
  }

  @Override public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.menu_install, menu);
    installMenuItem = menu.findItem(R.id.menu_install);

    InstalledAccessor accessor = AccessorFactory.getAccessorFor(Installed.class);
    accessor.get(packageName).subscribe(installed -> {
      if (installed != null) {
        // app installed... update text
        installMenuItem.setTitle(R.string.open);
      }
    }, err -> {
      Logger.e(TAG, err);
      CrashReports.logException(err);
    });
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int itemId = item.getItemId();
    if (itemId == android.R.id.home) {
      getActivity().onBackPressed();
      return true;
    }
    if (itemId == R.id.menu_install) {
      getNavigationManager().navigateTo(V8Engine.getFragmentProvider()
          .newAppViewFragment(packageName, storeName, AppViewFragment.OpenType.OPEN_AND_INSTALL));
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void fetchRating(boolean refresh) {
    GetAppRequest.of(appId, AptoideAccountManager.getInstance().getAccessToken(),
        new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
            DataProvider.getContext()).getAptoideClientUUID()).execute(getApp -> {
      GetAppMeta.App data = getApp.getNodes().getMeta().getData();
      setupTitle(data.getName());
      setupRating(data);
      finishLoading();
    }, refresh);
  }

  private void setupRating(GetAppMeta.App data) {
    ratingTotalsLayout.setup(data);
    ratingBarsLayout.setup(data);
  }

  private void fetchReviews() {
    ListReviewsRequest of =
        ListReviewsRequest.of(storeName, packageName, AptoideAccountManager.getInstance().getAccessToken(),
            AptoideAccountManager.getInstance().getUserEmail(),
            new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
                DataProvider.getContext()).getAptoideClientUUID());

    endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(this.getAdapter(), of,
        listFullReviewsSuccessRequestListener, errorRequestListener);
    recyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);
    endlessRecyclerOnScrollListener.onLoadMore(false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    if (storeTheme != null) {
      ThemeUtils.setStatusBarThemeColor(getActivity(), StoreThemeEnum.get(storeTheme));
      ThemeUtils.setStoreTheme(getActivity(), storeTheme);
    }
  }

  @NonNull
  private CommentsReadMoreDisplayable createReadMoreDisplayable(final int count, Review review) {
    return new CommentsReadMoreDisplayable(review, review.getCommentList().getDatalist().getNext(),
        new CommentAdder(count) {
          @Override public void addComment(List<Comment> comments) {
            int nextReviewPosition = getAdapter().getReviewPosition(reviewIndex + 1);
            nextReviewPosition =
                nextReviewPosition == -1 ? getAdapter().getItemCount() : nextReviewPosition;
            getAdapter().removeDisplayable(nextReviewPosition - 1);
            List<Displayable> displayableList = new ArrayList<>();
            createDisplayableComments(comments, displayableList);
            getAdapter().addDisplayables(nextReviewPosition - 1, displayableList);
          }

          @Override public void collapseComments() {

          }
        });
  }

  private List<Displayable> createDisplayableComments(List<Comment> comments,
      List<Displayable> displayables) {
    for (final Comment comment : comments) {
      displayables.add(new CommentDisplayable(comment));
    }
    return displayables;
  }

  public void setupTitle(String title) {
    super.setupToolbar();
    if (toolbar != null) {
      ActionBar bar = ((AppCompatActivity) getActivity()).getSupportActionBar();
      bar.setTitle(title);
    }
  }

  private static class RatingTotalsLayout {

    private TextView usersVoted;
    private TextView ratingValue;
    private AppCompatRatingBar ratingBar;

    public RatingTotalsLayout(View view) {
      usersVoted = (TextView) view.findViewById(R.id.users_voted);
      ratingValue = (TextView) view.findViewById(R.id.rating_value);
      ratingBar = (AppCompatRatingBar) view.findViewById(R.id.rating_bar);
    }

    public void setup(GetAppMeta.App data) {
      GetAppMeta.Stats stats = data.getStats();
      usersVoted.setText(AptoideUtils.StringU.withSuffix(stats.getRating().getTotal()));
      ratingValue.setText(
          String.format(AptoideUtils.LocaleU.DEFAULT, "%.1f", stats.getRating().getAvg()));
      ratingBar.setRating(stats.getRating().getAvg());
    }
  }

  private static class RatingBarsLayout {

    private ProgressAndTextLayout[] progressAndTextLayouts;

    public RatingBarsLayout(View view) {
      progressAndTextLayouts = new ProgressAndTextLayout[5];
      progressAndTextLayouts[0] =
          new ProgressAndTextLayout(R.id.one_rate_star_progress, R.id.one_rate_star_count, view);
      progressAndTextLayouts[1] =
          new ProgressAndTextLayout(R.id.two_rate_star_progress, R.id.two_rate_star_count, view);
      progressAndTextLayouts[2] =
          new ProgressAndTextLayout(R.id.three_rate_star_progress, R.id.three_rate_star_count,
              view);
      progressAndTextLayouts[3] =
          new ProgressAndTextLayout(R.id.four_rate_star_progress, R.id.four_rate_star_count, view);
      progressAndTextLayouts[4] =
          new ProgressAndTextLayout(R.id.five_rate_star_progress, R.id.five_rate_star_count, view);
    }

    public void setup(GetAppMeta.App data) {
      GetAppMeta.Stats.Rating rating = data.getStats().getRating();
      final int total = rating.getTotal();
      for (final GetAppMeta.Stats.Rating.Vote vote : rating.getVotes()) {
        progressAndTextLayouts[vote.getValue() - 1].setup(total, vote.getCount());
      }
    }
  }

  private static class ProgressAndTextLayout {

    private ProgressBar progressBar;
    private TextView text;

    public ProgressAndTextLayout(int progressId, int textId, View view) {
      progressBar = (ProgressBar) view.findViewById(progressId);
      text = (TextView) view.findViewById(textId);
    }

    public void setup(int total, int count) {
      progressBar.setMax(total);
      progressBar.setProgress(count);
      text.setText(AptoideUtils.StringU.withSuffix(count));
    }
  }

  public static abstract class CommentAdder {

    final int reviewIndex;

    public CommentAdder(int reviewIndex) {
      this.reviewIndex = reviewIndex;
    }

    public abstract void addComment(List<Comment> comments);

    public abstract void collapseComments();
  }

  private void checkAndRemoveProgressBarDisplayable() {
    for (int i = 0; i < adapter.getItemCount(); i++) {
      Displayable displayable = adapter.getDisplayable(i);
      if (displayable instanceof ProgressBarDisplayable) {
        adapter.removeDisplayable(i);
        adapter.notifyItemRemoved(i);
      }
    }
  }
  */
}
