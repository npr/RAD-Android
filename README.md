# RAD-Android

## RAD

##### Podcast Analytics

Remote Audio Data is a framework for reporting the listenership of podcasts in Android apps using ExoPlayer version 2.9.1.

If you want to view the RAD specification in more detail, please visit [this page](https://docs.google.com/document/d/14W1M3RaNfv-3mzY0paTs1A_uZ5fITSvWbpMbIikdHxk).

### Getting Started

#### Project Setup

Add _implementation org.npr:rad:1.0.11_ to your module's gradle's dependencies.

#### Initialize the Framework

Call `Rad.with(Application application)` passing a reference to the application context.

_e.g.:_ call `Rad.with(this)` in the `onCreate()` method of the Android application subclass.

#### Configure the Framework

`Rad.getInstance().get/setBatchSize(int batchSize)` - configures the maximum number of events to be reported in a request

`Rad.getInstance().get/setUserAgent(String userAgent)` - configures the User-Agent header for http requests

`Rad.getInstance().get/setExpirationTimeInterval(long millis)` - configures the maximum age of events to be reported

`Rad.getInstance().get/setSubmissionTimeInterval(long millis)` - configures the interval of time the framework will monitor for events before submitting them

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
             
#### Using RAD with custom versions of exoplayer

  The framework is build for exoplayer 2.9.1. To use the framwork with custom forks of exoplayer remove the exoplayer dependency from the build file, use Android Studio to add a new module with the custom version of exoplayer that you may have, and add the module as a dependency to the _app_ module. You may then build the framework with a custom version of exoplayer. If the version of exoplayer that was forked is lower than 2.9, in the body of the method:
  
  `private static Id3Frame parseInternalAttribute(ParsableByteArray data, int endPosition)`
  
  from `MetadatUtil.java` (line ~ 296), find the block of code:
  
  `if (!"com.apple.iTunes".equals(domain) || !"iTunSMPB".equals(name) || dataAtomPosition == -1) {
      // We're only interested in iTunSMPB.
      return null;
    }` and comment it out or remove it! 
    
  This early return statement prevents exoplayer versions 2.8.x from parsing and returning custom tags from mp4 files.

    
  
