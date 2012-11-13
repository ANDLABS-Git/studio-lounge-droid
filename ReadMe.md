##  * hAppy Log * 

# Studio-Lounge Android-Library
Multi Platform
Multi-Player
Multi-App

![http://andlabs.eu](http://andlabs.info/pix/android_andlabs_logo.png)

Licensed under the Apache License, Version 2.0

[API docs](http://andlabs-git.github.com/studio-lounge-droid/docs/index.html)

### Usage

  * include StudioLounge as [Android Library Project](http://developer.android.com/tools/projects/index.html)
  
  * register GCP background Service and LoungActivity in AndroidManifest.xml
  ```Xml
    <activity android:name="eu.andlabs.studiolounge.LoungeActivity" />
    <service android:name="eu.andlabs.studiolounge.gcp.GCPService" />
  ```
  Themes can be applied to the Lounge Activity

  * launch the Lounge Activity to visit the Player Lobby and Chat
  ```Java
    startActivity(new Intent(this, LoungeActivity.class));
  ```

  * add category "eu.andlabs.lounge" to the Game Intent Filter
  ```Xml
    <intent-filter>
      <action android:name="android.intent.action.MAIN" />
      <category android:name="eu.andlabs.lounge" />
    </intent-filter>
  ```
  This activity will be started once players joined for a game

  * add the permission 
  ```Xml
    <uses-permission android:name="android.permission.GET_ACCOUNTS"/>
  ```
  for authentication and
  ```Xml
    <uses-permission android:name="android.permission.INTERNET"/>
  ```
  for internet access. 

  * start, bind and unbind the GCP backround service
  ```Java
    @Override
    protected void onStart() { mLounge = GCPService.bind(this); }

    @Override
    protected void onStop() { GCPService.unbind(this, mLounge); }
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
