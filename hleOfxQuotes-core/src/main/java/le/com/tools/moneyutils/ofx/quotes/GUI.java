package le.com.tools.moneyutils.ofx.quotes;

import java.util.prefs.Preferences;

/**
 * The Class GUI.
 */
public class GUI {

    private static final Class<le.com.tools.moneyutils.ofx.quotes.GUI> PREFS_CLASS = le.com.tools.moneyutils.ofx.quotes.GUI.class;

    /** The Constant prefs. */
    // TODO: le.com.tools.moneyutils.ofx.quotes.GUI
    public static final Preferences PREFS = Preferences.userNodeForPackage(PREFS_CLASS);

    // PREF_DECIMAL_DIGITS
//    public static final String PREF_FRACTION_DIGITS = "decimalDigits";
//    public static final int PREF_FRACTION_DIGITS_DEFAULT = 4;
    
//    int minimumFractionDigits = 4;
//    int maximumFractionDigits = 8;

    public static final String PREF_MINIMUM_FRACTION_DIGITS = "minimumFractionDigits";
    public static final int PREF_MINIMUM_FRACTION_DIGITS_DEFAULT = 4;

    public static final String PREF_MAXIMUM_FRACTION_DIGITS = "maximumFractionDigits";
    public static final int PREF_MAXIMUM_FRACTION_DIGITS_DEFAULT = 8;
}
