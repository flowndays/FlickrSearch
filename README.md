# FlickrSearch

### Build Environment
* Android Studio 2.2 Preview 2
* JRE: 1.8.0_76-release-b01 x86_64
* JVM: OpenJDK 64-Bit Server VM by JetBrains s.r.o

### Features
This app uses the Flickr image search API to search photos with keywords, and show them in a grid.

Endless scrolling, automatically requesting and displaying more images when the user scrolls to the bottom of the grid.

Search histories are recorded and shown as a dropdown input suggestion list. User can clear the histories from action menu.

Supports portrait and landscape, while in landscape, the grid has 4 columns and in portrait, it has 3 columns.

Supports different screen sizes.

### Tech Structure

This app implements a [MVP model](http://code.tutsplus.com/tutorials/an-introduction-to-model-view-presenter-on-android--cms-26162). MVP increases the separation of concerns and facilitates unit testing. The View(Activity) focuses on its lifecycle, and UI components, the presenter holds and manipulates all data, and connects to Model, and the Model handles network requests only. 

> One thing I'd like to mention is that all three roles in MVP depend on class [Photo]. For a product project which usually contains more features, we should separate the Model Object and View Object, and transform them in the Presenter.
> I kept it like this because in this demo users are not expected to add any status to [Photo] Objects, so an immutable class would work well.

#### View

MainActivity is the View of MVP, it contains a [ToolBar] and [RecyclerView] with [GridLayoutManager]. 

A SearchView inside the ToolBar is the entrance of searching function. It uses a [SearchRecentSuggestionsProvider] to provide search suggestions based on search histories. 

When user presses enter key in the soft keyboard, the SearchView sends an Intent with action Intent.ACTION_SEARCH to MainActivity, to avoid initialising multi instances, I made the launchMode of MainActivity singleTop.

The RecyclerView is wrapped by a [SwipeRefreshLayoutBottom], which is changed from [SwipeRefreshLayout] in Android source code. It is shown in the bottom instead of top of the screen, and applied to vertical-reversed animations. It provides auto fetching more feature to the RecyclerView.

Some basic entering animations are applied to grid items, and I used a fixed-size queue to make sure only the last few items will show animations.

#### Presenter
MainPresenter is the Presenter of MVP, it retrieves data from the Model and shows it in the View. 

To decouple the presenter and the View, I created an interface MainMvpView, which contains all methods the presenter should know about the View.

MainPresenter extends from Fragment, and calls "setRetainInstance(true)", to utilize the self-retained function over orientation changes. It also keeps all search result data.

#### Model
SearchService is an interface for Retrofit requests.

SuggestionProvider is a Content Provider which keeps and provides Search histories.


### Handling Orientation Changes
The target is to keep status over orientation changes without requiring android:configChanges in manifest. 

All the data/status are divided into two parts: 
* status of UI components in MainActivity
* data fetched from network

The first part is retained by saving to Bundle in onSaveInstanceState(), and retrieved in onCreate(). The second part is automatically kept by the retained Fragment.

### Reference
Open source projects used:
* [Retrofit](https://github.com/square/retrofit)
* [Picasso](https://github.com/square/picasso)
* [RxAndroid](https://github.com/ReactiveX/RxAndroid)
* [RxJava](https://github.com/ReactiveX/RxJava)
* [RxBinding](https://github.com/JakeWharton/RxBinding)
* [Mockito](https://github.com/mockito/mockito)
* [Robolectric](http://robolectric.org/)
* [SwipeRefreshLayoutBottom](https://github.com/JohannBlake/SwipeRefreshLayoutBottom)
