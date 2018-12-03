# RAD-Android

## RAD

##### Podcast Analytics

Remote Audio Data is a framework for reporting the listenership of podcasts in Android apps using ExoPlayer version 2.9.1.

If you want to view the RAD specification in more detail, please visit [this page](https://docs.google.com/document/d/14W1M3RaNfv-3mzY0paTs1A_uZ5fITSvWbpMbIikdHxk).

### Getting Started

#### Project Setup

Add _implementation org.npr:rad:1.0.5_ to your module's gradle's dependencies.

#### Initialize the Framework

Call `Rad.with(Application application)` passing a reference to the application context.

_e.g.:_ call `Rad.with(this)` in the `onCreate()` method of the Android application subclass.

#### Configure the Framework

`Rad.getInstance().get/setBatchSize(int batchSize)` - configures the maximum number of events to be reported in a request

`Rad.getInstance().get/setUserAgent(String userAgent)` - configures the User-Agent header for http requests

`Rad.getInstance().get/setExpirationTimeInterval(long millis)` - configures the maximum age of events to be reported

`Rad.getInstance().get/setSessionExpirationTimeInterval(long millis)` - configures the maximum age of a listening session

Alternatively, for initializing and configuring the framework call `Rad.with(Application application, Rad.Configuration config)` passing a `Rad.Configuration` object containing all of the above configuration. Please bear in mind that properties that were omitted in the configuration will be set to default, overriding previous values! The configuration was designed to be used when initializing the framework and the getter/setter pairs for each property were designed to change the behaviour of the framework after the initialization/configuration.

#### Monitoring Playback

Call `Rad.start(ExoPlayer player, DefaultTrackSelector selector)` passing an ExoPlayer object and a DefaultTrackSelector object. Method can be safely called multiple times with different player objects. The framework will monitor the last player object passed.

_e.g.:_

    `TrackSelection.Factory trackSelectionFactory = new AdaptiveTrackSelection.Factory(new DefaultBandwidthMeter());`
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
