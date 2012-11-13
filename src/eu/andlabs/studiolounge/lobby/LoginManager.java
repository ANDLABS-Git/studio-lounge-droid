package eu.andlabs.studiolounge.lobby;

import java.util.Random;

import eu.andlabs.studiolounge.LoungeConstants;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class LoginManager implements LoungeConstants {

	private static LoginManager sInstance;
	private Context mContext;
	private SharedPreferences mPrefs;

	private LoginManager(Context context) {
		this.mContext = context;
		this.mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
	}

	public static LoginManager getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new LoginManager(context);
		}
		return sInstance;
	}

	public String getUserId() {
		String login = mPrefs.getString(LOGIN, null);
		if (login == null) {
			login = retrieveUserId();
			mPrefs.edit().putString(LOGIN, login).commit();
		}

		return login;
	}

	private String retrieveUserId() {
		final AccountManager accountManager = AccountManager.get(mContext);
		final Account[] accounts = accountManager.getAccounts();

		Account selectedAccount = null;

		if (accounts.length > 0) { // yay, we have accounts
			for (int i = 0; i < accounts.length && selectedAccount == null; i++) {
				if (accounts[i].type.equalsIgnoreCase(ACCOUNT_TYPE_GOOGLE)) {
					selectedAccount = accounts[i];
				}
			}
			if (selectedAccount == null) { // no google account, choose the
											// first one
				selectedAccount = accounts[0];
			}

			return selectedAccount.name + "-" + selectedAccount.type;
		} else { // no account TODO: this has to be unique over all registered
					// apps
			return "User " + randomString();
		}
	}

	private String randomString() {
		String randString = "";
		final Random random = new Random();

		for (int i = 0; i < 5; i++) {
			randString += random.nextInt(10);
		}

		return randString;
	}

}
