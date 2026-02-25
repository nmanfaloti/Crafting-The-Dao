# Tuto - Stat custom persistante par joueur (NeoForge)

Ce projet inclut maintenant une base prête à l'emploi pour stocker des stats persos par joueur, persistantes entre redémarrages.

## Ce qui est en place

- API de stats: [src/main/java/net/ctd/ctdmod/playerData/PlayerStat.java](src/main/java/net/ctd/ctdmod/playerData/PlayerStat.java)
- Events joueur: [src/main/java/net/ctd/ctdmod/playerData/PlayerStatEvents.java](src/main/java/net/ctd/ctdmod/playerData/PlayerStatEvents.java)

La stat `login_count` est incrémentée automatiquement à chaque connexion.

## Utilisation rapide

### Lire le nombre de connexions

```java
int connexions = PlayerStat.getLoginCount(player);
```

### Ajouter une autre stat custom (int)

```java
int kills = PlayerStat.getIntStat(player, "kills");
PlayerStat.setIntStat(player, "kills", kills + 1);
```

## Pourquoi c'est persistant

- Les données sont stockées dans `player.getPersistentData()` sous la clé `ctd_player_stats`.
- Au clone du joueur (mort/respawn), les stats sont recopiées via l'event `PlayerEvent.Clone`.

## Bonnes pratiques

- Utiliser des clés stables (`"kills"`, `"quests_done"`, etc.).
- Éviter de stocker des objets complexes; privilégier `int`, `double`, `String`, `boolean`.
- Mettre la logique d'update dans un event serveur (ex: kill, craft, login).
