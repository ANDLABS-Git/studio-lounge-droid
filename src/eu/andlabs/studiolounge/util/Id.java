/*
 * Copyright (C) 2012, 2013 by it's authors. Some rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.andlabs.studiolounge.util;

import java.util.Random;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Id {

    
    public static String getName(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx)
                .getString("name", getAccount(ctx, "com.google"));
    }


    public static String getAccount(Context ctx, String type) {
        
        final Account[] accounts = AccountManager.get(ctx).getAccounts();

        for (int i = 0; i < accounts.length; i++) {
            if (accounts[i].type.equalsIgnoreCase(type)) {
                return accounts[i].name;
            }
        } // else fall back
        return "User " + randomString();
    }



    private static String randomString() {
        String randString = "";
        final Random random = new Random();
        for (int i = 0; i < 5; i++) {
            randString += random.nextInt(10);
        }
        return randString;
    }

}
