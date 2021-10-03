# PokerSimulator
COMP90018 assignment2: creating an innovative android app that makes use of sensors, internet, etc.
Online multi player poker simulator

# Ever played those online Majong or Poker games? This one is similar, but without any pre-defined rules, since it's a simulator.

Any number of users can join an open room (online information) and see a deck of poker cards in the middle, a total number of users in the room, on his side his cards in hand, on the opposite side the last active opponent (if any), etc. the UI is as below.

Players take turns to act, like draw cards and stuff, and can undo what's been done. Of course, as a simulator, we must assume people being disciplined.

The initiator of a room can kick out people. That's the only way to get rid of improper player actions.
To make use of sensors, we may locate the user to show a list of nearby users to invite (GPS), shake the phone to shuffle the deck (movement sensor), put the phone upsidedown on the table to show a photo of lecture slides or open a dictionary (light sensor), or whatever you guys can think of.
 
# During a round:
The host of a room decides when to start and end a round. Once a round starts, no one can join in the middle. 
Once started, people can click on take turn to act, shake the phone to re-shuffle, slide to draw cards or to put cards onto the deck or the table with side up or down, take back cards from the table to hand, and redo actions.
After a player finishes his actions, he will click on end turn to let another take turn.
People can chat during playing, maybe even send voice messages (extentsion feature), emojis (extension feature).
 
# Outside of a room:
People can see nearby active players and their status (in game, in room, idle)
See a list of rooms (dont have to be nearby, but may have the choice of filter out nearby ones)
Add friends (extension)
Set themselves online/offline/invisible (extension)
Send global messages to all players (extension)
 
# Architecture:
We can use a P2P architecture without a centualised server for all the game actions.
We can have a server if we want to store user data like friends, or to have a global chat channel. But all core features we need to make this app running can be done without one.