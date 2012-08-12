package eu.andlabs.studiolounge;

import eu.andlabs.studiolounge.GameCommunicationService.ChatSession;
import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;

public class LoungeActivity extends Activity {

    private ServiceConnection mBinding;
    protected ChatSession mChatSession;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lounge);
        
        mBinding = new ServiceConnection() {
            
            @Override
            public void onServiceConnected(ComponentName name, IBinder b) {
                join(((GameCommunicationService.LocalBinder)b).getService());
            }
            
            @Override
            public void onServiceDisconnected(ComponentName name) {}
        };
        bindService(new Intent(this, GameCommunicationService.class),
                mBinding, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        unbindService(mBinding);
        super.onDestroy();
    }

    // here starts the fun
    protected void join(GameCommunicationService service) {
        // auto login
        service.joinLounge(service.new ChatSession("Guest") {
            
            @Override
            public void onJoin(String name) {
                chat("I love Andlabs");
            }
            
            @Override
            public void onChat(String msg) {
                // TODO Auto-generated method stub
                
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_lounge, menu);
        return true;
    }
}
