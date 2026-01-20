package it.marcosoft.ticketwave.ui.main;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import it.marcosoft.ticketwave.R;
import it.marcosoft.ticketwave.util.auth.SharedPreferencesUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferencesUtil.checkAndCreatePreferencesFile(this);

        if (isUserLoggedIn()) {
            setupNavigation();
        } else {
            showWelcomeScreen();
        }
    }

    private boolean isUserLoggedIn() {
        return sharedpreferencesutil.getloginstatus(this);
    }

    private void setupMainScreen() {
        setContentView(R.layout.activity_main);
        setupNavigation();
    }

    private void showWelcomeScreen() {
        setContentView(R.layout.activity_welcome);
    }

    private void setupNavigation() {
        BottomNavigationView navigation = findViewById(R.id.bottomNavigationView);
        
        navigation.setOnItemSelectedListener(item -> {
            Fragment fragment = createFragmentById(item.getItemId());
            return loadFragment(fragment);
        });

        navigation.setSelectedItemId(R.id.discover);
    }

    private Fragment createFragmentById(int itemId) {
        if (itemId == R.id.liked) return new LikedFragment();
        if (itemId == R.id.calendar) return new TravelFragment();
        return new DiscoverFragment(); 
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment == null) return false;

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_container, fragment)
                .commit();
        return true;
    }

    public void logout() {
        SharedPreferencesUtil.setLoginStatus(this, false);
        showWelcomeScreen();
    }

    public void navigateToUserPage() {
        loadFragment(new UserPageFragment());
    }

    public void login() {
        SharedPreferencesUtil.setLoginStatus(this,true);
    }
}