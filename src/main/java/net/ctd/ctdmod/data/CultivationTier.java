package net.ctd.ctdmod.data;

public enum CultivationTier
{
  //--------------------------------------------------------------------------------------
  // Niveaux de cultivation (ordre croissant)
  //--------------------------------------------------------------------------------------

  MORTAL_BODY(1, "Mortal Body", 0xFFF7FCFD),              // #F7FCFD
  BODY_REFINING(2, "Body Refining", 0xFFF7FCFD),          // #F7FCFD
  QI_CONDENSATION(3, "Qi Condensation", 0xFFF7FCFD),      // #F7FCFD
  FOUNDATION_ESTABLISHMENT(4, "Foundation Establishment", 0xFFE0F3F8), // #E0F3F8
  GOLDEN_CORE(5, "Golden Core", 0xFFE0F3F8),              // #E0F3F8
  NASCENT_SOUL(6, "Nascent Soul", 0xFFBAD9E9),            // #BAD9E9
  SOUL_FORMATION(7, "Soul Formation", 0xFFBAD9E9),        // #BAD9E9
  MAHAYANA(8, "Mahayana", 0xFFBAD9E9),                    // #BAD9E9
  GRAND_ASCENSION(9, "Grand Ascension", 0xFF70A6BA),      // #70A6BA
  TRIBULATION_TRANSCENDENCE(10, "Tribulation Transcendence", 0xFF70A6BA), // #70A6BA
  EARTH_IMMORTAL(11, "Earth Immortal", 0xFF448AA3),       // #448AA3
  HEAVENLY_IMMORTAL(12, "Heavenly Immortal", 0xFF448AA3), // #448AA3
  GOLDEN_IMMORTAL(13, "Golden Immortal", 0xFF2A788E),     // #2A788E
  IMMORTAL_EMPEROR(14, "Immortal Emperor", 0xFF2A788E),   // #2A788E
  OUTER_GOD(15, "Outer God", 0xFF2A788E),                 // #2A788E
  ANCIENT_GOD(16, "Ancient God", 0xFF1F5F7A),             // #1F5F7A
  DAO_REFINEMENT(17, "Dao Refinement", 0xFF1F5F7A),       // #1F5F7A
  DAO_INTEGRATION(18, "Dao Integration", 0xFF1B4D6B),     // #1B4D6B
  DAO_FUSION(19, "Dao Fusion", 0xFF1B4D6B),               // #1B4D6B
  HEAVENLY_DAO(20, "Heavenly Dao", 0xFF0F3B59);           // #0F3B59

  //--------------------------------------------------------------------------------------
  // Attributs internes
  //--------------------------------------------------------------------------------------

  private final int level;
  private final String name;
  private final int color;

  private static final int NB_LVL = 20;


  private static final CultivationTier[] LEVEL_MAP = new CultivationTier[NB_LVL + 1]; // 21 pour indexer de 0 à 20

  static 
  {
    for (CultivationTier tier : values())
      if (tier.level >= 0 && tier.level < LEVEL_MAP.length)
        LEVEL_MAP[tier.level] = tier;
  }
  //--------------------------------------------------------------------------------------
  // Constructeur
  //--------------------------------------------------------------------------------------

  CultivationTier(int level, String displayName, int argbColor)
  {
    this.level = level;
    this.name = displayName;
    this.color = argbColor;
  }

  //--------------------------------------------------------------------------------------
  // Accès public
  //--------------------------------------------------------------------------------------

  /**
   * Retourne le tier (1..20) correspondant à un niveau numérique 1..20.
   *
   * @param level niveau de tier demandé (1–20)
   * @return l’entrée de {@link CultivationTier} associée
   */
  public static CultivationTier getFromLevel(int level) {
    if (level < 1) return MORTAL_BODY;
    if (level > 20) return HEAVENLY_DAO;
    return LEVEL_MAP[level];
  }

  /**
   * Retourne l’index de tier (1..20) à partir du sous-niveau (1..100).
   * Sous-niveau 1–5 → tier 1, 6–10 → tier 2, etc.
   */
  public static int getTierIndexFromSublevel(int sublevel) {
    if (sublevel < 1) return 1;
    if (sublevel > CultivationSettings.MAX_SUBLEVEL) return NB_LVL;
    return 1 + (sublevel - 1) / CultivationSettings.PALIERS_PER_TIER;
  }

  /**
   * Retourne l’index de palier (1..5) à partir du sous-niveau (1..100).
   * 1 → I, 2 → II, …, 5 → V (répété par tier).
   */
  public static int getPalierIndexFromSublevel(int sublevel) {
    if (sublevel < 1) return 1;
    int n = CultivationSettings.PALIERS_PER_TIER;
    return 1 + (sublevel - 1) % n;
  }

  /** Chiffres romains pour les paliers I à V. */
  private static final String[] PALIER_ROMAN = { "I", "II", "III", "IV", "V" };

  /**
   * Retourne le nom d’affichage complet pour un sous-niveau (ex. "Mortal Body III").
   *
   * @param sublevel sous-niveau 1..100
   * @return le nom du tier + palier (I–V)
   */
  public static String getDisplayNameForSublevel(int sublevel) {
    int tierIndex = getTierIndexFromSublevel(sublevel);
    int palierIndex = getPalierIndexFromSublevel(sublevel);
    CultivationTier tier = getFromLevel(tierIndex);
    String roman = palierIndex >= 1 && palierIndex <= PALIER_ROMAN.length
        ? PALIER_ROMAN[palierIndex - 1]
        : "I";
    return tier.getName() + " " + roman;
  }

  public int getColor() {
    return this.color;
  }

  public String getName() {
    return this.name;
  }
}
