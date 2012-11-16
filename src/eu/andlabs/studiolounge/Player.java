/*
 * Copyright (C) 2012 ANDLABS. All rights reserved.
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
package eu.andlabs.studiolounge;


public class Player implements LoungeConstants {

	private String mPlayerName;
	private String mHostedGame;

	public Player(String playername) {
		this.mPlayerName = playername;
	}

	public String getPlayername() {

		return this.mPlayerName;
	}

	public String getShortPlayername() {
		if (this.mPlayerName.contains(".")) {
			return mPlayerName.split("\\.")[0]; // Because split expects a regex
		} else if (this.mPlayerName.contains("@")) {
			return mPlayerName.split("@")[0];
		}
		return this.mPlayerName;
	}

	public void setPlayername(String playername) {
		this.mPlayerName = playername;
	}

	public String getHostedGame() {
		return mHostedGame;
	}

	public String getHostedGameName() {
		if (this.mHostedGame == null) {
			return null;
		}
		final String[] split = this.mHostedGame
				.split(PACKAGE_APPNAME_SEPERATOR);
		if (split.length > 1) {
			String[] split2 = split[1].split(".");
			if (split2.length > 1) {
				return split2[split2.length - 1];
			} else {
				return split[1];
			}
		}
		return "";
	}

	public String getHostedGamePackage() {
		if (this.mHostedGame == null) {
			return null;
		}
		final String[] split = this.mHostedGame
				.split(PACKAGE_APPNAME_SEPERATOR);
		if (split.length > 0) {
			return split[0];
		}
		return "";
	}

	public void setHostedGame(String hostedGame) {
		this.mHostedGame = hostedGame;
	}
}
