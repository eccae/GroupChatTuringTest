
# ![Logo](https://github.com/Eques72/GroupChatTuringTest/blob/main/resources/Logo.png)
# Group Chat Turing Test

Chat with :blond_haired_man: other people and a :robot: bot, discover who is the machine.



---

## [Android Aplication](https://github.com/Eques72/GroupChatTuringTest/blob/main/android) :iphone:
### Usage
> Limitations - In current state this program is not fitted for usage outside safe and local environments. How to adapt this project to broader usage see [Developer](https://github.com/Eques72/GroupChatTuringTest?tab=readme-ov-file#developer).

#### Permissions
App needs `INTERNET` permission. 
#### Android version
Minimum required version: Android 10 (SDK Level 29).
#### Data collection and data usage
This app does not collect or store any user data.
### Developer
#### Setup
> By default network security is off, in case of use outside local environment follow those steps:
- In [Android manifest](https://github.com/Eques72/GroupChatTuringTest/blob/main/android/app/src/main/AndroidManifest.xml) replace line `android:usesCleartextTraffic="true"`  with `android:networkSecurityConfig="@xml/network_security_config"`
- In [Network security config](https://github.com/Eques72/GroupChatTuringTest/blob/main/android/app/src/main/res/xml/network_security_config.xml) remove all occurrences  of `<domain-config cleartextTrafficPermitted="true">` and add your domains.
- In [Repository.kt](https://github.com/Eques72/GroupChatTuringTest/blob/main/android/app/src/main/java/com/adrians/groupchatturing/Repository.kt) you may wish to hardcode variables: `serverPort`,`serverIp`,`serverPrefix` and remove option to change them by disabling part of Settings in [MenuComposables.kt]()
#### Debug logs
Default log tag in application is: MyLogTag

```<domain-config cleartextTrafficPermitted="true">```
### User
#### Setup
- In your android device settings enable installation from unknown sources `"Install unknown apps"`.
- Download .apk file from [here](https://github.com/Eques72/GroupChatTuringTest/releases) to your android device and click on it, installation should begin. After it is completed, GTCC app should appear in app list on your device.
#### Usage 
1. In Main Menu, user has three possible activities. 
   - :gear: button that opens Settings pop up, where one can set up its username and IP address of the server together with opened port.
   - `Create room`  button that leads to becoming a lobby owner for a game session.
   - `Join by code` button opens a pop up where lobby join code must be entered in order to join said lobby, `lobby join code` is visible for everyone who already joined the lobby and lobby owner. 
2. To use application further, follow the UI
3. If you are returned to a Main Menu, it means that either game session has finished or an error occurred and you have been disconnected.

---

## Server
### Usage
#### About .env file:
This file should be located at [/bin](https://github.com/Eques72/GroupChatTuringTest/blob/main/server/bin). File content should look like this:
```
API_KEY="<Your Developer Key to Gemini API>"
```
[API Keys for Gemini can be obtained here](https://ai.google.dev/)

### The server is set to run on port: 
> 12345

## Build and run with docker
`docker compose up --build` 

## Run local build - Release
> [!NOTE]  
> Prefer to build the server with docker over local

```bash build.sh```

## Run local build - Debug
> [!NOTE]  
> Prefer to build the server with docker over local

```bash buildDebug.sh```

## Tips for running on Linux VM
- Enable Bridge Adapter in Network settings (NAT is also doable)
- Command to run Server: `sudo docker-compose up --build`
- `build.sh` might be encoded incorrectly for your system, try command `dos2unix build.sh`
- Might be also needed: `chmod +x` on docker-compose in `/usr/local/bin`
- docker-compose can be obtained from `sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose`

---

### Bug / Feature Request

If you find a bug, please open an issue [here](https://github.com/Eques72/GroupChatTuringTest/issues/new) by including your search query and the expected result.

If you'd like to request a new function, do so by opening an issue [here](https://github.com/Eques72/GroupChatTuringTest/issues/new).

## [License](https://github.com/Eques72/GroupChatTuringTest/blob/main/LICENSE)
MIT © [Eques72](https://github.com/Eques72),  [eccentricfae](https://github.com/eccentricfae)
