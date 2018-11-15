# RAD-Android
## RAD 
##### Podcast Analytics
Remote Audio Data is a framework for reporting the listenership of podcasts in Android apps using ExoPlayer.

### Getting Started

#### Project Setup

Add _implementation com.npr:rad:1.0.1_ to your module's gradle's dependencies.

#### Initialize the Framework

Call `Rad.with(Application application)` passing a reference to the application context.

_e.g.:_ call `Rad.with(this)` in the `onCreate()` method of the Android application subclass.

#### Configure the Framework

`Rad.getInstance().get/setBatchSize(int batchSize)` - configures the maximum number of events to be reported in a request

`Rad.getInstance().get/setUserAgent(String userAgent)` - configures the User-Agent header for http requests 

`Rad.getInstance().get/setExpirationTimeInterval(long millis)` - configures the maximum age of events to be reported

`Rad.getInstance().get/setSessionExpirationTimeInterval(long millis)` - configures the maximum age of a listening session

Alternatively, for initializing and configuring the framework call `Rad.with(Application application, Rad.Configuration config)` passing a `Rad.Configuration` object containing all of the above configuration

#### Monitoring Playback

Call `Rad.start(ExoPlayer player, DefaultTrackSelector selector)` passing an ExoPlayer object and a DefaultTrackSelector object. Method can be safely called multiple times with different player objects. The framework will monitor the last player object passed.

_e.g.:_ `TrackSelection.Factory trackSelectionFactory = new AdaptiveTrackSelection.Factory(new DefaultBandwidthMeter());`
            `DefaultTrackSelector trackSelector = new DefaultTrackSelector(trackSelectionFactory);`
            `ExoPlayer exoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector);`
            `Rad.start(exoPlayer, trackSelector);`
            `exoPlayer.setPlayWhenReady(true)`

#### Debugging the Framework

 For debug builds, call `Rad.printDataBase()` to print the contents of the SQLite database
 or use the debug listener for intercepting events within the framework:
      
     `Rad.getInstance().setDebugListener(new Rad.DebugListener() {`
                 `public void onMetadataChanged(String remoteAudioData) {}`
                 `public void onEventTriggered(String s) {}`
                 `public void onRequestSent(String s) {}`
             `});`
