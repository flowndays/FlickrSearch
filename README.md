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

> A Defeat in the system is, all three roles in MVP depend on class [Photo], which, in a clean MVP, should better be restricted in M(or also in P at most). The reason of keeping this defeat is that this demo is simple, and users are not expected to add any status to the photos, so we don't need to transform the Photo class to a View Object.

#### View

MainActivity is the View of MVP, it contains a [ToolBar] and [RecyclerView] with [GridLayoutManager]. 

A SearchView inside the ToolBar is the entrance of searching function. It uses a [SearchRecentSuggestionsProvider] to provide search suggestions based on search histories. 

When user presses enter key in the soft keyboard, the SearchView sends an Intent with action Intent.ACTION_SEARCH to MainActivity, to avoid initialising multi instances, I made the launchMode of MainActivity singleTop.

The RecyclerView is wrapped by a [SwipeRefreshLayoutBottom], which is changed from [SwipeRefreshLayout] in Android source code. It is shown in the bottom insted of top of the screen, and applied to vertical-reversed animations. It provides auto fetching more feature to the RecyclerView.

#### Presenter
MainPresenter is the Presenter of MVP, it retrieves data from the Model and shows it in the View. 

To decouple the presenter and the View, I created an interface MainMvpView, which contains all methods the presenter should know about the View.

MainPresenter extends from Fragment, and calls "setRetainInstance(true)", to utilize the self-retained function over orientation changes. It also keeps all search result data.

#### Model
SearchService is an interface for Retrofit requests.

SuggestionProvider is a Content Provider which keeps and provids Search histories.


### Handling Orientation Changes
The target is to keep status over orientation changes without requiring android:configChanges in manifest. 

All the data/status are divided into two parts: 
* status of UI components in MainActivity
* data fetched from network

The first part is retained by saving to Bundle in onSaveInstanceState(), and retrived in onCreate(). The second part is automatically kept by the reatained Fragment. 

### Reference
Open source projects used:
* [Retrofit](https://github.com/square/retrofit)
* [Picasso](https://github.com/square/picasso)
* [RxAndroid](https://github.com/ReactiveX/RxAndroid)
* [RxJava](https://github.com/ReactiveX/RxJava)
* [RxBinding](https://github.com/JakeWharton/RxBinding)
* [Mockito](https://github.com/mockito/mockito)
* [robolectric](http://robolectric.org/)
* [SwipeRefreshLayoutBottom](https://github.com/JohannBlake/SwipeRefreshLayoutBottom)
