# Comment ajouter des textures et des items au mod CTD

Ce guide explique comment ajouter un nouvel **item avec texture** au mod (comme la katana ou l’elbaboss). Le mod utilise **NeoForge 1.21+** : les items ont besoin de **trois types de fichiers** pour que la texture s’affiche correctement.

---

## 1. Structure des dossiers (namespace `ctdmod`)

Tous les assets du mod sont sous :

```
src/main/resources/assets/ctdmod/
├── items/          ← Définition client de l’item (quel modèle utiliser)
├── lang/           ← Traductions (noms d’items, blocs, onglets)
├── models/
│   ├── block/      ← Modèles des blocs
│   └── item/       ← Modèles des items (géométrie + référence texture)
├── blockstates/    ← États des blocs (variants)
└── textures/
    └── item/       ← Images PNG des items
```

Le **mod id** dans le code est `ctdmod` ([CTDMod.MODID](../src/main/java/net/ctd/ctdmod/CTDMod.java)). Les chemins dans les JSON utilisent ce namespace : `ctdmod:item/nom_item`.

---

## 2. Ajouter un nouvel item avec texture (étapes)

On suppose que l’item est déjà enregistré en Java (dans [CTDItems.java](../src/main/java/net/ctd/ctdmod/core/definition/CTDItems.java)) avec l’id `ctdmod:mon_item`.

### Étape 1 : Texture PNG

- **Fichier** : `assets/ctdmod/textures/item/mon_item.png`
- Format : PNG (souvent 16×16 ou 32×32 pour les items simples).
- Dans les modèles, on y fera référence par : `ctdmod:item/mon_item` (sans `.png`).

### Étape 2 : Modèle d’item (`models/item/`)

- **Fichier** : `assets/ctdmod/models/item/mon_item.json`

**Option A – Item 2D simple (une seule couche, comme l’elbaboss) :**

```json
{
  "parent": "minecraft:item/generated",
  "textures": {
    "layer0": "ctdmod:item/mon_item"
  }
}
```

**Option B – Item 3D (géométrie type Blockbench, comme la katana) :**

- Le JSON contient `elements`, `textures`, etc.
- Toute texture utilisée doit pointer vers le mod, ex. : `"0": "ctdmod:item/mon_item"`.
- Ne pas utiliser un autre namespace (ex. `tutomod:...`) sinon la texture ne sera pas trouvée.

### Étape 3 : Définition client de l’item (`items/`) — obligatoire en NeoForge 1.21+

- **Fichier** : `assets/ctdmod/items/mon_item.json`

Ce fichier indique **quel modèle** utiliser pour rendre l’item (inventaire, main, etc.) :

```json
{
  "model": {
    "type": "minecraft:model",
    "model": "ctdmod:item/mon_item"
  }
}
```

Sans ce fichier, le jeu ne lie pas le modèle à l’item et la texture ne s’affiche pas (item violet/noir ou modèle par défaut).

### Étape 4 : Traduction (nom affiché)

- **Fichier** : `assets/ctdmod/lang/en_us.json`
- Ajouter une entrée : `"item.ctdmod.mon_item": "Mon Item"`

---

## 3. Récapitulatif : les 3 fichiers minimum pour un item avec texture

| Rôle | Fichier | Exemple de contenu |
|------|---------|--------------------|
| Image | `textures/item/mon_item.png` | Ton sprite PNG |
| Modèle (géométrie + texture) | `models/item/mon_item.json` | `"layer0": "ctdmod:item/mon_item"` ou `elements` + textures |
| Lien item → modèle | `items/mon_item.json` | `"model": "ctdmod:item/mon_item"` dans un `minecraft:model` |

Plus la traduction dans `lang/en_us.json` pour le nom.

---

## 4. Blocs vs items

- **Bloc (ex. alchemy cauldron)** : le bloc et son item de bloc utilisent les modèles/blockstates et éventuellement des textures vanilla. Pas besoin de fichier dans `items/` pour l’item du bloc.
- **Item seul (ex. katana, elbaboss)** : il faut **à la fois** un modèle dans `models/item/` **et** une définition dans `items/`, sinon la texture ne s’affiche pas.

---

## 5. Vérifications si la texture ne s’affiche pas

1. **Namespace** : dans les JSON, utiliser `ctdmod:item/nom` et non un autre mod (ex. `tutomod:...`).
2. **Dossier `items/`** : le fichier `assets/ctdmod/items/nom_item.json` existe-t-il et pointe-t-il vers `ctdmod:item/nom_item` ?
3. **Chemin texture** : `ctdmod:item/nom` correspond au fichier `assets/ctdmod/textures/item/nom.png`.
4. **Nom cohérent** : le nom utilisé dans `items/`, `models/item/` et `textures/item/` (et dans l’enregistrement Java) doit être le même (ex. `mon_item` partout).

En suivant ces étapes, tu peux ajouter de nouveaux items avec textures de façon reproductible plus tard.
