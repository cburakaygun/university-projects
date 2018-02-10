To start the game, do the following in `Playground`:

`GameArena start.`


When done, `Transcript` will be opened and cleared. Then these 2 lines will be printed:

```
2017 SPRING CMPE260 PROJECT 3
Cemal Burak Aygün   2014400072
```


After this point, the game starts with a dialog which asks `Player1` to select a `Hero` from `Scource`.

Almost everything (_remaining gold_, _items players buy_, _effects of items_, _effects of active/passive skills_, _attack/skill damage of a hero_, _actual attack/skill damage given to a hero_, _occurrence of evasion_, etc.) going on in the game will be printed on `Transcript`. The order of information you see on `Transcript` is compatible with the order of processes that occur in the game.

On Transcript, the information that belongs to a round (turn) is printed between 2 lines of

`- - - - - - - - - - - - - - - - - - - -`


The template of information for a round (turn) is as follows:

```
- - - - - - - - - - - - - - - - - - - -


<TurnInformation>	(Example: Player1's turn!)

<InformationAboutEffectsAppliedBeforeAction>	(Example: Current MP of Magina increased by 50 thanks to the item Battle Fury.)

Player1

<InformationAboutPlayer1>	(information about hero name, HP, MP, last action, remaining potions, skills in cooldown)

Player2

<InformationAboutPlayer2>	(information about hero name, HP, MP, last action, remaining potions, skills in cooldown)

<InformationAboutEffectsAppliedAfterAction>		(Example: Attack damage of Magina: 242)

- - - - - - - - - - - - - - - - - - - -
```


**NOTE:** In my implementation, a potion occupies half of a slot and there CANNOT be 2 different kinds of potions in the same slot.
