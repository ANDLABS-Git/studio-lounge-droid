
##  * hAppy Log * 

# Studio-Lounge Android-App

![http://andlabs.eu](http://andlabs.info/pix/android_andlabs_logo.png)

### Usage

  * include StudioLounge as [Android Library Project](http://developer.android.com/tools/projects/index.html)
  
  * register GCP background Service and LoungActivity in AndroidManifest.xml
  ```Xml
    <activity android:name="eu.andlabs.studiolounge.LoungeMainActivity" />
    <service android:name="eu.andlabs.studiolounge.gcp.GCPService" />
  ```
  Themes can be applied to the Lounge Activity

  * launch the Lounge Activity to visit the Player Lobby and Chat

  ```Java
    Lounge.startLoungeActivity(this);
  ```

  * add category "eu.andlabs.lounge" to the Game Intent Filter
  ```Xml
    <intent-filter>
      <action android:name="android.intent.action.MAIN" />
      <category android:name="eu.andlabs.lounge" />
    </intent-filter>
  ```
  This activity will be started once players joined for a game

  * start, bind and unbind the GCP backround service
  ```Java
    @Override
    protected void onStart() { mLounge = new Lounge(this); }

    @Override
    protected void onStop() { unbindService(mLounge); }
  ```

  * send game messages
  ```Java
    Bundle data = new Bundle();
    data.putString("foo", "bar");
    mLounge.sendGameMessage(data)
```

* receive game messages
  ```Java
    mLounge.register(new GameMsgListener() {                
      @Override
      public void onMessageRecieved(Bundle msg) {
        msg.getString("foo") // returns "bar"
      }
    });
  ```
  Currently only String data is supported

  * register listeners to implement a custom Lounge experience
  ```Java
    mLounge.register( new LobbyListener() {...} );
    mLounge.register( new ChatListener() {...} );
  ```

### Sample Game
  [Moving Points](https://github.com/ANDLABS-Git/points)
