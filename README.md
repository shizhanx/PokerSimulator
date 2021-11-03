# Poker Simulator
COMP90018 assignment 2: creating an innovative android app that makes use of sensors, 
internet, etc.

Online multi player poker simulator

## Table of contents
* [Game Info](#game-information)
* [Requirements](#requirements)
* [Installing](#installation)
* [Running the App](#running-the-app)
* [Solution components](#solution-components)
* [Contributing](#contributing)
* [Authors](#Authors)

## Game Information
Ever played those online Majong or Poker games? 
This one is similar, but without any pre-defined rules, since it's a simulator.

Any number of users can join an open room (online information)
and see a deck of poker cards in the middle, a total number of users in the room,
on his side his cards in hand, on the opposite side the last active opponent (if any), etc.

### Rules

### Host
The host of a room can kick out players, if the host deemed their actions inappropriate .

### Players
Players take turns to act, like draw cards and stuff, and can undo what's been done. 
Of course, as a simulator, we must assume people being disciplined.


### During a round:
The host of a room decides when to start and end a round. Once a round starts, no one can join in the middle. 
Once started, people can click on take turn to act, shake the phone to re-shuffle, slide to draw cards or to put cards onto the deck or the table with side up or down, take back cards from the table to hand, and redo actions.
After a player finishes his actions, he will click on end turn to let another take turn.
People can chat during playing, maybe even send voice messages (extentsion feature), emojis (extension feature).
 
# Outside of a room:
* People can see nearby active players and their status (in game, in room, idle)
* See a list of rooms (dont have to be nearby, but may have the choice of filter out nearby ones)
* Add friends (extension)
* Set themselves online/offline/invisible (extension)
* Send global messages to all players (extension)
 
# Architecture:
We can use a P2P architecture without a centralised server for all the game actions.
We can have a server if we want to store user data like friends, or to have a global chat channel. But all core features we need to make this app running can be done without one.

To make use of sensors, we may locate the user to show a list of nearby users to invite (GPS), shake the phone to shuffle the deck (movement sensor), put the phone upsidedown on the table to show a photo of lecture slides or open a dictionary (light sensor), or whatever you guys can think of.

## Requirement
| Requirement | |
|-|-
| Android Studio |  [https://developer.android.com/studio](https://developer.android.com/studio)

## Installing the application
To install the application, Android studio is required to build the apk and install onto Android devices

1. Open Android Studio
2. Click Open an Existing Project, and select the `PokerSimulator` Folder
3. If running on a real device [click here](#run-on-a-real-device) or if running on virtual emulator
[click here](#run-on-a-virtual-emulator)

### Run on a real device
1. Connect your device to your development machine with a USB cable.
2. Enable USB debugging if the device does not have it enabled. To enable USB debugging:
   1. Open the Settings app. 
   2. If your device uses Android v8.0 or higher, select System. Otherwise, proceed to the next step.
   3. Scroll to the bottom and select About phone.
   4. Scroll to the bottom and tap Build number seven times.
   5. Return to the previous screen, scroll to the bottom, and tap Developer options.
   6. In the Developer options window, scroll down to find and enable USB debugging.
   
3. In Android Studio, select 'app' and the device connected.
   
      ![install](https://github.com/shizhanx/PokerSimulator/blob/readme/img/install_app.png)

4. Click Run ![run](https://developer.android.com/studio/images/buttons/toolbar-run.png)
5. Once the application has been installed, it'll be on the phone with 'PokerSimulator' app.

### Run on a virtual emulator
1. In Android Studio, create an [Android Virtual Device (AVD)](https://developer.android.com/studio/run/managing-avds#createavd)
   that the emulator can use to install and run your app.
2. In the toolbar, select your app from the run/debug configurations drop-down menu.
3. From the target device drop-down menu, select the AVD that you want to run your app on.
4. Click Run ![run](https://developer.android.com/studio/images/buttons/toolbar-run.png)

## Solution components

The solution consists in the following containers:

1. `firebase`
2. `UI`
3. `GameLogic`

To bring up the database:
TODO: Tariq

Debug configurations are included for the following tasks:

TODO: Ki Sian I

## Contributing

Code should adhere to the [Android Style Guide](https://developer.android.com/kotlin/style-guide)
wherever possible. All code should have docstrings.

The `app` folder is where the raw code of project stored in.

The `app/java` folder is the location for all substantive code. Runnable
code should have a debug configuration for easy use and debugging.

The `app/res` folder is the location for all UI resource. 
All newly added resources should not violate any copyright infringement laws.

### Gitflow workflow
We recommend a Gitflow workflow as detailed here:
https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow

#### Publishing changes
`git push origin main`

#### Managing conflicts
If your local history has diverged from the central repository, this is the result of changes
made by another person on the same files you've made changes on.
Git will refuse the request with a rather verbose error message:

`error: failed to push some refs to '/path/to/repo.git' hint: Updates were rejected because the tip of your current branch is behind hint: its remote counterpart. Merge the remote changes (e.g. 'git pull') hint: before pushing again. hint: See the 'Note about fast-forwards' in 'git push --help' for details.`

To proceed you need to pull everyone else's updates into your local
repository and resolve the diverging history:
`git pull --rebase origin main`

If you are working on unrelated features, it’s unlikely that the
rebasing process will generate conflicts. But if it does, Git will
pause the rebase at the current commit and output the following message,
along with some relevant instructions:

`CONFLICT (content): Merge conflict in [XXXX]`

Now run a git status to see where the problem is. Conflicted files will
appear in the Unmerged paths section:

`# Unmerged paths: # (use "git reset HEAD ..." to unstage) # (use "git add/rm ..." as appropriate to mark resolution) # # both modified: `

Edit the file(s) to your liking. Once you're happy with the result, you
can stage the file(s) in the usual fashion and let git rebase do the rest:

`git add git rebase --continue`

Git will move on to the next commit and repeat the process for any other
commits that generate conflicts.

If you get to this point and realize and you have no idea what’s going
on, don’t panic. Just execute the following command and you’ll be
right back to where you started:

`git rebase --abort`

## Authors

* **Shizhan Xu** - [University of Melbourne](https://www.unimelb.edu.au/)
* **Tariq Amjad** - [University of Melbourne](https://www.unimelb.edu.au/)
* **Ki Sian I** - [University of Melbourne](https://www.unimelb.edu.au/)
* **Ibrahim Alamri** - [University of Melbourne](https://www.unimelb.edu.au/)
* **Takemitsu Yamanaka** - [University of Melbourne](https://www.unimelb.edu.au/)